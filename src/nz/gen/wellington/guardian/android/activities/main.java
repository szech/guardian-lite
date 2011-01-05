package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class main extends ArticleListActivity {
		
	private ArticleSetFactory articleSetFactory;


	public main() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
        hideHeading();
        showSeperators = true;
    	showMainImage = false;
	}
	
	
	@Override
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getTopStoriesArticleSet();
	}
	
	
	@Override
	protected String getRefinementDescription(String refinementType) {
		return null;
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.FAVOURITES, 0, "Favourites");
	    menu.add(0, MenuedActivity.SECTIONS, 0, "Sections");
	    menu.add(0, MenuedActivity.ABOUT, 0, "About");
	    menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");
	    menu.add(0, MenuedActivity.SYNC, 0, "Sync");
	    menu.add(0, MenuedActivity.SETTING, 0, "Settings");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case MenuedActivity.FAVOURITES: 	    	
	    	switchToFavourites();
	    	return true;	 
	    case MenuedActivity.SECTIONS:
	    	switchToSections();
	    	return true;	 
	    case MenuedActivity.SYNC: 	    	
	    	swichToSync();
	        return true;
	    case MenuedActivity.SETTING:
	    	switchToSettings();
	    	return true;
	    case MenuedActivity.REFRESH:
			refresh();
			return true;
	    case MenuedActivity.ABOUT:
	    	switchToAbout();
	    	return true;
	    }
	    return false;
	}
	
}