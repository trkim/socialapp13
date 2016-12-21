package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by eelhea on 2016-10-22.
 */
public class DateDialog extends DialogFragment  {

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog date_picker = new DatePickerDialog(getActivity(), STYLE_NORMAL, (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);

        date_picker.getDatePicker().setTag(getTag());
        return date_picker;
    }

}
