package com.example.smartparentalapp;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionListAdapter extends ArrayAdapter<Session> {

    private static final String TAG = "SessionListAdapter";
    private Context mContext;
    private int mResource;
    private List<Session> sessionList;

    public SessionListAdapter(@NonNull Context context, int resource, ArrayList<Session> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        sessionList = new ArrayList<>();

        for(int i = 0; i < objects.size(); i++) {
            sessionList.add(objects.get(i));
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Session currentSession = sessionList.get(position);
        int increasedPosition = position + 1;
        int totalTime = currentSession.getTotalTime();
        String title = "Session #" + increasedPosition;
        String startTime = "Start time --> " + currentSession.getStartTime();
        String endTime = "End time --> " + currentSession.getEndTime();
        String totalTimeForDisplay = String.format("%02d:%02d:%02d", totalTime / 3600, (totalTime % 3600) / 60, totalTime % 60);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView titleText = convertView.findViewById(R.id.sessionTitle);
        TextView startTimeText = convertView.findViewById(R.id.sessionStartTime);
        TextView endTimeText = convertView.findViewById(R.id.sessionEndTime);
        TextView totalTimeText = convertView.findViewById(R.id.sessionTotalTime);

        titleText.setText(title);
        startTimeText.setText(startTime);
        endTimeText.setText(endTime);
        totalTimeText.setText(totalTimeForDisplay);

        HashMap<String, Integer> sessionList = currentSession.getSessionList();

        if(!sessionList.isEmpty()) {

            LinearLayout mStatisticsFirstColumn = convertView.findViewById(R.id.linearLayoutLeft);
            LinearLayout mStatisticsSecondColumn = convertView.findViewById(R.id.linearLayoutRight);

            //Populate statistics
            for(String key : sessionList.keySet()) {
                final TextView sessionKey = new TextView(mContext);
                final TextView sessionDuration = new TextView(mContext);
                Integer itemTime = sessionList.get(key);
                String formattedTime = String.format("%02d:%02d:%02d", itemTime / 3600, (itemTime % 3600) / 60, itemTime % 60);

                sessionKey.setText(key);
                sessionDuration.setText(formattedTime);

                if(key.equals("One UI Home")) {
                    sessionKey.setText(R.string.homeScreenText);
                }
                if(key.equals("SmartParentalApp")) {
                    sessionKey.setText(R.string.smartParentApplicationText);
                }

                sessionKey.setPadding(0, 4, 0, 0);
                sessionKey.setGravity(Gravity.CENTER);
                sessionDuration.setPadding(0, 4, 0, 0);
                sessionDuration.setGravity(Gravity.CENTER);

                mStatisticsFirstColumn.addView(sessionKey);
                mStatisticsSecondColumn.addView(sessionDuration);
            }
        }



        return convertView;
    }
}
