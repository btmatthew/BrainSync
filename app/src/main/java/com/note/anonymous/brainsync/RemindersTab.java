package com.note.anonymous.brainsync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Created by Anonymous on 10/06/2015.
 */
public class RemindersTab extends Fragment {

    ListView reminderView;
    private DatabaseAdapter db;
    private String[] theNamesOfFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.reminders_tab, container, false);
        reminderView = (ListView) v.findViewById(R.id.reminderViewOnTab);
        db = new DatabaseAdapter(getActivity());
        theNamesOfFiles = db.getAllReminders();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, theNamesOfFiles);

        reminderView.setAdapter(adapter);
        return v;
    }


}
