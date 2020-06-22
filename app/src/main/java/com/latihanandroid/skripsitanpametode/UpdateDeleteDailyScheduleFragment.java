package com.latihanandroid.skripsitanpametode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.latihanandroid.skripsitanpametode.pojo.DailySchedule;
import com.latihanandroid.skripsitanpametode.pojo.RolesAndGoals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateDeleteDailyScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateDeleteDailyScheduleFragment extends Fragment implements TimePickerFragment.DialogTimeListener {
    private Spinner spnJenisAlarm;
    private Button btnSetTime;
    private TextView txtTime;
    private TextView txtDay;
    private EditText edtPlace;
    private TextView txtActivity;
    private Button btnChooseActivity;
    private Button btnUpdate;
    private RolesAndGoals selectedRolesGoals=null;
    public static final String DAILY_SCHEDULE_EXTRA="EXTRA_DAILY_SCHEDULE";

    public UpdateDeleteDailyScheduleFragment() {
    }

    public static UpdateDeleteDailyScheduleFragment newInstance(String param1, String param2) {
        UpdateDeleteDailyScheduleFragment fragment = new UpdateDeleteDailyScheduleFragment();
        return fragment;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update_delete_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                deleteProcess();
            break;
            case android.R.id.home:
                if (getActivity()!=null){
                    getActivity().onBackPressed();
                }
            break;
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_update_delete_daily_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hubungkanDenganXML(view);
        hubungkanViewDenganListener();
        if (getArguments()!=null){
            DailySchedule dailySchedule=getArguments().getParcelable(DAILY_SCHEDULE_EXTRA);
            if (dailySchedule!=null){
                isiNilaiDefault(dailySchedule.getHariAsString(),dailySchedule.getWaktuAsString(),
                        dailySchedule.getTempat(),dailySchedule.getAktiviasAsString(),dailySchedule.getJenisAlarm());
            }else {
                Toast.makeText(getContext(), "Daily Schedule is null", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), "Argument is null", Toast.LENGTH_SHORT).show();
        }

    }
    private void hubungkanDenganXML(View view){
        btnSetTime=view.findViewById(R.id.btnSetTime);
        spnJenisAlarm =view.findViewById(R.id.spnJenisAlarm);
        txtDay=view.findViewById(R.id.txtDay);
        txtActivity=view.findViewById(R.id.txtActivity);
        txtTime=view.findViewById(R.id.txtTime);
        edtPlace=view.findViewById(R.id.edtPlace);
        btnChooseActivity=view.findViewById(R.id.btnSetActivity);
        btnUpdate=view.findViewById(R.id.btnUpdate);
    }
    private void hubungkanViewDenganListener(){
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openChooseTime();
            }
        });
        btnChooseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   openChooseActivity();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    updateProcess();
            }
        });
    }

    @Override
    public void onDialogTimeSet(int hours, int minutes) {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE,minutes);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm", Locale.US);
        txtTime.setText(simpleDateFormat.format(calendar.getTime()));
    }
    public ChooseRolesAndGoalsFragment.OnRolesAndGoalsSelectedListener onRolesAndGoalsSelectedListener=new ChooseRolesAndGoalsFragment.OnRolesAndGoalsSelectedListener() {
        @Override
        public void onRolesAndGoalsSelected(RolesAndGoals rolesAndGoals) {
            txtActivity.setText(rolesAndGoals.getRoles()+" : "+rolesAndGoals.getGoals());
            selectedRolesGoals=rolesAndGoals;
        }
    };
    private DailySchedule getDailyScheduleFromView(){
        DailySchedule dailySchedule=null;
        if (edtPlace.getText().toString().equals("")||this.selectedRolesGoals==null) return dailySchedule;
        short hari=-1;
        if (getArguments()!=null){
            hari= (short) getArguments().getInt(AddDailyScheduleFragment.ADD_EXTRA,-1);
        }
        dailySchedule=new DailySchedule();
        dailySchedule.setHari(hari);
        dailySchedule.setWaktuFromString(txtTime.getText().toString());
        dailySchedule.setTempat(edtPlace.getText().toString());
        dailySchedule.setAktivitas(selectedRolesGoals);
        dailySchedule.setJenisAlarm(spnJenisAlarm.getSelectedItemPosition());
        return dailySchedule;
    }
    private void isiNilaiDefault(String day, String time, String place, String activity, int jenisAlarm ){
        txtDay.setText(day);
        txtTime.setText(time);
        edtPlace.setText(place);
        txtActivity.setText(activity);
        spnJenisAlarm.setSelection(jenisAlarm);
        if (getArguments()!=null){
            DailySchedule old =getArguments().getParcelable(UpdateDeleteDailyScheduleFragment.DAILY_SCHEDULE_EXTRA);
            if (old != null) {
                this.selectedRolesGoals=old.getAktivitas();
            }
        }
    }
    private void updateProcess(){
        DailySchedule dailySchedule=getDailyScheduleFromView();
        if (dailySchedule==null){
            Toast.makeText(getContext(), "Empty field detected", Toast.LENGTH_SHORT).show();
        }else{
            if (getArguments()!=null){
                DailySchedule defaultDailySchedule=getArguments().getParcelable(UpdateDeleteDailyScheduleFragment.DAILY_SCHEDULE_EXTRA);
                dailySchedule.setId(defaultDailySchedule.getId());
                dailySchedule.setHari(defaultDailySchedule.getHari());
                long editedCount=DatabaseHelper.getInstance(getContext()).updateDailySchedule(dailySchedule);
                if (editedCount>0){
                    Toast.makeText(getContext(), String.valueOf(editedCount)+" data daily Schedule edited", Toast.LENGTH_SHORT).show();
                    if (dailySchedule.getJenisAlarm()>0){
                        if (getContext()!=null){
                            AlarmReceiver.setDailyScheduleAlarm(getContext(),dailySchedule);
                        }
                    }else{
                        if (getContext()!=null){
                            AlarmReceiver.cancelAlarm(getContext(),dailySchedule);
                        }
                    }
                    if (getActivity()!=null){
                        getActivity().onBackPressed();
                    }
                }else {
                    Toast.makeText(getContext(), "Failed to edit data Daily", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void deleteProcess(){
        if (getArguments()!=null){
            DailySchedule old= getArguments().getParcelable(UpdateDeleteDailyScheduleFragment.DAILY_SCHEDULE_EXTRA);
            if (getContext()!=null){
                AlarmReceiver.cancelAlarm(getContext(),old);
            }
            long deletedCount= DatabaseHelper.getInstance(getContext()).deleteDailySchedule(old);
            if (deletedCount>0){
                Toast.makeText(getContext(), String.valueOf(deletedCount)+" data daily schedule deleted", Toast.LENGTH_SHORT).show();
                if (getActivity()!=null){
                    getActivity().onBackPressed();
                }
            }else {
                Toast.makeText(getContext(), " Failed to delete data", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openChooseActivity(){
        ChooseRolesAndGoalsFragment chooseRolesAndGoalsFragment=new ChooseRolesAndGoalsFragment();
        FragmentManager fragmentManager=getChildFragmentManager();
        chooseRolesAndGoalsFragment.show(fragmentManager,ChooseRolesAndGoalsFragment.class.getSimpleName());
    }
    private void openChooseTime(){
        if (getFragmentManager()!=null){
            String[] waktu=txtTime.getText().toString().split(":");
            TimePickerFragment timePickerFragment=TimePickerFragment.newInstance(Integer.parseInt(waktu[0]),
                    Integer.parseInt(waktu[1]),UpdateDeleteDailyScheduleFragment.this);
            timePickerFragment.show(getFragmentManager(),TimePickerFragment.class.getSimpleName());
        }
    }
}
