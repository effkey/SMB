package com.example.here;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

public class MovingFragment extends Fragment {
    private View view;
    String[] items = {"Ostatnia aktywność","Tydzień","Miesiąc","Rok","Cały okres"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    public MovingFragment(){
        // require a empty public constructor
    }
    public void chooseActivePeriod(){
        autoCompleteTxt = view.findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item,items);
        autoCompleteTxt.setText(items[0]);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getActivity().getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_moving, container, false);;

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        chooseActivePeriod();
        return view;
    }
}
