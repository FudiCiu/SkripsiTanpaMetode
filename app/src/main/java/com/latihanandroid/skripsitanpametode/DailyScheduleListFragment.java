package com.latihanandroid.skripsitanpametode;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.latihanandroid.skripsitanpametode.adapter.DailyScheduleAdapter;
import com.latihanandroid.skripsitanpametode.pojo.DailySchedule;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailyScheduleListFragment extends Fragment {
    public static final String EXTRA_DAILY_SCHEDULE="EXTRA_DAILY_SCHEDULE";
    public static short hariValue=0;
    private String hari="";
    private FloatingActionButton btnAddDailyScheduleFragment;
    private RecyclerView rvDailyScheduleList;
    private DailyScheduleAdapter dailyScheduleAdapter;
    public DailyScheduleListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_daily_schedule_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hubungkanViewDenganXML(view);
        hubungkanViewDenganListener();
        rvDailyScheduleList.setHasFixedSize(false);
        rvDailyScheduleList.setLayoutManager(new LinearLayoutManager(rvDailyScheduleList.getContext()));
        rvDailyScheduleList.setAdapter(dailyScheduleAdapter);

        showAllListDailySchedule();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main,menu);

        SearchManager searchManager= (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager!=null){
            SearchView searchView= (SearchView) (menu.findItem(R.id.search)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_keyword));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    findListDailySchedule(query);
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    showAllListDailySchedule();
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.print:
                printPdf(dailyScheduleAdapter.getDatas());
                break;
        }
        return false;
    }
    private void printPdf(ArrayList<DailySchedule> dailySchedules){
        ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        if (dailySchedules.size()>0){
            GeneratePdfHelper pdfHelper=new GeneratePdfHelper(getContext());
            pdfHelper.createPdfDailySchedule(dailySchedules);
        }else {
            Toast.makeText(getContext(), "There is no data to print", Toast.LENGTH_SHORT).show();
        }
    }
    private void hubungkanViewDenganXML(View view){
        btnAddDailyScheduleFragment= view.findViewById(R.id.btnAddDailySchedule);
        rvDailyScheduleList= view.findViewById(R.id.rvDailyScheduleList);
        dailyScheduleAdapter=new DailyScheduleAdapter();
    }
    private void hubungkanViewDenganListener(){
        Bundle bundle=new Bundle();
        bundle.putInt(AddDailyScheduleFragment.ADD_EXTRA,DailyScheduleListFragment.hariValue);
        btnAddDailyScheduleFragment.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_add_daily_schedule_fragment,bundle));
        dailyScheduleAdapter.setOnItemDailyScheduleClickCallback(new DailyScheduleAdapter.OnItemDailyScheduleClickListener() {
            @Override
            public void onClick(DailySchedule dailySchedule) {
                Bundle bundle=new Bundle();
                bundle.putParcelable(UpdateDeleteDailyScheduleFragment.DAILY_SCHEDULE_EXTRA,dailySchedule);
                if (getActivity()!=null){
                    Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.nav_update_delete_daily_schedule,bundle);
                }else{
                    Toast.makeText(getContext(), "Activity is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showAllListDailySchedule(){
        ArrayList<DailySchedule> datas=new ArrayList<>();
        switch (DailyScheduleListFragment.hariValue){
            case Calendar.MONDAY :
                datas.clear();
                datas=DatabaseHelper.getInstance(getContext()).readAllDailySchedule(Calendar.MONDAY);
                break;
            case Calendar.TUESDAY:
                datas.clear();
                datas=DatabaseHelper.getInstance(getContext()).readAllDailySchedule(Calendar.TUESDAY);
                break;
            case Calendar.WEDNESDAY:
                datas.clear();
                datas=DatabaseHelper.getInstance(getContext()).readAllDailySchedule(Calendar.WEDNESDAY);
                break;
            case Calendar.THURSDAY:
                datas.clear();
                datas=DatabaseHelper.getInstance(getContext()).readAllDailySchedule(Calendar.THURSDAY);
                break;
            case Calendar.FRIDAY:
                datas.clear();
                datas=DatabaseHelper.getInstance(getContext()).readAllDailySchedule(Calendar.FRIDAY);
                break;
            case Calendar.SATURDAY:
                datas.clear();
                datas=DatabaseHelper.getInstance(getContext()).readAllDailySchedule(Calendar.SATURDAY);
                break;
            case Calendar.SUNDAY:
                datas.clear();
                datas=DatabaseHelper.getInstance(getContext()).readAllDailySchedule(Calendar.SUNDAY);
                break;
        }
        dailyScheduleAdapter.setDatas(datas);
        dailyScheduleAdapter.notifyDataSetChanged();
    }
    private void findListDailySchedule(String keyword){
        ArrayList<DailySchedule> dailySchedules=DatabaseHelper.getInstance(getContext()).findDailySchedule(hariValue,keyword);
        dailyScheduleAdapter.setDatas(dailySchedules);
        dailyScheduleAdapter.notifyDataSetChanged();
    }

}
