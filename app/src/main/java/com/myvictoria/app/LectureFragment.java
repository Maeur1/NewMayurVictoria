package com.myvictoria.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class LectureFragment extends Fragment implements View.OnClickListener{

    Button submit;
    EditText input;
    TextView info, response;
    String room, type, day, start, end;
    Boolean found;
    ArrayList<String> strings = new ArrayList<>();
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lecture, container, false);
        initialise(rootView);
        TextView.OnEditorActionListener listen = new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return checkSearchLectureFirst();
            }
        };
        input.setOnEditorActionListener(listen);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bSubmit) {
            checkSearchLectureFirst();
        }
    }

    private boolean checkSearchLectureFirst(){
        String s = input.getText().toString();
        if (!s.matches("") && s.length() > 3) {
            searchLecture();
            return true;
        } else if (s.length() < 4) {
            listDataHeader.clear();
            listDataChild.clear();
            response.setText("Please enter at least 4 characters.");
            response.setVisibility(View.VISIBLE);
            return false;
        } else {
            listDataHeader.clear();
            listDataChild.clear();
            response.setText("Please enter a class.");
            response.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean processTime(Calendar currentTime, String s){
        Calendar startTime = Calendar.getInstance();
        switch (s.substring(s.lastIndexOf(" ")+1)){
            case "Monday":
                startTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case "Tuesday":
                startTime.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                break;
            case "Wednesday":
                startTime.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                break;
            case "Thursday":
                startTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                break;
            case "Friday":
                startTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                break;
        }
        String startOfLecture = s.substring(s.lastIndexOf(" ")-8, s.lastIndexOf(" ")-2);
        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startOfLecture.substring(0, 2).replaceAll("\\s","")));
        startTime.set(Calendar.MINUTE, Integer.parseInt(startOfLecture.substring(3, 5).replaceAll("\\s","")));
        if(startTime.get(Calendar.DAY_OF_WEEK) != currentTime.get(Calendar.DAY_OF_WEEK)){
            return true;
        }

        return true;
    }

    private void searchLecture(){
        listDataHeader.clear();
        listDataChild.clear();
        response.setText("");
        found = false;
        strings.clear();
        final ProgressDialog ringProgressDialog= ProgressDialog.show(getActivity(), "Please wait...", "Searching for Lectures...", true);
        AsyncTask<String, Void, ArrayList<String>> task = new AsyncTask<String, Void, ArrayList<String>>() {
            boolean malformed_data = false;

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                InputStream inputStream = getResources().openRawResource(R.raw.classdata);
                BufferedReader b = new BufferedReader(new InputStreamReader(inputStream));
                String search = params[0];
                ArrayList<String> strings2 = new ArrayList<>();
                String s;
                try {
                    while ((s = b.readLine()) != null) {
                        String[] parts = s.split("\\t");
                        if (parts[0].contains(search.toUpperCase())) {
                            if(parts.length < 8){
                                malformed_data = true;
                                break;
                            }
                            type = parts[2];
                            day = parts[4];
                            start = parts[5];
                            end = parts[6];
                            room = parts[7];
                            String helper = day;
                            for (int i = 0; i < day.length(); i++) {
                                char c = day.charAt(i);
                                switch (c) {
                                    case 'M':
                                        helper = "Monday";
                                        break;
                                    case 'T':
                                        helper = "Tuesday";
                                        break;
                                    case 'W':
                                        helper = "Wednesday";
                                        break;
                                    case 'R':
                                        helper = "Thursday";
                                        break;
                                    case 'F':
                                        helper = "Friday";
                                        break;
                                    case 'S':
                                        helper = "Saturday";
                                        break;
                                }
                                strings2.add(parts[0] + " " + type + " is in " + room + " at " + start + " on " + helper);
                            }
                            found = true;
                        }
                    }
                    return strings2;
                } catch (IOException e) {
                    Log.d("IOEXCEP", "File not found brada.");
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings2) {
                strings.clear();
                strings.addAll(strings2);
                Calendar currentTime = Calendar.getInstance();
                for(String s: strings2) {
                    String firstword = s.substring(0, s.indexOf(" "));
                    if(!listDataHeader.contains(firstword)){
                        listDataHeader.add(firstword);
                        listDataChild.put(firstword, new ArrayList<String>());
                    }
                    if(processTime(currentTime, s.substring(firstword.length(), s.length()))) {
                        listDataChild.get(firstword).add(s.substring(firstword.length(), s.length()));
                    }
                }
                if (!found) {
                    response.setVisibility(View.VISIBLE);
                    response.setText("There were no matches found.");
                } else {
                    listAdapter = new ExpandedListAdapter(getActivity().getApplicationContext(), listDataHeader, listDataChild);
                    expListView.setAdapter(listAdapter);
                    if(malformed_data){
                        response.setText("Some classes returned with incorrect data");
                        response.setVisibility(View.VISIBLE);
                    }else {
                        response.setVisibility(View.GONE);
                    }
                    expListView.setVisibility(View.VISIBLE);
                }
                ringProgressDialog.dismiss();
            }
        };
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        task.execute(input.getText().toString());
    }

    private void initialise(View view){
        submit = (Button) view.findViewById(R.id.bSubmit);
        input = (EditText) view.findViewById(R.id.etInput);
        info = (TextView) view.findViewById(R.id.tvInfo);
        response = (TextView) view.findViewById(R.id.tvResponse);
        response.setVisibility(View.GONE);
        submit.setOnClickListener(this);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        expListView.setVisibility(View.GONE);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<String, List<String>>();
    }


}
