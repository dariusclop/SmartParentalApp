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
        String startTime = currentSession.getStartTime();
        String endTime = currentSession.getEndTime();
        String displayDate = startTime + " <-> " + endTime;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView titleText = convertView.findViewById(R.id.sessionTitle);
        TextView datesText = convertView.findViewById(R.id.sessionStartEndTime);

        titleText.setText(title);
        datesText.setText(displayDate);

        return convertView;
    }
}
