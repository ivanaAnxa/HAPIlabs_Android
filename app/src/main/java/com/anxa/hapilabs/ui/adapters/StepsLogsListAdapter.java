package com.anxa.hapilabs.ui.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Steps;

import java.util.Calendar;
import java.util.List;

/**
 * Created by aprilanxa on 25/10/2016.
 */

public class StepsLogsListAdapter extends ArrayAdapter<Steps> implements View.OnClickListener {

    Context context;

    List<Steps> items;
    Steps selectedSteps;
    LayoutInflater layoutInflater;

    String inflater = Context.LAYOUT_INFLATER_SERVICE;

    TextView date_tv;
    TextView steps_tv;
    TextView device_tv;

    EditText date;
    EditText steps;

    List<String> actions;
    View.OnClickListener listener;

    final Calendar myCalendar = Calendar.getInstance();

    public StepsLogsListAdapter(Context context, List<Steps> items) {

        super(context, R.layout.steps_logs_list, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = layoutInflater.inflate(R.layout.steps_logs_list, parent, false);

        Steps currentSteps = (Steps) items.get(position);
        String stepsDateString = AppUtil.getWeightDateFormat(currentSteps.start_datetime);

        date_tv = ((TextView) rowView.findViewById(R.id.text_date));
        date_tv.setText(stepsDateString);

        String stepsString = currentSteps.steps_count;
        steps_tv = ((TextView) rowView.findViewById(R.id.text_steps));
        steps_tv.setText(stepsString);

        device_tv = ((TextView) rowView.findViewById(R.id.text_device));
        device_tv.setText(currentSteps.device_name);

        if (currentSteps.device_name.equalsIgnoreCase("Manual")) {
            ((ImageButton) rowView.findViewById(R.id.steps_log_edit)).setVisibility(View.VISIBLE);
            ((ImageButton) rowView.findViewById(R.id.steps_log_edit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOptionsDialog(v, items.get(position));
                }
            });
        } else {
            ((ImageButton) rowView.findViewById(R.id.steps_log_edit)).setVisibility(View.GONE);
        }

        if (position >= getCount())
            ((View) rowView.findViewById(R.id.separator)).setVisibility(View.GONE);

        return rowView;
    }


    public void updateItems(List<Steps> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();// more than zero
    }


    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }

    private void showOptionsDialog(View v, Steps selectedSteps) {

        this.selectedSteps = selectedSteps;
        ApplicationEx.getInstance().currentSteps = selectedSteps;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        showEditDialog();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        showDeleteDialog();
                        break;
                }
            }
        };

        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        }else{
            builder = new AlertDialog.Builder(context);
        }

        builder.setMessage(null).setPositiveButton(context.getResources().getString(R.string.btn_edit), dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.btn_delete), dialogClickListener).show();
    }

    private void showDeleteDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        ApplicationEx.getInstance().currentSteps = selectedSteps;
                        Intent broadint = new Intent();
                        broadint.setAction(context.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_DELETE));
                        context.sendBroadcast(broadint);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else{
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(context.getResources().getString(R.string.ALERTMESSAGE_DELETE_MEAL)).setPositiveButton(context.getResources().getString(R.string.btn_yes), dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.btn_no), dialogClickListener).show();
    }

    private void showEditDialog() {

        AlertDialog.Builder alertDialog;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else{
            alertDialog = new AlertDialog.Builder(context);
        }

        TextView title = new TextView(context);
        title.setText(context.getResources().getString(R.string.EDIT_STEPS));
        title.setBackgroundColor(Color.WHITE);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#F68802"));
        title.setTextSize(20);

        alertDialog.setCustomTitle(title);

        final TextView date_edit_tv = new TextView(context);
        final TextView steps_edit_tv = new TextView(context);

        date = new EditText(context);
        date.setText(AppUtil.getEditWeightDateFormat(selectedSteps.start_datetime));
        steps = new EditText(context);
        steps.setText(selectedSteps.steps_count);

        date_edit_tv.setText(context.getResources().getString(R.string.WEIGHT_GRAPH_DATE));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        steps_edit_tv.setText(context.getResources().getString(R.string.STEPS_BUTTON));

        date.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL | InputType.TYPE_DATETIME_VARIATION_DATE);
        steps.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);
        ll.addView(date_edit_tv);
        ll.addView(date);
        ll.addView(steps_edit_tv);
        ll.addView(steps);
        ll.setPadding(5, 5, 5, 5);
        alertDialog.setView(ll);

        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                selectedSteps.steps_count = steps.getText().toString();
                selectedSteps.start_datetime = myCalendar.getTime();

                ApplicationEx.getInstance().currentSteps = selectedSteps;


                Intent broadint = new Intent();
                broadint.setAction(context.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_EDIT));
                context.sendBroadcast(broadint);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                //ACTION
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }


    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateLabel() {
        date.setText(AppUtil.getEditWeightDateFormat(myCalendar.getTime()));
    }
}
