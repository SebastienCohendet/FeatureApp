package com.ecn.urbapp.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.LoadLocalProjectsActivity;
import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.activities.Test;
import com.ecn.urbapp.activities.TestPhoto;
import com.ecn.urbapp.utils.ImageDownloader;
import com.ecn.urbapp.utils.UploadImage;


/**
 * This is the fragment used to make the user choose between the differents type of project.
 * 
 * @author	COHENDET Sébastien
 * 			DAVID Nicolas
 * 			GUILBART Gabriel
 * 			PALOMINOS Sylvain
 * 			PARTY Jules
 * 			RAMBEAU Merwan
 * 			
 */

public class HomeFragment extends Fragment implements OnClickListener{
	
	/**
	 * creating the interface
	 */
	private Button takePhoto = null ;
	private Button loadImage = null ;
	private Button loadLocal = null ;
	private Button test = null ;
	private Button testPhoto = null ;
	private Button loadExt;
	private ImageDownloader imageDownloader = new ImageDownloader();
	private ImageView image;
	private String imageStoredUrl;

	private String[] URLs={
			"http://static.tumblr.com/604c1f8526cf8f5511c6d7a5e32f9abd/u00yntv/2wEmlbf4d/tumblr_static_baby_otter.jpg",
			"http://axemdo.files.wordpress.com/2010/07/loutre1.jpg",
			"http://www.spaycificzoo.com/wp-content/uploads/2011/11/loutre_naine1-300x232.jpg"
	};
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.layout_home, null);

		takePhoto=(Button)v.findViewById(R.id.home_takePicture);
		takePhoto.setOnClickListener(take_picture);
		loadImage=(Button)v.findViewById(R.id.home_loadPicture);
		loadImage.setOnClickListener(loadPhoto);
		
		loadLocal=(Button)v.findViewById(R.id.home_loadLocalProject);
		loadLocal.setOnClickListener(this);
		test=(Button)v.findViewById(R.id.home_test);
		test.setOnClickListener(this);
		
		testPhoto=(Button)v.findViewById(R.id.home_test_photo);
		testPhoto.setOnClickListener(this);
		
		Intent i = getActivity().getIntent();
		
		loadExt = (Button) v.findViewById(R.id.home_loadDistantlProject);
		loadExt.setOnClickListener(load);

		image = (ImageView) v.findViewById(R.id.home_image_loadDistantProject);

		return v;
		
	}
	
	@Override
	public void onClick(View v) {
		Intent i = null;
		int id = v.getId();
        if (id == R.id.home_loadLocalProject) {
                i = new Intent(this.getActivity(), LoadLocalProjectsActivity.class);
                getActivity().startActivityForResult(i,1);
        } else if (id == R.id.home_test_photo) {
			i = new Intent(this.getActivity(), TestPhoto.class);
			startActivity(i);
        } else if (id == R.id.home_test) {
            i = new Intent(this.getActivity(), Test.class);
            startActivity(i);
        }
	}

    private OnClickListener getImage = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
        	imageStoredUrl = imageDownloader.download(URLs[(int) (Math.random()*3)], image, "img"+((int)(Math.random()*3+1))+".png");
        }
    };

    private OnClickListener uploadImage = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
        	image.buildDrawingCache();
        	
        	Bitmap bmap = image.getDrawingCache();
        	Intent i = new Intent(getActivity(), UploadImage.class);
			i.putExtra("imageUrl", imageStoredUrl);
			
			startActivity(i);
        }
    };
    
		// Create a click listener on the take_picture button

	OnClickListener take_picture  = new OnClickListener() {
		@Override
		public void onClick(View v) {

    		// Create a featureapp folder on the tablet if needed

			File folder = new File(Environment.getExternalStorageDirectory(), "featureapp/");
			folder.mkdirs();
			// Get the time to create a unique name for the photo
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String currentDateandTime = sdf.format(new Date());
			// The place where to photo will be saved
			File photo = new File(Environment.getExternalStorageDirectory(),"featureapp/Photo_"+currentDateandTime+".jpg");
			MainActivity.pathImage = photo.getAbsolutePath();
    		// Creating an intent to take a photo and store it in photo
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
	    	getActivity().startActivityForResult(intent, 0);
		}
	};
	// Create a click listener on the load_image button

	OnClickListener loadPhoto = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			// Creating an intent to get an image from the gallery
			Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
			getActivity().startActivityForResult(i, 2);
		}
	};
	
}