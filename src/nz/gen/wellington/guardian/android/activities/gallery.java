package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.PictureClicker;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.MediaElement;
import nz.gen.wellington.guardian.android.model.Picture;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class gallery extends ContentRenderingActivity {
	
	private static final int THUMBNAILS_PER_ROW = 3;
	
	private GalleryImageUpdateHandler galleryImageUpdateHandler;
	private TableRow currentRow;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	galleryImageUpdateHandler = new GalleryImageUpdateHandler();
	}

	
	@Override
	protected int getLayout() {
		return R.layout.gallery;
	}
	
	
	@Override
	public void populateContent(Article article, int bodytextColour, int headlineColour) {
		super.populateContent(article, bodytextColour, headlineColour);
		
		
		if (!article.getMediaElements().isEmpty()) {			
			GalleryImageLoader galleryImageLoader = new GalleryImageLoader(imageDAO, article.getMediaElements());
			Thread loader = new Thread(galleryImageLoader);
			loader.start();
		}	
	}

		
	private void populateGalleryPicture(Picture picture) {
		ImageView imageView = new ImageView(this);
		Bitmap image = images.get(picture.getThumbnail());
		imageView.setImageBitmap(image);
		imageView.setPadding(5, 5, 5, 5);
		imageView.setOnClickListener(new PictureClicker(picture));
		
		TableLayout thumbnails = (TableLayout) findViewById(R.id.GalleryThumbnails);
		if (currentRow == null || currentRow.getChildCount() >= THUMBNAILS_PER_ROW) {
			currentRow =  new TableRow(this.getApplicationContext());
			thumbnails.addView(currentRow);
		}
		currentRow.addView(imageView);
	}
	
	
	class GalleryImageLoader implements Runnable {

		private static final String TAG = "GalleryImageLoader"
			;
		private ImageDAO imageDAO;
		private List<MediaElement> mediaElements;
		
		public GalleryImageLoader(ImageDAO imageDAO, List<MediaElement> mediaElements) {
			this.imageDAO = imageDAO;
			this.mediaElements = mediaElements;
		}
		
		@Override
		public void run() {
			Log.i(TAG, "Running gallery image loader with " + mediaElements + " media elements");
			for (MediaElement mediaElement : mediaElements) {
				
				if (mediaElement != null && mediaElement.isPicture() && mediaElement.getThumbnail() != null) {					
					Picture picture = new Picture(mediaElement.getThumbnail(), mediaElement.getFile(), mediaElement.getCaption());
					Bitmap image = imageDAO.getImage(picture.getThumbnail());
					if (image != null) {
						images.put(picture.getThumbnail(), image);
						sendThumbnailAvailableForPictureMessage(picture);
					}					
				}
			}
		}	

		private void sendThumbnailAvailableForPictureMessage(Picture picture) {
			Message msg = new Message();
			msg.what = GalleryImageUpdateHandler.GALLERY_IMAGE_AVAILABLE;
			
			Bundle bundle = new Bundle();
			bundle.putSerializable("picture", picture);			
			msg.setData(bundle);	
			galleryImageUpdateHandler.sendMessage(msg);
		}
		
	}
	
	
	class GalleryImageUpdateHandler extends Handler {
		
		public static final int GALLERY_IMAGE_AVAILABLE = 1;

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {	   
			    case GALLERY_IMAGE_AVAILABLE:			    	
			    final Picture picture = (Picture) msg.getData().get("picture");
			    populateGalleryPicture(picture);
			}
		}
	}
	
}
