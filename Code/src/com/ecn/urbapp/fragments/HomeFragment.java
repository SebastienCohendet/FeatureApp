package com.ecn.urbapp.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.Toast;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.LoadLocalProjectsActivity;
import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.activities.Test;
import com.ecn.urbapp.activities.TestPhoto;
import com.ecn.urbapp.utils.Cst;
import com.ecn.urbapp.utils.ImageDownloader;
import com.ecn.urbapp.utils.Utils;


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
	 * Button launching the native photo app
	 */
	private Button takePhoto;
	private Button loadImage;
	/**
	 * Button launching the loadLocalProject activity
	 */
	private Button loadLocal;
	private Button test;
	private Button testPhoto;
	private Button downloadImage;
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
		takePhoto.setOnClickListener(this);
		loadImage=(Button)v.findViewById(R.id.home_loadPicture);
		loadImage.setOnClickListener(this);
		
		loadLocal=(Button)v.findViewById(R.id.home_loadLocalProject);
		loadLocal.setOnClickListener(this);
		test=(Button)v.findViewById(R.id.home_test);
		test.setOnClickListener(this);
		
		testPhoto=(Button)v.findViewById(R.id.home_test_photo);
		testPhoto.setOnClickListener(this);
		
		downloadImage = (Button) v.findViewById(R.id.home_loadDistantlProject);
		downloadImage.setOnClickListener(this);
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		Intent i;
		switch(v.getId()){
			case R.id.home_takePicture:
				Utils.showToast(MainActivity.baseContext, "Lancement de l'appareil photo", Toast.LENGTH_SHORT);
				File folder = new File(Environment.getExternalStorageDirectory(), "featureapp/");
				folder.mkdirs();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
				String currentDateandTime = sdf.format(new Date());
				File photo = new File(Environment.getExternalStorageDirectory(),"featureapp/Photo_"+currentDateandTime+".jpg");
				MainActivity.photo.setUrlTemp(photo.getAbsolutePath());
				
				i= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		    	getActivity().startActivityForResult(i, Cst.CODE_TAKE_PICTURE);
			break;
			case R.id.home_loadPicture:
				Utils.showToast(MainActivity.baseContext, "Lancement de la gallerie", Toast.LENGTH_SHORT);
				i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				getActivity().startActivityForResult(i, Cst.CODE_LOAD_PICTURE);
			break;
			case R.id.home_loadLocalProject:
				i = new Intent(this.getActivity(), LoadLocalProjectsActivity.class);
				getActivity().startActivityForResult(i,Cst.CODE_LOAD_LOCAL_PROJECT);
			break;
			case R.id.home_test_photo:
				i = new Intent(this.getActivity(), TestPhoto.class);
				startActivity(i);
			break;
			case R.id.home_test:
				i = new Intent(this.getActivity(), Test.class);
				startActivity(i);
			break;
			case R.id.home_loadDistantlProject:
				imageStoredUrl = imageDownloader.download(URLs[(int) (Math.random()*3)], image, "img"+((int)(Math.random()*3+1))+".png");
			break;
		}
	}
}