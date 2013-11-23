package com.ecn.urbapp.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ecn.urbapp.R;
import com.ecn.urbapp.activities.MainActivity;

/**
 * This class creates the dialog that ask the user to choose the characteristics of the
 * zone
 * 
 * @author Jules Party
 * 
 */
public class ConfirmPhotoDialogFragment extends DialogFragment {

	//TODO Adddescription for javadoc
	private Dialog box;
	//TODO Adddescription for javadoc
	private Button bpok;
	//TODO Adddescription for javadoc
	private Button quit;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		box = new Dialog(getActivity());
		box.setContentView(R.layout.layout_confirm_dialog);
		box.setTitle(R.string.confirm_photo);
		box.setCanceledOnTouchOutside(true);
		bpok = (Button)box.findViewById(R.id.confirm_ok);
		bpok.setOnClickListener(listener_ok);
		quit = (Button)box.findViewById(R.id.confirm_quit);
		quit.setOnClickListener(listener_quit);
		return box;
	}

	//TODO Adddescription for javadoc
	private OnClickListener listener_ok = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.zones=null;
			box.dismiss();
		}
	};

	//TODO Adddescription for javadoc
	private OnClickListener listener_quit = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.pathImage=null;
			box.dismiss();
		}
	};
	/**
	 * Action realized when the user cancel the dialog (touch outside the dialog
	 * or press the back button)
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
	}
}