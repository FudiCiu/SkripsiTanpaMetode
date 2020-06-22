package com.latihanandroid.skripsitanpametode;

import android.os.Bundle;
import android.view.LayoutInflater;
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


public class AddDailyScheduleFragment extends Fragment implements TimePickerFragment.DialogTimeListener {
    private Spinner spnJenisAlarm;
    private Button btnSetTime;
    private Button btnAdd;
    private TextView txtTime;
    private TextView txtDay;
    private EditText edtPlace;
    private TextView txtActivity;
    private Button btnChooseActivity;
    private RolesAndGoals selectedRolesGoals;
    public static final String ADD_EXTRA="EXTRA_DAY";
    public AddDailyScheduleFragment() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (getActivity()!=null){
                    getActivity().onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_daily_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hubungkanDenganXML(view);
        hubungkanViewDenganListener();
        isiNilaiDefault();
    }

    @Override
    public void onDialogTimeSet(int hours, int minutes) {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE,minutes);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm",Locale.US);
        txtTime.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void hubungkanDenganXML(View view){
        btnSetTime=view.findViewById(R.id.btnSetTime);
        spnJenisAlarm =view.findViewById(R.id.spnJenisAlarm);
        txtDay=view.findViewById(R.id.txtDay);
        txtActivity=view.findViewById(R.id.txtActivity);
        txtTime=view.findViewById(R.id.txtTime);
        edtPlace=view.findViewById(R.id.edtPlace);
        btnChooseActivity=view.findViewById(R.id.btnSetActivity);
        btnAdd=view.findViewById(R.id.btnAdd);
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
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProcess();
            }
        });
    }
    private void isiNilaiDefault(){
        if (getArguments()!=null){
            int hariVal=getArguments().getInt(ADD_EXTRA,0);
            Calendar c=Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK,hariVal);
            SimpleDateFormat sf=new SimpleDateFormat("EEEE", Locale.US);
            txtDay.setText(sf.format(c.getTime()));
        }
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm",Locale.US);
        txtTime.setText(simpleDateFormat.format(calendar.getTime()));
        edtPlace.setText("");
        txtActivity.setText("Roles : Goals");
        spnJenisAlarm.setSelection(0);
    }
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
    private void addProcess(){
        if (getDailyScheduleFromView()==null){
            Toast.makeText(getContext(), "Empty Field detected", Toast.LENGTH_SHORT).show();
        }else {
            DailySchedule dailySchedule=getDailyScheduleFromView();
            long insertedId=DatabaseHelper.getInstance(getContext()).insertDailySchedule(dailySchedule);
            if (insertedId>0){
                Toast.makeText(getContext(), "Daily Schedule with id "+ insertedId+ " added", Toast.LENGTH_SHORT).show();
                dailySchedule.setId((int) insertedId);
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
                Toast.makeText(getContext(), "Failed to insert daily activity", Toast.LENGTH_SHORT).show();
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
                    Integer.parseInt(waktu[1]),AddDailyScheduleFragment.this);
            timePickerFragment.show(getFragmentManager(),TimePickerFragment.class.getSimpleName());
        }
    }
    public ChooseRolesAndGoalsFragment.OnRolesAndGoalsSelectedListener onRolesAndGoalsSelectedListener=new ChooseRolesAndGoalsFragment.OnRolesAndGoalsSelectedListener() {
        @Override
        public void onRolesAndGoalsSelected(RolesAndGoals rolesAndGoals) {
            selectedRolesGoals=rolesAndGoals;
            txtActivity.setText(rolesAndGoals.getRoles()+" : "+rolesAndGoals.getGoals());

        }
    };
}
