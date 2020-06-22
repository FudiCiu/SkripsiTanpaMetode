package com.latihanandroid.skripsitanpametode;

import android.os.Bundle;
import android.view.LayoutInflater;
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
 * Use the {@link AddRolesAndGoalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRolesAndGoalsFragment extends Fragment {

    private Button btnAdd;
    private EditText edtRoles,edtGoals;
    public AddRolesAndGoalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_roles_and_goals, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hubungkanDenganXML(view);
        hubungkanViewDenganListener();
    }

    private void hubungkanDenganXML(View view){
        edtRoles=view.findViewById(R.id.edtRoles);
        edtGoals=view.findViewById(R.id.edtGoals);
        btnAdd=view.findViewById(R.id.btnAdd);
    }

    private void hubungkanViewDenganListener(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addProcess();
            }
        });
    }
    private void addProcess(){
        if (getFromView()==null){
            Toast.makeText(getContext(), "Empty field detected", Toast.LENGTH_SHORT).show();
        }else {
            long id=DatabaseHelper.getInstance(getContext()).insertRolesAndGoals(getFromView());
            if (id==-1)
                Toast.makeText(getContext(), "Failed to insert roles and goals", Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(getContext(), "Roles and Goals added with id= "+ String.valueOf(id), Toast.LENGTH_SHORT).show();
                if (getActivity()!=null){
                    getActivity().onBackPressed();
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
    private void isiNilaiDefault(){
        edtRoles.setText("");
        edtGoals.setText("");
    }
}
