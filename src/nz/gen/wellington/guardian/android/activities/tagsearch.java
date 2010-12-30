package nz.gen.wellington.guardian.android.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

// TODO should warn that an active connection is needed to search
public class tagsearch extends DownloadProgressAwareActivity implements OnClickListener, FontResizingActivity {
	
	private static final int RESULTS_LOADED = 1;
	private static final int ERROR = 2;
	
	private Button search;
	private PreferencesDAO preferencesDAO;
	private NetworkStatusService networkStatusService;
	
	private List<Tag> searchResults;
	private Map<String, Section> sections;
	private TagSearchResultsHandler tagSearchResultsHandler;
	private SectionDAO sectionDAO;
	private ArticleSetFactory articleSetFactory;
	private Context context;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagsearch);		
		
		preferencesDAO = SingletonFactory.getPreferencesDAO(this.getApplicationContext());
		networkStatusService = new NetworkStatusService(this.getApplicationContext());	// TODO push to factoryy
		articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		sectionDAO = SingletonFactory.getSectionDAO(this.getApplicationContext());
		sections = sectionDAO.getSectionsMap();

		final int baseSize = preferencesDAO.getBaseFontSize();
        setFontSize(baseSize);
		
		search = (Button) findViewById(R.id.Search);        
		search.setOnClickListener(this);
		
		searchResults = new ArrayList<Tag>();		
		tagSearchResultsHandler = new TagSearchResultsHandler(this.getApplicationContext());
		this.context = this.getApplicationContext();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		final int baseSize = preferencesDAO.getBaseFontSize();
        setFontSize(baseSize);
        
		search.setEnabled(networkStatusService.isConnectionAvailable());
		// TODO text warning if no connection is available
		populateSearchResults();
	}

	
	@Override
	public void setFontSize(int baseSize) {
		View view =  findViewById(R.id.Main);
		view.setBackgroundColor(ColourScheme.BACKGROUND);		
	}
	

	@Override
	public void onClick(View src) {
		switch (src.getId()) {		
			case R.id.Search:	{
				
				EditText input = (EditText) findViewById(R.id.Input);
				final String searchTerm = input.getText().toString().trim();
				
				if (!(searchTerm.trim().equals(""))) {					
					hideKeyboard(input);
					Thread loader = new Thread(new TagSearchRunner(searchTerm));
					loader.start();
				}
				return;								
			}
		}
		return;
	}

	
	class TagSearchResultsHandler extends Handler {

		private Context context;
		
		public TagSearchResultsHandler(Context context) {
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case RESULTS_LOADED:
				populateSearchResults();
				return;

			case ERROR:
	        	Toast.makeText(context, "Could not load article", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}
	
	
	class TagSearchRunner implements Runnable {
		
		String searchTeam;
		
		public TagSearchRunner(String searchTerm) {
			this.searchTeam = searchTerm;
		}

		@Override
		public void run() {
			List<Tag> results = fetchTagResults(this.searchTeam);
			if (results != null) {
				searchResults = results;				
				Message msg = new Message();
				msg.what = RESULTS_LOADED;
				tagSearchResultsHandler.sendMessage(msg);				
			} else {
				Message msg = new Message();
				msg.what = ERROR;
				tagSearchResultsHandler.sendMessage(msg);
			}
		}
			
		private List<Tag> fetchTagResults(final String searchTerm) {
			ContentSource api = SingletonFactory.getOpenPlatformApi(context);
			List<Tag> results = api.searchTags(searchTerm, sections);
			return results;
		}

	}


	private void hideKeyboard(EditText input) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
	}

	
	private void populateSearchResults() {
		LinearLayout resultsPane = (LinearLayout) findViewById(R.id.TagList);
		resultsPane.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(this);
		TagListPopulatingService.populateTags(inflater, networkStatusService.isConnectionAvailable(), resultsPane, articleSetFactory.getArticleSetsForTags(searchResults), this.getApplicationContext());
	}
	
}
