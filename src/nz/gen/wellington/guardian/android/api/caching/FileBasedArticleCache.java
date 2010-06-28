package nz.gen.wellington.guardian.android.api.caching;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.Log;

public class FileBasedArticleCache {
	
	private static final String TAG = "FileBasedArticleCache";

	private Context context;
	
	public FileBasedArticleCache(Context context) {
		this.context = context;
	}
	
	
	 public void putArticleSetArticles(ArticleSet articleSet, List<Article> articles, List<Tag> refinements) {
		 ArticleBundle articleBundle = new ArticleBundle(articles, refinements);
		 Log.i(TAG, "Writing to disk: " + articleSet.getName());
		 try {
		 FileOutputStream fos = FileService.getFileOutputStream(context, articleSet.getApiUrl());
		 ObjectOutputStream out = new ObjectOutputStream(fos);
		 out.writeObject(articleBundle);
		 out.close();
		 } catch (IOException ex) {
		 Log.e(TAG, "IO Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		 }
		 }


	public ArticleBundle getArticleSetArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		if (!FileService.isLocallyCached(context, articleSet.getApiUrl())) {
			return null;
		}
		
		final String filepath = FileService.getLocalFilename(articleSet.getApiUrl());
		Log.i(TAG, "Reading from disk: " + filepath);
		try {
			FileInputStream fis = FileService.getFileInputStream(context, articleSet.getApiUrl());

			Log.i(TAG, "Reading from disk: " + filepath);
			ObjectInputStream in = new ObjectInputStream(fis);
			ArticleBundle loaded = (ArticleBundle) in.readObject();
			in.close();
			Log.i(TAG, "Finished reading from disk: " + filepath);
			if (loaded != null) {
				
				if (articleCallback != null) {
					for (Article article : loaded.getArticles()) {
						articleCallback.articleReady(article);
					}
				}
					
				Log.i(TAG, "Loaded " + loaded.getArticles().size() + " articles");
				return loaded;
			}
			return null;

		} catch (IOException ex) {
			Log.e(TAG, "IO Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		}
		return null;
	}
	
	
	public void clear() {
		FileService.clearAll(context);
	}
	
		
	public void clear(ArticleSet articleSet) {
		Log.i(TAG, "Clearing article set: " + articleSet.getName());
		if (FileService.isLocallyCached(context, articleSet.getApiUrl())) {
			FileService.clear(context, articleSet.getApiUrl());
		} else {
			Log.i(TAG, "No local copy to clear:" + articleSet.getApiUrl());
		}
	}


	public DateTime getModificationTime(ArticleSet articleSet) {
		return new FileService().getModificationTime(context, articleSet.getApiUrl());
	}
		
}
