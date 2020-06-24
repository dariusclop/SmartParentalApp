package com.example.smartparentalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
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
        String title = "Session #" + increasedPosition;
        String startTime = "Start time --> " + currentSession.getStartTime();
        String endTime = "End time --> " + currentSession.getEndTime();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView titleText = convertView.findViewById(R.id.sessionTitle);
        TextView startTimeText = convertView.findViewById(R.id.sessionStartTime);
        TextView endTimeText = convertView.findViewById(R.id.sessionEndTime);

        titleText.setText(title);
        startTimeText.setText(startTime);
        endTimeText.setText(endTime);

        return convertView;
    }
}
