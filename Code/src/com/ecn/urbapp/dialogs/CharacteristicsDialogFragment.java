package com.ecn.urbapp.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.MainActivity;
import com.ecn.urbapp.db.ElementType;
import com.ecn.urbapp.db.Material;
import com.ecn.urbapp.fragments.CharacteristicsFragment;
import com.ecn.urbapp.utils.colorpicker.AmbilWarnaDialog;
import com.ecn.urbapp.utils.colorpicker.AmbilWarnaDialog.OnAmbilWarnaListener;
import com.ecn.urbapp.zones.UtilCharacteristicsZone;

/**
 * This class creates the dialog that ask the user to choose the characteristics of the
 * zone
 * 
 * @author Jules Party
 * 
 */
public class CharacteristicsDialogFragment extends DialogFragment {

	/**
	 * The Dialog instance that allow the user to characterize Elements.
	 */
	private Dialog box;
	/**
	 * The Spinner instance used to select the type of the Element(s) to characterize.
	 */
	private Spinner spinType;
	/**
	 * The Spinner instance used to select the material of the Element(s) to characterize.
	 */
	private Spinner spinMaterial;
	/**
	 * The View instance used to show the color of the Element(s) to characterize.
	 */
	private View colorView;
	/**
	 * Dialog used to choose a color.
	 */
	private AmbilWarnaDialog colorDialog;
	/**
	 * The Color chosen in the colorView.
	 */
	private int chosenColor;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		box = new Dialog(getActivity());
		box.setContentView(R.layout.layout_definition_dialog);
		box.setTitle(R.string.definition_dialog_title);
		box.setCanceledOnTouchOutside(true);
		spinType = (Spinner) box.findViewById(R.id.typeZone);
		spinMaterial = (Spinner) box.findViewById(R.id.materialZone);
		Button validate = (Button) box.findViewById(R.id.validation);
		spinType.setOnItemSelectedListener(itemSelectedListenerType);
		spinMaterial.setOnItemSelectedListener(itemSelectedListenerMaterial);
		validate.setOnClickListener(validation);
		colorView = box.findViewById(R.id.color);
		chosenColor = -1;
		if (UtilCharacteristicsZone.getColorForSelectedZones() != 0) {
			colorView.setBackgroundColor(UtilCharacteristicsZone.getColorForSelectedZones());
		} else {
			colorView.setBackgroundColor(Color.RED);
			//colorView.setBackgroundDrawable(getResources().getDrawable(R.drawable.back));
		}
		colorView.setOnClickListener(openColorDialog);
		Map<String, HashMap<String, Float>> summary = UtilCharacteristicsZone.getStatsForSelectedZones(getResources());
		HashMap<String, Float> types = summary.get(getString(R.string.type));
		HashMap<String, Float> materials = summary.get(getString(R.string.materials));
		List<String> list = //c
				new ArrayList<String>();
		for (ElementType et : MainActivity.elementType) {
			list.add(et.getElementType_name());
		}
		list = getMaterialList(R.array.type);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
		spinType.setAdapter(adapter);
		int position = -1;
		String type;
		if (types.keySet().size() == 1) {
			type = (String) types.keySet().toArray()[0];
			if (type.equals(getResources().getString(R.string.not_defined))) {
				type = "";
			}
		} else {
			type = getResources().getString(R.string.nullString);
		}
		position = list.indexOf(type);
		if (position != -1) {
			spinType.setSelection(position);
		}
		position = -1;
		list = new ArrayList<String>();
		for (Material mat : MainActivity.material) {
			list.add(mat.getMaterial_name());
		}
		list = getMaterialList(R.array.frontagematerial, R.array.groundmaterial, R.array.roofmaterial);
		String material;
		if (materials.keySet().size() == 1) {
			material = (String) materials.keySet().toArray()[0];
			if (material.equals(getResources().getString(R.string.not_defined))) {
				material = "";
			}
		} else {
			material = getResources().getString(R.string.nullString);
		}
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
		spinMaterial.setAdapter(adapter);
		position = list.indexOf(material);
		if (position != -1) {
			spinMaterial.setSelection(position);
		}
		return box;

	}

	//TODO Adddescription for javadoc
	private OnItemSelectedListener itemSelectedListenerType = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	//TODO Adddescription for javadoc
	private List<String> getMaterialList(int... arrayIds) {
		List<String> list = new ArrayList<String>();
		for (int arrayId : arrayIds) {
			String[] array = getResources().getStringArray(arrayId);
			List<String> subList = new ArrayList<String>(Arrays.asList(array));
			subList.remove(getResources().getString(R.string.nullString));
			java.util.Collections.sort(subList);
			list = fusionSortedList(list, subList);
		}
		list.add(0, getResources().getString(R.string.nullString));
		return list;
	}

	//TODO Adddescription for javadoc
	private OnItemSelectedListener itemSelectedListenerMaterial = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	/**
	 * Listener that add the chosen characteristics to all the selected elements
	 * and close the dialog.
	 */
	private OnClickListener validation = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String selection;
			selection = (String) spinType.getSelectedItem();
			if (!selection.equals("")) {
				UtilCharacteristicsZone.setTypeForSelectedZones(selection);
			}
			selection = (String) spinMaterial.getSelectedItem();
			if (!selection.equals("")) {
				UtilCharacteristicsZone.setMaterialForSelectedZones(selection);
			}
			if (chosenColor != 0) {
				UtilCharacteristicsZone.setColorForSelectedZones(chosenColor);
			}
			CharacteristicsFragment.getMyImage().invalidate();
			box.dismiss();
		}
	};

	//TODO Adddescription for javadoc
	private List<String> fusionSortedList(List<String> list1, List<String> list2) {
		List<String> result = new ArrayList<String>();
		while (!list1.isEmpty()) {
			String str1 = list1.remove(0);
			while (!list2.isEmpty()) {
				if (str1.compareToIgnoreCase(list2.get(0)) < 0) {
					break;
				} else {
					String str2 = list2.remove(0);
					result.add(str2);
				}
			}
			result.add(str1);
		}
		while (!list2.isEmpty()) {
			String str2 = list2.remove(0);
			result.add(str2);
		}
		return result;
	}

	/**
	 * Listener that open an AmbilWarnaDialog to chose a color.
	 */
	private OnClickListener openColorDialog = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Create the color picker dialog (with the actual color of the selected zone)
			colorDialog = new AmbilWarnaDialog(getActivity(),
					UtilCharacteristicsZone.getColorForSelectedZones(),
					colorListener);

			// Add a title to the dialog
			colorDialog.getDialog().setTitle(R.string.definition_dialog_color);

			colorDialog.getDialog().show();
		}
	};
	
	/**
	 * Listener for AmbilWarnaListener that save the chosen color in the attribute chosenColor and change the color of the colorView.
	 */
	OnAmbilWarnaListener colorListener = new OnAmbilWarnaListener() {
		@Override
		public void onOk(AmbilWarnaDialog dialog, int color) {
			// Modify the color of the zone
			chosenColor = color;
			colorView.setBackgroundColor(color);
		}

		@Override
		public void onCancel(AmbilWarnaDialog dialog) {
		}
	};

	@Override
	public void onCancel(DialogInterface dialog) {
	}
}