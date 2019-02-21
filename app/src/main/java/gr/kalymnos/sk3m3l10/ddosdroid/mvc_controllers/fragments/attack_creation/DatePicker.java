package gr.kalymnos.sk3m3l10.ddosdroid.mvc_controllers.fragments.attack_creation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePicker extends DialogFragment {
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getContext(), dateSetListener, currentYear, currentMonth, currentDay);
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener dateSetListener) {
        this.dateSetListener = dateSetListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dateSetListener = null;
    }
}