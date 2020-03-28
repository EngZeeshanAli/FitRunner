package com.example.fitrunner.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitrunner.R;
import com.example.fitrunner.UiControllers.Constants;
import com.example.fitrunner.adapter.LogsAdapter;
import com.example.fitrunner.objects.RunningLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Reports extends Fragment {
    RecyclerView reports;
    ArrayList<RunningLog> list;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_fragment_layout, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        list = new ArrayList<>();
        reports = v.findViewById(R.id.reports_recycler);
        reports.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        getReportsList(user.getUid());
    }

    void getReportsList(String child) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("processing");
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.LOGS_TABLE).child(child).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RunningLog log = snapshot.getValue(RunningLog.class);
                    list.add(log);
                }
                Collections.reverse(list);
                LogsAdapter adapter = new LogsAdapter(getActivity(), list);
                reports.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }
}
