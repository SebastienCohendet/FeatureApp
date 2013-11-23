package com.ecn.urbapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ecn.urbapp.R;
import com.ecn.urbapp.db.LocalDataSource;
import com.ecn.urbapp.db.Photo;
import com.ecn.urbapp.utils.CustomListViewAdapter;
import com.ecn.urbapp.utils.RowItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LoadLocalPhotosActivity extends Activity{

	/**
	 * creating datasource
	 */
	private LocalDataSource datasource;
	/**
	 * Contains all the projects attributes
	 */
	private List<Photo> refreshedValues;

	/**
	 * Hashmap between unique id of markers and the relative project_id
	 */
	private HashMap<String, Integer> photosMarkers = new HashMap<String, Integer>();
	/**
	 * displayable photos list
	 */
	private ListView listePhotos = null;	
	/**
	 * The google map object
	 */
	private GoogleMap map = null;
	/**
	 * The instance of GeoActivity for map activity
	 */
	GeoActivity displayedMap;
	/**
	 * The button for switching to satellite view
	 */
	private Button satellite = null;

	/**
	 * The button for switching to plan view
	 */
	private Button plan = null;

	/**
	 * The button for switching to hybrid view
	 */
	private Button hybrid = null;

	//TODO add description for javadoc
	private ArrayList<RowItem> rowItems;


	//TODO add description for javadoc
	long project_id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_loadlocalphotos);
		datasource=MainActivity.datasource;
		datasource.open();

		/**
		 * extras that countains the project_id
		 */

		project_id = getIntent().getExtras().getLong("SELECTED_PROJECT_ID");

		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		displayedMap = new GeoActivity(true, GeoActivity.defaultPos, map);

		/**
		 * Define the listeners for switch satellite/plan/hybrid
		 */
		satellite = (Button)findViewById(R.id.satellite);
		plan = (Button)findViewById(R.id.plan);
		hybrid = (Button)findViewById(R.id.hybrid);

		satellite.setOnClickListener(displayedMap.toSatellite);
		plan.setOnClickListener(displayedMap.toPlan);
		hybrid.setOnClickListener(displayedMap.toHybrid);

		listePhotos = (ListView) findViewById(R.id.listViewPhotos);
		refreshListPhoto();


		listePhotos.setOnItemClickListener(selectedPhoto);
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Toast.makeText(MainActivity.baseContext, refreshedValues.get(photosMarkers.get(marker.getId())).toString(), Toast.LENGTH_LONG).show();

				//TODO do a better way to have the path !
				MainActivity.pathImage=Environment.getExternalStorageDirectory()+"/featureapp/"+refreshedValues.get(photosMarkers.get(marker.getId())).getPhoto_url();
				finish();

			}
		});
	}

	protected void onClose() {      
		datasource.close();
	}

	//TODO add description for javadoc
	/**
	 * loading the different projects of the local db
	 * @return
	 */
	public List<com.ecn.urbapp.db.Photo> recupPhoto() {

		//List<com.ecn.urbapp.db.Photo> values = this.datasource.getAllPhotos();

		//TODO CATCH EXCEPTION

		List<com.ecn.urbapp.db.Photo> values = this.datasource.getAllPhotolinkedtoProject(project_id);
		return values;

	}

	/**
	 * creating a list of project and loads in the view
	 */
	public void refreshListPhoto(){      

		refreshedValues = recupPhoto();

		rowItems = new ArrayList<RowItem>();
		for (Photo image:refreshedValues) {
			RowItem item = new RowItem(Environment.getExternalStorageDirectory()+"/featureapp/"+image.getPhoto_url(), image.getPhoto_author(), image.getPhoto_description());
			rowItems.add(item);
		}

		CustomListViewAdapter adapter = new CustomListViewAdapter(this,
				R.layout.layout_photolistview, rowItems);
		listePhotos.setAdapter(adapter);

		//ArrayAdapter<com.ecn.urbapp.db.Photo> adapterPhoto = new ArrayAdapter<com.ecn.urbapp.db.Photo>(this, android.R.layout.simple_expandable_list_item_1,refreshedValues);
		//listePhotos.setAdapter(adapterPhoto);  

		/**
		 * Put markers on the map
		 */
		Integer i = Integer.valueOf(0);
		for (Photo enCours:refreshedValues){
			String[] coord = enCours.getExt_GpsGeomCoord().split("//");
			LatLng coordPhoto = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
			Marker marker = map.addMarker(new MarkerOptions()
			.position(coordPhoto)
			.title("Cliquez ici pour valider cette photo"));

			photosMarkers.put(marker.getId(), i);
			i++;
		}
	}    

	/**
	 *  get the project selected in listview and show its position on the map 
	 */
	public OnItemClickListener selectedPhoto = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {

			String[] coord = refreshedValues.get(position).getExt_GpsGeomCoord().split("//");
			LatLng coordPhoto = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
			displayedMap = new GeoActivity(false, coordPhoto, map);
			Toast.makeText(getApplicationContext(), coordPhoto.toString(), Toast.LENGTH_LONG).show();                  
		}
	};
}
