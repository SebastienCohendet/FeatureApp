package com.ecn.urbapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ecn.urbapp.R;
import com.ecn.urbapp.db.GpsGeom;
import com.ecn.urbapp.db.LocalDataSource;
import com.ecn.urbapp.db.Project;
import com.ecn.urbapp.syncToExt.Sync;
import com.ecn.urbapp.utils.ConvertGeom;
import com.ecn.urbapp.utils.MathOperation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * 
 * @author Sebastien
 *
 */
public class LoadExternalProjectsActivity extends Activity {

	/**
	 * creating datasource
	 */
	private LocalDataSource datasource;
	/**
	 * Contains all the projects attributes
	 */
	public List<Project> refreshedValues;
	
	/**
	 * COntains all GPSInfo from all Project
	 */
	public List<GpsGeom> allGpsGeom;
	
	/**
	 * Hashmap between unique id of markers and the relative project_id
	 */
	private HashMap<String, Integer> projectMarkers = new HashMap<String, Integer>();
	/**
	 * displayable project list
	 */
	private ListView listeProjects = null;	
    /**
     * The google map object
     */
    private GoogleMap map = null;
    /**
     * The instance of GeoActivity for map activity
     */
    private GeoActivity displayedMap;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loadlocaldb);
        datasource=MainActivity.datasource;
        datasource.open();
        
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
        
        listeProjects = (ListView) findViewById(R.id.listView);
        refreshList();
        
        listeProjects.setOnItemClickListener(selectedProject);
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
               Toast.makeText(MainActivity.baseContext, refreshedValues.get(projectMarkers.get(marker.getId())).toString(), Toast.LENGTH_LONG).show();
   				Intent i = new Intent(getApplicationContext(), LoadExternalPhotosActivity.class);
   				i.putExtra("SELECTED_PROJECT_ID", refreshedValues.get(projectMarkers.get(marker.getId())).getProjectId());
   				
   				ArrayList<LatLng> coordProjet = new ArrayList<LatLng>();

   					for(GpsGeom gg : allGpsGeom){
   		        		if(refreshedValues.get(projectMarkers.get(marker.getId())).getGpsGeom_id()==gg.getGpsGeomsId()){
   		        			coordProjet.addAll(ConvertGeom.gpsGeomToLatLng(gg));
   		        		}
   					}
   				i.putExtra("PROJECT_COORD", ConvertGeom.latLngToGpsGeom(coordProjet));
   				startActivityForResult(i, 1);
   				
            }
        });
    }
    
    protected void onClose() {      
        datasource.close();
    }
    
    /**
     * creating a list of project and loads in the view
     */
    public void refreshList(){      
    	
    	Sync Synchro = new Sync();
    	if (Synchro.getProjectsFromExt())
    		try{
    			refreshedValues=Sync.refreshedValues;
    			allGpsGeom=Sync.allGpsGeom;
    		}
    		catch (Exception e){
    			Log.e("DFHUPLOAD", "Pb de data");
    		}
    	
    	
        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this, android.R.layout.simple_list_item_1, refreshedValues);
        listeProjects.setAdapter(adapter);
        
        /**
         * Put markers on the map
         */
        Integer i = Integer.valueOf(0);
        for (Project enCours:refreshedValues){
			LatLng coordProjet = null;
        	for(GpsGeom gg : allGpsGeom){
        		if(enCours.getGpsGeom_id()==gg.getGpsGeomsId()){
        			coordProjet =  MathOperation.barycenter(ConvertGeom.gpsGeomToLatLng(gg));
        		}
        	}
        	
        	Marker marker = displayedMap.addMarkersColored(i, "Cliquez ici pour charger le projet", coordProjet);
            
        	projectMarkers.put(marker.getId(), i);
        	i++;
        }
   }
    
    /**
     *  get the project selected in listview and show its position on the map 
     */
    
    public OnItemClickListener selectedProject = new OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
			LatLng coordProjet = null;
        	for(GpsGeom gg : allGpsGeom){
        		if(refreshedValues.get(position).getGpsGeom_id()==gg.getGpsGeomsId()){
        			coordProjet =  MathOperation.barycenter(ConvertGeom.gpsGeomToLatLng(gg));
        		}
        	}

			displayedMap = new GeoActivity(false, coordProjet, map);
    		Toast.makeText(getApplicationContext(), coordProjet.toString(), Toast.LENGTH_LONG).show();                  
		}
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
        	if(MainActivity.photo.getUrlTemp() != null){
            //TODO vérifier que l'activité s'est bien terminée
            	finish();
        	}
        }
    }
}
