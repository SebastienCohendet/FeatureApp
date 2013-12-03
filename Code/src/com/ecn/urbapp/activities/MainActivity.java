package com.ecn.urbapp.activities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.ecn.urbapp.R;
import com.ecn.urbapp.db.Composed;
import com.ecn.urbapp.db.Element;
import com.ecn.urbapp.db.ElementType;
import com.ecn.urbapp.db.GpsGeom;
import com.ecn.urbapp.db.LocalDataSource;
import com.ecn.urbapp.db.Material;
import com.ecn.urbapp.db.Photo;
import com.ecn.urbapp.db.PixelGeom;
import com.ecn.urbapp.db.Project;
import com.ecn.urbapp.dialogs.ConfirmPhotoDialogFragment;
import com.ecn.urbapp.fragments.CharacteristicsFragment;
import com.ecn.urbapp.fragments.HomeFragment;
import com.ecn.urbapp.fragments.InformationFragment;
import com.ecn.urbapp.fragments.SaveFragment;
import com.ecn.urbapp.fragments.ZoneFragment;
import com.ecn.urbapp.listener.MyTabListener;
import com.ecn.urbapp.utils.ConnexionCheck;

/**
 * @author	COHENDET Sébastien
 * 			DAVID Nicolas
 * 			GUILBART Gabriel
 * 			PALOMINOS Sylvain
 * 			PARTY Jules
 * 			RAMBEAU Merwan
 * 
 * MainActivity class
 * 
 * This is the main activity of the application.
 * It contains an action bar filled with the different fragments
 * 			
 */

public class MainActivity extends Activity {

	/**
	 * Attribut represent the action bar of the application
	 */
	private ActionBar bar;
	
	/**
	 * Attribut representing the local database
	 */
	public static LocalDataSource datasource;

	
	/**
	 * BaseContext to get the static context of app anywhere (for file)
	 */
    public static Context baseContext;

	//TODO add description for javadoc
	private static Builder alertDialog;

	/**
	 * Link to ask google to create a specific Connexion code to chck if there is no portal between android and server
	 */
    public static final String CONNECTIVITY_URL="http://clients3.google.com/generate_204";
    
    /**
     * Server address
     */
    public static String serverURL="http://192.168.177.1/";
    
    /**
	 * Attributs for the project information
	 */
	//TODO add description for javadoc
	public static String pathImage=null;
	//TODO add description for javadoc
	public static String pathTampon=null;
	//TODO add description for javadoc
	public static File sphoto=null;
	//TODO add the set of this bool into each function loading a photo
	public static boolean isPhoto=false;
	//TODO add description for javadoc
	public static boolean start = true;
	//TODO add description for javadoc
	public static boolean local = false;

	//TODO add description for javadoc
	private Vector<Fragment> fragments=null;
	
	/**ArrayList for the elements of the database**/

	public static ArrayList<Composed> composed=null;
	public static ArrayList<Element> element=null;
	public static ArrayList<ElementType> elementType=null;
	public static ArrayList<GpsGeom> gpsGeom=null;
	public static ArrayList<Material> material=null;
	public static ArrayList<PixelGeom> pixelGeom=null;
	public static ArrayList<Project> project=null;
	public static boolean projectSet = false;
	public static Photo photo=null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		
		composed = new ArrayList<Composed>();
		element = new ArrayList<Element>();
		elementType = new ArrayList<ElementType>();
		gpsGeom = new ArrayList<GpsGeom>();
		material = new ArrayList<Material>();
		pixelGeom = new ArrayList<PixelGeom>();
		project = new ArrayList<Project>();
		photo = new Photo();
		
		
		fragments=new Vector<Fragment>();
		
		//Setting the Context of app
		baseContext = getBaseContext();
		
		alertDialog = new AlertDialog.Builder(MainActivity.this);
		isInternetOn();
		
		//Setting the Activity bar
		bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayHomeAsUpEnabled(true);
		
		//Setting the Context of app
		baseContext = getBaseContext();
		
		//initialization of the local database
		datasource = new LocalDataSource(this);
		
		//Setting of the different tab of the bar
		
		//Home tab
		Tab tabHome =  bar.newTab();
		tabHome.setText(R.string.homeFragment);
		HomeFragment home = new HomeFragment();
		tabHome.setTabListener(new MyTabListener(home, this));
		bar.addTab(tabHome);
		fragments.add(home);
		
		//Information tab
		Tab tabInformation =  bar.newTab();
		tabInformation.setText(R.string.informationFragment);
		InformationFragment information = new InformationFragment();
		tabInformation.setTabListener((new MyTabListener(information, this)));
		bar.addTab(tabInformation);
		fragments.add(information);
		
		//Zone tab
		Tab tabZone =  bar.newTab();
		tabZone.setText(R.string.zoneFragment);
		ZoneFragment zone = new ZoneFragment();
		tabZone.setTabListener(new MyTabListener(zone, this));
		bar.addTab(tabZone);
		fragments.add(zone);
		
