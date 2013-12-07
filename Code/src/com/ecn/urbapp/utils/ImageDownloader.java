package com.ecn.urbapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Download a picture from the web and prints it on screen
 * @author Sebastien
 *
 */
public class ImageDownloader {
	/** Path of the directory where image is going to be registered	 */
	private final File path=com.ecn.urbapp.activities.MainActivity.baseContext.getCacheDir();
	/** Name of the file to be registered */
	private String name;

	//TODO Adddescription for javadoc
	/**
	 * Method to launch the download of picture from an url
	 * @param url
	 * @param imageView
	 * @param name name of the file to registered in path
	 * @return
	 */
    public String download(String url, ImageView imageView, String name) {
    		this.name=name;
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
            
            //check if file already exists. If so, displays it !
            File imgFile = new  File(path+"/"+name);
        	if(imgFile.exists()){

        	    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        	    imageView.setImageBitmap(myBitmap);
        	}
        	else
        		task.execute(url); //download image !
            return path+"/"+name;
        }

	//TODO Adddescription for javadoc
	/**
	 * Convert a bitmap image to a file (more convenient to manipule)
	 * Maybe temporaly method !
	 * @param image
	 * @return
	 */
	public File BitmapToFile(Bitmap image){
		//create a file to write bitmap data
		File f = new File(path, name);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Convert bitmap to byte array
		Bitmap bitmap = image;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		//write the bytes in file
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return f;
	}

	//TODO Adddescription for javadoc
static Bitmap downloadBitmap(String url) {
    final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
    final HttpGet getRequest = new HttpGet(url);

    try {
        HttpResponse response = client.execute(getRequest);
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) { 
            Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url); 
            return null;
        }
        
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent(); 
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } finally {
                if (inputStream != null) {
                    inputStream.close();  
                }
                entity.consumeContent();
            }
        }
    } catch (Exception e) {
        // Could provide a more explicit error message for IOException or IllegalStateException
        getRequest.abort();
        Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
    } finally {
        if (client != null) {
            client.close();
        }
    }
    return null;
}
    /* class BitmapDownloaderTask, see below */

//TODO Adddescription for javadoc
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	
	    public BitmapDownloaderTask(ImageView imageView) {
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }
	
	    @Override
	    // Actual download method, run in the task thread
	    protected Bitmap doInBackground(String... params) {
	         // params comes from the execute() call: params[0] is the url.
	         return downloadBitmap(params[0]);
	    }
	
	    @Override
	    // Once the image is downloaded, associates it to the imageView
	    protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }
	
	        if (imageViewReference != null) {
	            ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	            	imageView.setImageBitmap(bitmap);
	                
	                //save file on cache data of the app
	                BitmapToFile(bitmap);
	            }
	        }
	    }
	}
}