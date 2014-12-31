package com.example.faceeventapp;

import com.example.faceeventapp.adapter.EventsAdapter;
import com.example.faceeventapp.model.EventsItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
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

import com.example.faceeventapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;


public class EventFragment extends Fragment implements AbsListView.OnItemClickListener {

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
    private List exampleEventsList;

    // TODO: Rename and change types of parameters
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
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
    public EventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GlobalUser global_user = (GlobalUser) getActivity().getApplicationContext();
        global_user.setCurrentUser(global_user.getUsername());

        exampleEventsList = new ArrayList<Event>();
        EventsItem events = new EventsItem();
        Log.d("Rashmi",events.length+"");
        for(int i=0;i<events.length;i++)
        {
            exampleEventsList.add(events.getEventItem(i));
        }
        mAdapter = new EventsAdapter(getActivity(),exampleEventsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
//        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event item = (Event) this.exampleEventsList.get(position);
        final GlobalUser global_user = (GlobalUser) getActivity().getApplicationContext();
        global_user.setEvent(item.getEventName());
        //Toast.makeText(getActivity(),global_user.getEvent() + " Clicked!", Toast.LENGTH_SHORT).show();
        // Go to Event Activity
        Intent intent = new Intent(getActivity(),EventActivity.class);
        getActivity().startActivity(intent);
    }

}
