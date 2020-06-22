package com.latihanandroid.skripsitanpametode;

import android.app.SearchManager;
import android.content.Context;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.latihanandroid.skripsitanpametode.adapter.RolesAndGoalsAdapter;
import com.latihanandroid.skripsitanpametode.pojo.RolesAndGoals;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseRolesAndGoalsFragment extends DialogFragment {

    private RecyclerView rvRolesAndGoalsList;
    private RolesAndGoalsAdapter rolesAndGoalsAdapter;
    private OnRolesAndGoalsSelectedListener rolesAndGoalsCallback;
    private Toolbar toolbar;
    public ChooseRolesAndGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
//        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_choose_roles_and_goals, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hubungkanDenganXML(view);
        setNilaiDefault();
        hubungkanDenganListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main,menu);
        toolbar.inflateMenu(R.menu.main);
        SearchManager searchManager= (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager!=null){
            SearchView searchView= (SearchView) toolbar.getMenu().findItem(R.id.search).getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_keyword));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    findRolesAndGoals(query);
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
                    showAllRolesAndGoals();
                    return false;
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment fragment= getParentFragment();
        if (fragment instanceof AddDailyScheduleFragment){
            AddDailyScheduleFragment addDailyScheduleFragment= (AddDailyScheduleFragment) fragment;
            this.rolesAndGoalsCallback= addDailyScheduleFragment.onRolesAndGoalsSelectedListener;
        }else if (fragment instanceof UpdateDeleteDailyScheduleFragment){
            UpdateDeleteDailyScheduleFragment updateDeleteDailyScheduleFragment= (UpdateDeleteDailyScheduleFragment) fragment;
            this.rolesAndGoalsCallback=updateDeleteDailyScheduleFragment.onRolesAndGoalsSelectedListener;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.rolesAndGoalsCallback=null;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ChooseRolesAndGoalsFragment.this.dismiss();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hubungkanDenganXML(View view){
        rvRolesAndGoalsList=view.findViewById(R.id.rvRolesGoalsList);
        toolbar=view.findViewById(R.id.toolbar);

    }
    private void setNilaiDefault(){
        toolbar.setTitle("Choose Roles And Goals");
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        if (getArguments()!=null){
            this.rolesAndGoalsCallback= (OnRolesAndGoalsSelectedListener) getArguments().getSerializable("EXTRA_INTERFACE");
        }
        ArrayList<RolesAndGoals> datas= DatabaseHelper.getInstance(getContext()).readAllRolesAndGoals();
        rolesAndGoalsAdapter= new RolesAndGoalsAdapter(datas);
        rvRolesAndGoalsList.setAdapter(rolesAndGoalsAdapter);
        rvRolesAndGoalsList.setLayoutManager(new LinearLayoutManager(rvRolesAndGoalsList.getContext()));
        rvRolesAndGoalsList.setHasFixedSize(true);
    }
    private void hubungkanDenganListener(){
        rolesAndGoalsAdapter.setOnItemRolesAndGoalsClickCallback(new RolesAndGoalsAdapter.OnItemRolesAndGoalsClickListener() {
            @Override
            public void onItemClick(RolesAndGoals rolesAndGoals) {
                if (rolesAndGoalsCallback!=null){
                    rolesAndGoalsCallback.onRolesAndGoalsSelected(rolesAndGoals);
                }else {
                    Toast.makeText(getContext(), "Tess", Toast.LENGTH_SHORT).show();
                }
                ChooseRolesAndGoalsFragment.this.dismiss();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseRolesAndGoalsFragment.this.dismiss();
            }
        });
    }
    private void findRolesAndGoals(String keyword){
        ArrayList<RolesAndGoals> datas=DatabaseHelper.getInstance(getContext()).findRolesAndGoals(keyword);
        rolesAndGoalsAdapter.setDatas(datas);
        rolesAndGoalsAdapter.notifyDataSetChanged();
    }
    private void showAllRolesAndGoals(){
        ArrayList<RolesAndGoals> datas= DatabaseHelper.getInstance(getContext()).readAllRolesAndGoals();
        rolesAndGoalsAdapter.setDatas(datas);
        rolesAndGoalsAdapter.notifyDataSetChanged();
    }
    public ChooseRolesAndGoalsFragment(OnRolesAndGoalsSelectedListener rolesAndGoalsCallback) {
        this.rolesAndGoalsCallback = rolesAndGoalsCallback;
    }
    public interface OnRolesAndGoalsSelectedListener extends Serializable {
        public void onRolesAndGoalsSelected(RolesAndGoals rolesAndGoals);
    }
}