		//Definition tab
		Tab tabDefinition =  bar.newTab();
		tabDefinition.setText(R.string.definitionFragment);
		CharacteristicsFragment definition = new CharacteristicsFragment();
		tabDefinition.setTabListener(new MyTabListener(definition, this));
		bar.addTab(tabDefinition);
		fragments.add(definition);
		
		//Save tab
		Tab tabSave =  bar.newTab();
		tabSave.setText(R.string.saveFragment);
		SaveFragment save = new SaveFragment();
		tabSave.setTabListener(new MyTabListener(save, this));
		bar.addTab(tabSave);
		fragments.add(save);
		
		//TODO coordinate with the remote database
		datasource.open();
		
		datasource.createElementTypeInDB("Toit");
		datasource.createElementTypeInDB("Façade");
		datasource.createElementTypeInDB("Sol");
		
		datasource.getAllElementType();

		datasource.createMaterialInDB("Acier");
		datasource.createMaterialInDB("Ardoises");
		datasource.createMaterialInDB("Bois");
		datasource.createMaterialInDB("Béton");
		datasource.createMaterialInDB("Cuivre");
		datasource.createMaterialInDB("Enrobé");
		datasource.createMaterialInDB("Goudron");
		datasource.createMaterialInDB("Herbe");
		datasource.createMaterialInDB("Terre");
		datasource.createMaterialInDB("Tuiles");
		datasource.createMaterialInDB("Verre");

		datasource.getAllMaterial();
		
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu, this adds items to the action bar if it is present.
		MenuInflater inflater =	getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Method to check if internet is available (and no portal !)
	 */
	public final void isInternetOn() {

		ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean mobile = false;
        
        try {
         mobile=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        }
        catch (NullPointerException e){
        	mobile=false;
        }
        boolean internet=wifi|mobile;
        if (internet)
        	 new ConnexionCheck().Connectivity();
	}

	/**
	 * Method if no internet connectivity to print a Dialog.
	 */
	public static void errorConnect() {
		alertDialog.setTitle("Pas de connexion internet de disponible. Relancer l'application, une fois internet fonctionnel");
		alertDialog.show();		
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
            	confirm();
            	//Setting the photo path from the pathImage
            	MainActivity.photo.setPhoto_url(pathImage.split("/")[pathImage.split("/").length-1]);
            	MainActivity.photo.setPhoto_id(1);
                getActionBar().setSelectedNavigationItem(1);
                MainActivity.isPhoto=true;
            }
        }
        if (requestCode == 1) {
            if (pathImage != null) {
        	//TODO check that this is not a crash
            	MainActivity.local=true;
            	confirm();
                getActionBar().setSelectedNavigationItem(2);
                MainActivity.isPhoto=true;
                datasource.instanciateAllElement();
                datasource.instanciateAllGpsGeom();
                datasource.instanciateAllProject();
                datasource.instanciateAllpixelGeom(); //load pixelGeom linked to the photo in the relative public static arrayList
                MainActivity.projectSet=true;
                Log.w("papa","p");
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
            	confirm();
            	//Setting the photo path
            	String url = getRealPathFromURI(baseContext, data.getData());
            	MainActivity.photo.setPhoto_url(url.split("/")[url.split("/").length-1]);
            	MainActivity.photo.setPhoto_id(1);
            	try {
            		if(!url.equals(Environment.getExternalStorageDirectory()+"/featureapp/"+MainActivity.photo.getPhoto_url())){
            			copy(new File(url), new File(Environment.getExternalStorageDirectory()+"/featureapp/"+MainActivity.photo.getPhoto_url()));
            		}
            	} catch (IOException e) {
					e.printStackTrace();
				}
                getActionBar().setSelectedNavigationItem(1);
                MainActivity.isPhoto=true;
            }
        }
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                getActionBar().setSelectedNavigationItem(1);
            }
        }
    }

	//TODO add description for javadoc
	public void confirm(){
		if(MainActivity.pathTampon!=null){
			ConfirmPhotoDialogFragment typedialog = new ConfirmPhotoDialogFragment();
			typedialog.show(getFragmentManager(), "CharacteristicsDialogFragment");
		}
	}

	//TODO add description for javadoc
	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try{ 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		}finally{
		    if (cursor != null) {
		    	cursor.close();
		    }
		}
	}
	
	/**
	 * Function called when the back button of the screen is called. It will display the previous fragment.
	 */
	@Override
	public void onBackPressed(){

		int i=0;
		for(Fragment f : fragments){
			if(f.isVisible()){
				break;
			}
			i++;
		}
		if(i>0){
			getActionBar().selectTab(getActionBar().getTabAt(i-1));
		}

	}
	
	public void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
}