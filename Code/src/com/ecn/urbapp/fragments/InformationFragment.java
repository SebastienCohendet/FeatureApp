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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.GeoActivity;
import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.db.Composed;
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

public class InformationFragment extends Fragment implements OnClickListener, OnCheckedChangeListener{

	/**
	 * Button launching the geolocalisation activity. It change of state once the photo is linked with a GpsGeom.
	 */
	private ToggleButton geo;
	
	/**
	 * Button launching the next fragment. It only displyed when the photo is linked with a GpsGeom.
	 */
	private Button next;
	
	/**
	 * listView of the project element
	 */
	private RelativeLayout RelLayout;
	
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
		
		RelLayout = (RelativeLayout) v.findViewById(R.id.info_layout_listView);

		for(Project p : MainActivity.project){
			CheckBox cb = new CheckBox(getActivity());
			cb.setText(p.getProjectName());
			for(Composed c : MainActivity.composed){
				if(c.getPhoto_id()==MainActivity.photo.getPhoto_id() && c.getProject_id()==p.getProjectId())
					cb.setChecked(true);
			}
			cb.setOnCheckedChangeListener(this);
			RelLayout.addView(cb);
		}

		return v;
	}
	
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.info_button_geo:
					//to cancel the automatic toggle action on click
					if(MainActivity.photo.getGpsGeom_id()==0){
						geo.setChecked(false);
						next.setVisibility(View.GONE);
					}
					else{
						geo.setChecked(true);
						next.setVisibility(View.VISIBLE);
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
	    	//save of the information set
			
			EditText txt = (EditText) getView().findViewById(R.id.info_edit_description);
			MainActivity.photo.setPhoto_description(txt.getText().toString());
			txt = (EditText) getView().findViewById(R.id.info_edit_author);
			MainActivity.photo.setPhoto_author(txt.getText().toString());
			txt = (EditText) getView().findViewById(R.id.info_edit_adress);
			MainActivity.photo.setPhoto_adresse(txt.getText().toString());
			
			if(!MainActivity.project.isEmpty()){
				if(!MainActivity.project.get(MainActivity.project.size()-1).getRegistredInLocal()){
			    	Project pro = MainActivity.project.get(MainActivity.project.size()-1);
			    	txt = (EditText) getView().findViewById(R.id.info_edit_project);
			    	pro.setProjectName(txt.getText().toString());
				}
			}
		    else if(!txt.getText().toString().equals("")) {
			    Project pro = new Project();
			    txt = (EditText) getView().findViewById(R.id.info_edit_project);
			    pro.setProjectName(txt.getText().toString());
			    pro.setProjectId(GetId.Project());
			    MainActivity.project.add(pro);
			    
			    Composed comp = new Composed();
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
			
			//setting the state of buttons
			if(MainActivity.photo.getGpsGeom_id()==0){
				geo.setChecked(false);
				next.setVisibility(View.GONE);
			}
			else{
				geo.setChecked(true);
				next.setVisibility(View.VISIBLE);
			}

			EditText txt = (EditText) getView().findViewById(R.id.info_edit_author);
			txt.setText(MainActivity.photo.getPhoto_author());
			txt = (EditText) getView().findViewById(R.id.info_edit_description);
			txt.setText(MainActivity.photo.getPhoto_description());
			txt = (EditText) getView().findViewById(R.id.info_edit_adress);
			txt.setText(MainActivity.photo.getPhoto_adresse());
			if(!MainActivity.project.isEmpty()){
				if(!MainActivity.project.get(MainActivity.project.size()-1).getRegistredInLocal()){
					txt = (EditText) getView().findViewById(R.id.info_edit_project);
					txt.setText(MainActivity.project.get(MainActivity.project.size()-1).getProjectName());
				}
			}
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
			boolean isComposed = false;
			long index = -1;
			
			for(int i=0; i<MainActivity.project.size(); i++){
				if(buttonView.getText().equals(MainActivity.project.get(i).getProjectName())){
					for(int j=0; j<MainActivity.composed.size(); j++){
						if(MainActivity.composed.get(j).getProject_id()==MainActivity.project.get(i).getProjectId()){
							isComposed = true;
							index=j;
						}
					}
					if(!isComposed)
						index = MainActivity.project.get(i).getProjectId();
				}
			}
			if(index!=-1){
				if(isComposed)
					MainActivity.composed.remove((int)index);
				else{
					Composed c = new Composed();
					c.setPhoto_id(MainActivity.photo.getPhoto_id());
					for(Project p : MainActivity.project){
						if(p.getProjectName().equals(buttonView.getText())){
							c.setProject_id(p.getProjectId());
						}
					}
					MainActivity.composed.add(c);
				}
			}
		}
}