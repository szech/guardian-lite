/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteTagsArticleSet;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class favourites extends ArticleListActivity {
	
    private ArticleSetFactory articleSetFactory;
    private NetworkStatusService networkStatusService;
	private TagListPopulatingService tagListPopulatingService;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
        networkStatusService = SingletonFactory.getNetworkStatusService(this.getApplicationContext());
        tagListPopulatingService = SingletonFactory.getTagListPopulator(this.getApplicationContext());
        
        setContentView(R.layout.favourites);        
        setHeading("Favourites");
        setHeadingColour("#0061A6");
    	showSeperators = true;
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		
		LinearLayout favouritesPane = (LinearLayout) findViewById(R.id.FavouritesPane);
		favouritesPane.removeAllViews();
		populateFavourites();		
	}


	private void populateFavourites() {
		TextView description = (TextView) findViewById(R.id.Description);

		List<ArticleSet> favouriteArticleSets = ((FavouriteTagsArticleSet) articleSetFactory.getFavouritesArticleSet()).getArticleSets();
		if (favouriteArticleSets == null) {
			description.setText("There was a problem loading your favorite sections and tags.");			
			return;
		}
		
		if (!favouriteArticleSets.isEmpty()) {
			LayoutInflater inflater = LayoutInflater.from(this);
			LinearLayout authorList = (LinearLayout) findViewById(R.id.FavouritesPane);
		
			// TODO move to a layout
			LinearLayout tagGroup = new LinearLayout(this.getApplicationContext());
			tagGroup.setOrientation(LinearLayout.VERTICAL);
			tagGroup.setPadding(2, 0, 2, 0);
			
			tagListPopulatingService.populateTags(inflater, networkStatusService.isConnectionAvailable(), tagGroup, favouriteArticleSets, colourScheme, baseFontSize, false);
			authorList.addView(tagGroup);			
			description.setText("The following sections and tags have been marked as favourites.");			
			
		} else {
			description.setText("No favourite sections of tags have been set.\n\nAdd favourites to populate the articles on this screen and to " +
					"indicate which articles should be downloaded for offline viewing.");			
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");
		menu.add(0, MenuedActivity.SECTIONS, 0, "Sections");
		menu.add(0, MenuedActivity.SAVED, 0, "Saved items");
		MenuItem refreshOption = menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");
		enableMenuItemIfConnectionIsAvailable(refreshOption);
	    return true;
	}
	
	@Override
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getFavouritesArticleSet();
	}
	
	@Override
	public void setFontSize() {
		super.setFontSize();
		TextView description = (TextView) findViewById(R.id.Description);
        description.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
        description.setTextColor(colourScheme.getBodytext());
	}
	
}
