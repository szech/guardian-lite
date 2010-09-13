package nz.gen.wellington.guardian.android.contentupdate;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.contentupdate.tasks.UpdateArticleSetTask;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ContentUpdateService extends Service {
	
    public static final String CONTROL = "nz.gen.wellington.guardian.android.services.CONTENT_UPDATE";
	
    public static final String TASK_START = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_START";
    public static final String TASK_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_COMPLETION";
    public static final String BATCH_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_BATCH_COMPLETION";
    
    public static final int RUNNING = 1;
    public static final int CLEANUP = 2;
    public static final int STOPPED = 3;
        
	private static final String TAG = "ContentUpdateService";
    
    public static final int UPDATE_COMPLETE_NOTIFICATION_ID = 1;

    
    private Thread thread;
    private InternalRunnable internalRunnable;
 
   
	private final IBinder mBinder = new ContentUpdateServiceBinder();

	
    @Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent != null && intent.getAction() != null && intent.getAction().equals("RUN")) {
			Log.i(TAG, "Got start command");
			this.start();
		}
	}

        
    public void start() {
    	Log.i(TAG, "Queuing tasks");
    	queueUpdateTasks();
    	internalRunnable = new InternalRunnable(this, (NotificationManager)getSystemService(NOTIFICATION_SERVICE));
    	thread = new Thread(internalRunnable);
    	thread.setDaemon(true);
    	thread.start();
    	internalRunnable.start();
	}

    
	public void stop() {
		internalRunnable.stop();
		//running = false;
	}
	
	
	public int getStatus() {
		if (internalRunnable != null) {
			return internalRunnable.getStatus();
		}
		return STOPPED;
	}
	
	
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
	
	public class ContentUpdateServiceBinder extends Binder {
		public ContentUpdateService getService() {
			return ContentUpdateService.this;
		}
	}
	
	
	private void queueUpdateTasks() {
		TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue(this.getApplicationContext());
		FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO = ArticleDAOFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());
		
		taskQueue.addArticleTask(new UpdateArticleSetTask(this.getApplicationContext(), new TopStoriesArticleSet()));
		
		List<Section> favouriteSections = favouriteSectionsAndTagsDAO.getFavouriteSections(); 
		List<Tag> favouriteTags = favouriteSectionsAndTagsDAO.getFavouriteTags();
		if (!favouriteSections.isEmpty() || !favouriteTags.isEmpty()) {
			queueSections(taskQueue, favouriteSections);
			queueTags(taskQueue, favouriteTags);
			taskQueue.addArticleTask(new UpdateArticleSetTask(this.getApplicationContext(), new FavouriteStoriesArticleSet(favouriteSections, favouriteTags)));
		}
	}
	
	
	private void queueTags(TaskQueue taskQueue, List<Tag> tags) {
		if (tags != null) {
			for (Tag tag : tags) {
				taskQueue.addArticleTask(new UpdateArticleSetTask(this
						.getApplicationContext(), new TagArticleSet(tag)));
			}
		}
	}

	
	private void queueSections(TaskQueue taskQueue, List<Section> sections) {
		if (sections != null) {
			for (Section section : sections) {
				UpdateArticleSetTask articleTask = new UpdateArticleSetTask(this.getApplicationContext(), new SectionArticleSet(section));
				taskQueue.addArticleTask(articleTask);
			}
		}
	}
	
}