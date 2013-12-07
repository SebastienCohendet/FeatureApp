package com.ecn.urbapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.GeoActivity;
import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.db.Composed;
import com.ecn.urbapp.db.GpsGeom;
import com.ecn.urbapp.db.Project;
import com.ecn.urbapp.utils.GetId;

/**
 * @author	COHENDET Sébastien
 * 			DAVID Nicolas
 * 			GUILBART Gabriel
 * 			PALOMINOS Sylvain
 * 			PARTY Jules
 * 			RAMBEAU Merwan
 * 
 * InformationFragment class
 * 
 * This is the fragment used to define the informations about the project.
 * 			
 */

public class InformationFragment extends Fragment implements OnClickListener{

	private ToggleButton geo;
	
	private Button next;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.layout_information, null);

		geo = (ToggleButton) v.findViewById(R.id.info_button_geo);
		geo.setOnClickListener(this);
		
		next = (Button) v.findViewById(R.id.info_button_next);
		next.setOnClickListener(this);

		return v;
	}
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.info_button_geo:
					if(MainActivity.photo.getGpsGeom_id()==0){
						geo.setChecked(false);
					}
					else{
						geo.setChecked(true);
					}
					Intent i = new Intent(this.getActivity(), GeoActivity.class);
					startActivityForResult(i, 10);
				break;
				case R.id.info_button_next:
					this.getActivity().getActionBar().setSelectedNavigationItem(2);
				break;
			}
		}

		/**
		 * Function called when the fragment stop (i.e. when an other fragment is selected).
		 */
		@Override
		public void onStop(){
			super.onStop();
		    if(!MainActivity.project.isEmpty()){
		    	Project pro = MainActivity.project.get(MainActivity.project.size()-1);
		    	EditText txt = (EditText) getView().findViewById(R.id.info_edit_project);
				pro.setProjectName(txt.getText().toString());
				txt = (EditText) getView().findViewById(R.id.info_edit_description);
				MainActivity.photo.setPhoto_description(txt.getText().toString());
				txt = (EditText) getView().findViewById(R.id.info_edit_author);
				MainActivity.photo.setPhoto_author(txt.getText().toString());
		    }
		    else{
			    Project pro = new Project();
			    Composed comp= new Composed();
			    EditText txt = (EditText) getView().findViewById(R.id.info_edit_project);
			    pro.setProjectName(txt.getText().toString());
			    pro.setProjectId(GetId.Project());
			    
			    txt = (EditText) getView().findViewById(R.id.info_edit_description);
			    MainActivity.photo.setPhoto_description(txt.getText().toString());
			    txt = (EditText) getView().findViewById(R.id.info_edit_author);
			    MainActivity.photo.setPhoto_author(txt.getText().toString());
			    
			    MainActivity.project.add((int)pro.getGpsGeom_id(), pro);
			    comp.setPhoto_id(MainActivity.photo.getPhoto_id());
			    comp.setProject_id(pro.getProjectId());
			    MainActivity.composed.add(comp);
			    
		    }
		}

		/**
		 * Function called when the fragment start.
		 */
		@Override
		public void onStart(){
			super.onStart();
			
			if(MainActivity.photo.getGpsGeom_id()==0){
				geo.setChecked(false);
				next.setVisibility(View.GONE);
			}
			else{
				geo.setChecked(true);
				next.setVisibility(View.VISIBLE);
			}
			
			if(!MainActivity.project.isEmpty()){
				EditText txt = (EditText) getView().findViewById(R.id.info_edit_author);
				txt.setText(MainActivity.photo.getPhoto_author());
				txt = (EditText) getView().findViewById(R.id.info_edit_project);
				txt.setText(MainActivity.project.get(MainActivity.project.size()-1).getProjectName());
				txt = (EditText) getView().findViewById(R.id.info_edit_description);
				txt.setText(MainActivity.photo.getPhoto_description());
				txt = (EditText) getView().findViewById(R.id.info_edit_adress);
				txt.setText("");
			}
			else{
				EditText txt = (EditText) getView().findViewById(R.id.info_edit_author);
			    txt.setText("");
			    txt = (EditText) getView().findViewById(R.id.info_edit_project);
			    txt.setText("");
			    txt = (EditText) getView().findViewById(R.id.info_edit_description);
			    txt.setText("");
			    txt = (EditText) getView().findViewById(R.id.info_edit_adress);
			    txt.setText("");
		    }
		    EditText txt = (EditText) getView().findViewById(R.id.info_edit_adress);
	    	for(GpsGeom gg : MainActivity.gpsGeom){
	    		if(gg.getGpsGeomsId()==MainActivity.photo.getGpsGeom_id()){
				    txt.setText(gg.getAddress());
				    break;
	    		}
	    		else{
	    			txt.setText("");
	    		}
	    	}
		}
}