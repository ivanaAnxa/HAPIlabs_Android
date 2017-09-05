package com.anxa.hapilabs.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class RadioDialogFragment extends DialogFragment 
{
    public static final String DATA = "items";
     
    public static final String SELECTED = "selected";
    
    public RadioDialogFragment() {
	}
     
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        Bundle bundle = getArguments();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
         
        dialog.setTitle(com.hapilabs.R.string.PLEASE_SELECT);
        dialog.setPositiveButton(com.hapilabs.R.string.CANCEL, (OnClickListener) new PositiveButtonClickListener());
         
        @SuppressWarnings("unchecked")
		List<String> list = (List<String>)bundle.get(DATA);
        int position = bundle.getInt(SELECTED);
         
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        dialog.setSingleChoiceItems(cs, position, selectItemListener);
         
        return dialog.create();
    }
     
    public class PositiveButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which) 
        {
            dialog.dismiss();
        }
    }
     
    OnClickListener selectItemListener = new OnClickListener() 
    {
 
        @Override
        public void onClick(DialogInterface dialog, int which) 
        {
            // process 
            //which means position
            dialog.dismiss();
        }
     
    };


} 