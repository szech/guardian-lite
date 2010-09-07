package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class main extends ArticleListActivity {
		
	public main() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        hideHeading();
    	updateArticlesHandler = new UpdateArticlesHandler(this, getArticleSet());
    	showSeperators = true;
    	showMainImage = false;
	}
	
	
	@Override
	protected ArticleSet getArticleSet() {
		return new TopStoriesArticleSet();		
	}
	
	
	@Override
	protected String getRefinementDescription(String refinementType) {
		return null;
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Favourites");
	    menu.add(0, 2, 0, "Sections");
	    menu.add(0, 6, 0, "About");
	    menu.add(0, 5, 0, "Refresh");
	    menu.add(0, 3, 0, "Sync");
	    menu.add(0, 4, 0, "Settings");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case 1: 	    	
	    	switchToFavourites();
	    	return true;	 
	    case 2:
	    	switchToSections();
	    	return true;	 
	    case 3: 	    	
	    	swichToSync();
	        return true;
	    case 4:
	    	switchToPreferences();
	    	return true;
	    case 5:
			refresh();
			return true;	
	    case 6:
	    	showAbout();
	    }
	    return false;
	}


	private void showAbout() {
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.about_dialog);
		dialog.show();
		
		ImageView heading = (ImageView) dialog.findViewById(R.id.KingsPlace);
		heading.setImageResource(R.drawable.kingsplace);
		
		TextView description = (TextView) dialog.findViewById(R.id.Description);		
		description.setText("This unofficial application was developed by Tony McCrae of Eel Pie Consulting Limited.\n\n" +
				"Articles are retreived from the Guardian's RSS feeds. Tag information is supplied by the Guardian Content API.\n\n" +
				"For more information see:\nhttp://eelpieconsulting.co.uk/guardianlite\n\n" +
				"Application � 2010 Eel Pie Consulting Limited\n\n" +
				"Content � 2010 Guardian News and Media Limited");
				
		ImageView image = (ImageView) dialog.findViewById(R.id.GuardianLogo);
		image.setImageResource(R.drawable.poweredbyguardian);
		dialog.setTitle(null);
	}
	
}