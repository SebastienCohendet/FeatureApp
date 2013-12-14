package com.ecn.urbapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.GeoActivity;
import com.ecn.urbapp.activities.MainActivity;

/**
 * Dialog for the choice of number of points to put in GpsGeom
 * @author Sebastien
 *
 */
public class NbPointsGeo extends DialogFragment{
	
	public Boolean choosed=false;
	
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        
	        CharSequence[] array = {"Facade (2 points)", "Sol/orthophoto (4 points)"};	        
	        builder.setMessage(R.string.dialog_request_nbPoint)
	        .setPositiveButton(R.string.sol, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	        		GeoActivity.nbPoints=4;
	        		choosed=true;
	        		Intent i = new Intent(MainActivity.baseContext, GeoActivity.class);
					startActivityForResult(i, 10);
	        	}
	        });
	        builder.setNegativeButton(R.string.facade, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	        		GeoActivity.nbPoints=2;
	        		choosed=true;
	        		Intent i = new Intent(MainActivity.baseContext, GeoActivity.class);
					startActivityForResult(i, 10);
	        	}
	        });


	        // Create the AlertDialog object and return it
	        return builder.create();
	    }

}
