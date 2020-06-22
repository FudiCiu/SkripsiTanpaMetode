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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.latihanandroid.skripsitanpametode.pojo.RolesAndGoals;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UpdateDeleteRolesAndGoalsFragment extends Fragment {
    private EditText edtRoles,edtGoals;
    private Button btnUpdate;
    public static final String EXTRA_ROLES_GOALS="EXTRA_ROLES_GOALS";
    public UpdateDeleteRolesAndGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update_delete_menu,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_update_delete_roles_and_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arg=getArguments();
        if (arg!=null){
            RolesAndGoals rolesAndGoals=arg.getParcelable(EXTRA_ROLES_GOALS);
            if (rolesAndGoals!=null){
                hubungkanDenganXML(view);
                hubungkanViewDenganListener();
                isiNilaiDefault(rolesAndGoals.getRoles(),rolesAndGoals.getGoals());
            }
        }
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

    private void hubungkanDenganXML(View view){
        edtRoles=view.findViewById(R.id.edtRoles);
        edtGoals=view.findViewById(R.id.edtGoals);
        btnUpdate=view.findViewById(R.id.btnUpdate);
    }

    private void hubungkanViewDenganListener(){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    updateProcess();
            }
        });
    }
    private void isiNilaiDefault(String roles,String goals){
        edtRoles.setText(roles);
        edtGoals.setText(goals);
    }
    private void updateProcess(){
        if (getArguments()!=null){
            RolesAndGoals rolesAndGoalsBefore= getArguments().getParcelable(EXTRA_ROLES_GOALS);
            if (rolesAndGoalsBefore!=null){
                RolesAndGoals rolesAndGoalsAfter= getFromView();
                if (rolesAndGoalsAfter==null){
                    Toast.makeText(getContext(), "Empty Field Detected", Toast.LENGTH_SHORT).show();
                }else {
                    rolesAndGoalsAfter.setId(rolesAndGoalsBefore.getId());
                    long updated=DatabaseHelper.getInstance(getContext()).upadateRolesAndGoals(rolesAndGoalsAfter);
                    if (updated>0){
                        Toast.makeText(getContext(), "Roles and goals with id "+rolesAndGoalsAfter.getId()+" updated", Toast.LENGTH_SHORT).show();
                        if (getActivity()!=null){
                            getActivity().onBackPressed();
                        }
                    }else{
                        Toast.makeText(getContext(), "Failed to delete data", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }
    private void deleteProcess(){
        if (getArguments()!=null) {
            RolesAndGoals rolesAndGoalsBefore = getArguments().getParcelable(EXTRA_ROLES_GOALS);
            if (rolesAndGoalsBefore != null) {
                long deletedCount= DatabaseHelper.getInstance(getContext()).deleteRolesAndGoals(rolesAndGoalsBefore);
                if (deletedCount>0){
                    Toast.makeText(getContext(), "Roles and goals with id "+rolesAndGoalsBefore.getId()+" deleted", Toast.LENGTH_SHORT).show();
                    if (getActivity()!=null){
                        getActivity().onBackPressed();
                    }
                }else {
                    Toast.makeText(getContext(), "Failed to delete data", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    private RolesAndGoals getFromView(){
        String roles= String.valueOf(edtRoles.getText());
        String goals=String.valueOf(edtGoals.getText());
        if (roles.equals("")||goals.equals(""))
            return null;
        else
            return new RolesAndGoals(roles,goals);
    }

}
