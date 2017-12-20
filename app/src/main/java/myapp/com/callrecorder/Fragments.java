package myapp.com.callrecorder;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myapp.com.callrecorder.Database.Database;
import myapp.com.callrecorder.Database.MissCallDetails;

/**
 * Created by SSPL on 12/07/17.
 */

public class Fragments extends Fragment {

    List<MissCallDetails> missCallcount;
    int misscalldetailscount, misscallid;

    View view;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    ArrayList<String> misscallName = new ArrayList<>();
    ArrayList<String> misscallNumber = new ArrayList<>();
    ArrayList<String> misscallDateTime = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_layout, container, false);

        Database db = new Database(getActivity().getApplicationContext());
        //Getting miss call details
        missCallcount = db.getAllMissCalldetails();
        misscalldetailscount = 0;
        for (MissCallDetails cn : missCallcount) {
            misscalldetailscount++;
        }
        if (misscalldetailscount > 0) {
            List<MissCallDetails> calldetailscount = db.getAllMissCalldetails();

            for (MissCallDetails cn : calldetailscount) {
                misscallid = cn.getId();
                misscallName.add(cn.getMisscallDetailName());
                misscallNumber.add(cn.getMisscallDetailMobno());
                misscallDateTime.add(cn.getMisscallDateTime());

            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No misscalls", Toast.LENGTH_LONG).show();
        }

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // The number of Columns
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Collections.reverse(misscallName);
        Collections.reverse(misscallNumber);
        Collections.reverse(misscallDateTime);
        mAdapter = new HLVAdapter(getActivity().getApplicationContext(), misscallNumber, misscallDateTime,misscallName);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}
