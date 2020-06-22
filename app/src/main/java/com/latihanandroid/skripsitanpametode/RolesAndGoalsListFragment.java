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
import com.latihanandroid.skripsitanpametode.adapter.RolesAndGoalsAdapter;
import com.latihanandroid.skripsitanpametode.pojo.RolesAndGoals;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RolesAndGoalsListFragment extends Fragment {
    private FloatingActionButton btnAddRolesAndGoals;
    private RecyclerView rvRolesAndGoalsList;
    private RolesAndGoalsAdapter rolesAndGoalsAdapter;
    public RolesAndGoalsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_roles_and_goals_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hubungkanDenganXML(view);
        rolesAndGoalsAdapter= new RolesAndGoalsAdapter();
        rvRolesAndGoalsList.setAdapter(rolesAndGoalsAdapter);
        rvRolesAndGoalsList.setLayoutManager(new LinearLayoutManager(rvRolesAndGoalsList.getContext()));
        rvRolesAndGoalsList.setHasFixedSize(false);
        showAllRolesAndGoals();
        hubungkanDenganListener();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);

        SearchManager searchManager= (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager!=null){
            SearchView searchView= (SearchView) (menu.findItem(R.id.search)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_keyword));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
//                    Toast.makeText(getContext(), query+" dicari", Toast.LENGTH_SHORT).show();
                      ArrayList<RolesAndGoals> datas=DatabaseHelper.getInstance(getContext()).findRolesAndGoals(query);
                      rolesAndGoalsAdapter.setDatas(datas);
                      rolesAndGoalsAdapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.print:
                printPdf(rolesAndGoalsAdapter.getDatas());
                break;
        }
        return false;
    }
    private void hubungkanDenganXML(View view){
        btnAddRolesAndGoals=view.findViewById(R.id.btnAddRolesAndGoals);
        rvRolesAndGoalsList=view.findViewById(R.id.rvRolesGoalsList);
    }
    private void showAllRolesAndGoals(){
        ArrayList<RolesAndGoals> datas= DatabaseHelper.getInstance(getContext()).readAllRolesAndGoals();
        rolesAndGoalsAdapter.setDatas(datas);
        rolesAndGoalsAdapter.notifyDataSetChanged();
    }

    private void findRolesAndGoals(String keyword){
        ArrayList<RolesAndGoals> datas=DatabaseHelper.getInstance(getContext()).findRolesAndGoals(keyword);
        rolesAndGoalsAdapter.setDatas(datas);
        rolesAndGoalsAdapter.notifyDataSetChanged();

    }
    private void hubungkanDenganListener(){
        rolesAndGoalsAdapter.setOnItemRolesAndGoalsClickCallback(new RolesAndGoalsAdapter.OnItemRolesAndGoalsClickListener() {
            @Override
            public void onItemClick(RolesAndGoals rolesAndGoals) {
                Bundle bundle=new Bundle();
                bundle.putParcelable(UpdateDeleteRolesAndGoalsFragment.EXTRA_ROLES_GOALS,rolesAndGoals);
                if (getActivity()!=null){
                    Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.nav_update_delete_roles_and_goals,bundle);
                }else {
                    Toast.makeText(getContext(), "Activity is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAddRolesAndGoals.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_to_add_roals_goals));
    }
    private void printPdf(ArrayList<RolesAndGoals> rolesAndGoals){
        ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        if (rolesAndGoals.size()>0){
            GeneratePdfHelper pdfHelper=new GeneratePdfHelper(getContext());
            pdfHelper.createPdfRolesGoals(rolesAndGoals);
        }else {
            Toast.makeText(getContext(), "There is no data to print", Toast.LENGTH_SHORT).show();
        }
    }
}
