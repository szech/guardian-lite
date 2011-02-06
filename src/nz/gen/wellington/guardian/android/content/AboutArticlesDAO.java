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

package nz.gen.wellington.guardian.android.content;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleXmlParser;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.LoggingBufferedInputStream;
import android.content.Context;
import android.util.Log;

public class AboutArticlesDAO implements ArticleSource {
	
	static final String TAG = "AboutArticlesDAO";
	static final String ABOUT_ENDPOINT_URL = "http://guardian-lite.appspot.com/about";
	
	private HttpFetcher httpFetcher;
	private ContentApiStyleXmlParser contentXmlParser;
		
	public AboutArticlesDAO(Context context) {
		this.contentXmlParser = new ContentApiStyleXmlParser(context);
		this.httpFetcher = new HttpFetcher(context);
	}
	
	public String getArticleSetUrl() {
		return ABOUT_ENDPOINT_URL;
	}
	
	public ArticleBundle getArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		Log.i(TAG, "Fetching about articles");		
		LoggingBufferedInputStream input = httpFetcher.httpFetch(ABOUT_ENDPOINT_URL, "About");
		if (input != null) {
			ArticleBundle results = contentXmlParser.parseArticlesXml(input, articleCallback);
			if (results != null && !results.getArticles().isEmpty()) {
				String checksum = input.getEtag();
				results.setChecksum(checksum);
				return results;			
			}
		}
		return null;
	}
	
}
