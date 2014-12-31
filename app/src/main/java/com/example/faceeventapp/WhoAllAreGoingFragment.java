package com.example.faceeventapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.faceeventapp.adapter.UsersAdapter;
import com.example.faceeventapp.dummy.DummyContent;
import com.example.faceeventapp.model.EventsItem;
import com.example.faceeventapp.model.UsersItem;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;

public class WhoAllAreGoingFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;
    private List exampleUsersList;

    // TODO: Rename and change types of parameters
    public static WhoAllAreGoingFragment newInstance(String param1, String param2) {
        WhoAllAreGoingFragment fragment = new WhoAllAreGoingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WhoAllAreGoingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GlobalUser global_user = (GlobalUser) getActivity().getApplicationContext();
        String event_name = global_user.getEvent();

        exampleUsersList = new ArrayList<User>();
        UsersItem users = new UsersItem(event_name);
        for(int i=0;i<users.length;i++)
        {
            exampleUsersList.add(users.getUserItem(i));
        }
        mAdapter = new UsersAdapter(getActivity(),exampleUsersList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whoallaregoing, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User item = (User) this.exampleUsersList.get(position);
        //Toast.makeText(getActivity(), item.username + " Clicked!", Toast.LENGTH_SHORT).show();
        final GlobalUser global_user = (GlobalUser) getActivity().getApplicationContext();
        global_user.setCurrentUser(item.username);
        global_user.setHomeFragmentId(1);
        // Go to Event Activity
        Intent intent = new Intent(getActivity(),HomeActivity.class);
        getActivity().startActivity(intent);
    }

}
