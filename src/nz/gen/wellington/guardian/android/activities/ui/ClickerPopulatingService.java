package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.section;
import nz.gen.wellington.guardian.android.activities.tag;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SearchResultsArticleSet;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import android.view.View;
import android.widget.TextView;

public class ClickerPopulatingService {
		
	public static void populateTagClicker(ArticleSet articleSet, View tagView, boolean contentIsAvailable, int contentAvailableColour, int contentUnavailableColour) {
		TextView tagName = (TextView) tagView.findViewById(R.id.TagName);
		setColour(tagName, contentIsAvailable, contentAvailableColour, contentUnavailableColour);
		populateClicker(articleSet, tagView, contentIsAvailable);
		
	}

	private static void populateClicker(ArticleSet articleSet, View tagView,
			boolean contentIsAvailable) {
		if (contentIsAvailable) {		
			// TODO suggests article sets should have knowledge about which activity renders them?
			if (articleSet instanceof SectionArticleSet) {				
				ArticleSetClicker clicker = new ArticleSetClicker(articleSet, section.class);
				tagView.setOnClickListener(clicker);
				
			} else if (articleSet instanceof SearchResultsArticleSet) {
				tagView.setOnClickListener(new SearchTermClicker(((SearchResultsArticleSet) articleSet).getSearchTerm()));
				
			} else if (contentIsAvailable) {
				ArticleSetClicker clicker = new ArticleSetClicker(articleSet, tag.class);
				tagView.setOnClickListener(clicker);
			}
			
		}
	}

	private static void setColour(TextView titleText, boolean contentIsAvailable, int contentAvailableColour, int contentUnavailableColour) {
		if (contentIsAvailable) {
			titleText.setTextColor(contentAvailableColour);			
		} else {
			titleText.setTextColor(contentUnavailableColour);			
		}
	}
	
}
