package com.note.anonymous.brainsync;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Anonymous on 10/06/2015.
 */
public class EntriesTab extends Fragment {

    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    private static final int SUBMENU1 = 3;
    private static final int SUBMENU2 = 4;
    private static final int SUBMENU3 = 5;
    private static final int GROUP1 = 6;
    private int sortingMethod;
    private String fileDirectory;
    //These are all created here so I could use them in multiple methods in this activity
    int itemSelectedCount = 0;
    ArrayList<String> selectedMenuItems = new ArrayList<>();
    CustomAdapter dataAdapter = null;
    private ArrayList<Filenames> fileNamesList;
    private Menu menu;
    private MenuItem item;
    private MenuItem item1;
    private DatabaseAdapter db;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.entries_tab, container, false);
        listView = (ListView) v.findViewById(R.id.listEntriesViewOnTab);
        fileDirectory = getString(R.string.directoryLocation);

        FloatingActionButton fabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_add))
                .withButtonColor(Color.GRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEntryActivity.class);
                startActivity(intent);
            }
        });
        createList();
        setHasOptionsMenu(true);
        return v;
    }

    protected void onRestart() {
        dataAdapter.notifyDataSetChanged();
        itemSelectedCount = 0;
        selectedMenuItems.clear();
        item = menu.findItem(R.id.deleteMenuButton);
        item1 = menu.findItem(R.id.search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        setTitle("Entries");
        createList();
    }

    //method used for purpose of creating view list
    private void createList() {

        sortingMethod = ((FragmentParentActivity) getActivity()).getSorting();

        db = new DatabaseAdapter(getActivity());
        fileNamesList = db.getAllData();
        switch (sortingMethod) {
            case 0:
                Collections.sort(fileNamesList, new Sorting.CustomComparatorByTitle());
                break;
            case 1:
                Collections.sort(fileNamesList, new Sorting.CustomComparatorByDateEditedYoungestToOldest());
                break;
            case 2:
                Collections.sort(fileNamesList, new Sorting.CustomComparatorByDateCreatedYoungestToOldest());
                break;
        }
        dataAdapter = new CustomAdapter(getActivity(), R.layout.list_entries_row, fileNamesList);

        listView.setAdapter(dataAdapter);
    }

    //Custom adapter used for purpose of creating custom adapter duh?
    private class CustomAdapter extends ArrayAdapter<Filenames> {

        private ArrayList<Filenames> fileList;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Filenames> fileList) {
            super(context, textViewResourceId, fileList);
            this.fileList = new ArrayList<>();
            this.fileList.addAll(fileList);
        }

        private class ViewHolder {
            TextView name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //LayoutInflater vi = (LayoutInflater)getSystemService(
                //        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_entries_row, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.listEntriesViewRow);
                convertView.setTag(holder);


                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (itemSelectedCount == 0) {
                            openItem(holder.name.getText().toString());
                        } else {
                            selectItem(v);
                        }

                    }
                });

                holder.name.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectItem(v);
                        return true;
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Filenames files = fileList.get(position);
            if (files.isSelected()) {
                holder.name.setBackgroundColor(Color.GRAY);
            } else {
                holder.name.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.name.setText(files.getFilename());
            holder.name.setTag(files);
            return convertView;
        }

        private void selectItem(View v) {

            item = menu.findItem(R.id.deleteMenuButton);
            item1 = menu.findItem(R.id.search);
            //--Playing around :P

            TextView tx = (TextView) v;
            Filenames filename = (Filenames) tx.getTag();
            if (!filename.isSelected()) {

                tx.setBackgroundColor(Color.GRAY);
                filename.setSelected(true);
                selectedMenuItems.add(filename.getFilename());
                itemSelectedCount = selectedMenuItems.size();
                if (itemSelectedCount == 0) {
                    setTitle("Entries");
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else if (itemSelectedCount == 1) {
                    setTitle("Items Selected " + itemSelectedCount);
                    item.setVisible(true);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                } else {
                    setTitle("Items Selected " + itemSelectedCount);
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            } else {
                tx.setBackgroundColor(Color.TRANSPARENT);
                filename.setSelected(false);
                int b = selectedMenuItems.indexOf(filename.getFilename());
                selectedMenuItems.remove(b);
                itemSelectedCount = selectedMenuItems.size();
                if (itemSelectedCount == 0) {
                    setTitle("Entries");
                    item.setVisible(false);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else if (itemSelectedCount == 1) {
                    setTitle("Items Selected " + itemSelectedCount);
                } else {
                    setTitle("Items Selected " + itemSelectedCount);

                }
            }
        }
    }

    //Method User for purpose of opening selected item
    private void openItem(String title) {
        Intent intent = new Intent(getActivity(), DisplaySelectedItem.class);
        intent.putExtra("EXTRA_MESSAGE", title);
        startActivity(intent);
    }

    //Method used for purpose of setting up a title of Fragment parent activity
    private void setTitle(String title) {
        ((FragmentParentActivity) getActivity()).setActionBarTitle(title);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_list_entries, menu);

        MenuItem searchItem = menu.findItem(R.id.search);


        SubMenu subMenu = menu.addSubMenu(0, Menu.NONE, 1, "Sort by").setIcon(R.drawable.ic_action_sort_by_size);

        subMenu.add(GROUP1, SUBMENU1, 1, "A-Z");
        subMenu.add(GROUP1, SUBMENU2, 2, "Edit date");
        subMenu.add(GROUP1, SUBMENU3, 3, "Creation date");


        searchItem.setVisible(true);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId()) {
            case R.id.search:
                ((FragmentParentActivity) getActivity()).onSearch();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent1 = new Intent(getActivity(), About.class);
                startActivity(intent1);
                break;
            case R.id.deleteMenuButton:
                deleteMethod();
                break;
            case SUBMENU1:
                setSorting(0);
                break;
            case SUBMENU2:
                setSorting(1);
                break;
            case SUBMENU3:
                setSorting(2);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteMethod() {
        //Carries out the delete action based on the size of the arraylist held by the variable itemSelectedCount
        for (int i = 0; i < itemSelectedCount; i++) {
            String fileName = selectedMenuItems.get(i);
            //Removes the file from Database
            db.deleteEntry(fileName);
            //Gets the name of the file at position i in the array list, concatenates it with the directory assigned to the File object
            File dir = new File(fileDirectory + fileName);
            dir.delete();
        }


        Toast.makeText(getActivity(), itemSelectedCount + " Item(s) Deleted", Toast.LENGTH_LONG).show();
        //Cleaning up action bar

        //Sends broadcast to widget to update the viewList
        ((FragmentParentActivity) getActivity()).sendBroadcastToWidget();
        onRestart();
    }

    //Method used for purpose of setting sorting preference
    private void setSorting(int sort) {
        ((FragmentParentActivity) getActivity()).setSorting(sort);
        onRestart();
    }


}
