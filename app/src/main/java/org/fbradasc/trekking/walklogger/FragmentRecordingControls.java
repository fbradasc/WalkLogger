/*
 * FragmentRecordingControls - Java Class for Android
 * Created by G.Capelli on 20/5/2016
 * This file is part of BasicAirData GPS Logger
 *
 * Copyright (C) 2011 BasicAirData
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
// TODO TODO TODO TODO TODO
package org.fbradasc.trekking.walklogger;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentRecordingControls extends Fragment{

    public FragmentRecordingControls() {
        // Required empty public constructor
    }

    TableLayout tableLayoutGeoPoints;
    TableLayout tableLayoutSteps;
    TableLayout tableLayoutPlacemarks;

    private TextView TVGeoPoints;
    private TextView TVSteps;
    private TextView TVPlacemarks;
    private TextView TVGeoPointsLabel;
    private TextView TVPlacemarksLabel;
    private ImageView IVDetectedActivity;

    final GPSApplication gpsApplication = GPSApplication.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording_controls, container, false);

        tableLayoutGeoPoints = (TableLayout) view.findViewById(R.id.id_TableLayout_GeoPoints);
        tableLayoutGeoPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ontoggleRecordGeoPoint(v);
            }
        });

        tableLayoutSteps = (TableLayout) view.findViewById(R.id.id_TableLayout_Steps);
        tableLayoutSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ontoggleRecordSteps(v);
            }
        });

        tableLayoutPlacemarks = (TableLayout) view.findViewById(R.id.id_TableLayout_Placemarks);
        tableLayoutPlacemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlacemarkRequest(v);
            }
        });

        TVGeoPoints = (TextView) view.findViewById(R.id.id_textView_GeoPoints);
        TVSteps = (TextView) view.findViewById(R.id.id_textView_Steps);
        TVPlacemarks = (TextView) view.findViewById(R.id.id_textView_Placemarks);
        TVGeoPointsLabel = (TextView) view.findViewById(R.id.id_textView_GeoPointsLabel);
        TVPlacemarksLabel = (TextView) view.findViewById(R.id.id_textView_PlacemarksLabel);
        IVDetectedActivity = (ImageView) view.findViewById(R.id.id_imageView_DetectedActivity);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Workaround for Nokia Devices, Android 9
        // https://github.com/BasicAirData/GPSLogger/issues/77
        if (EventBus.getDefault().isRegistered(this)) {
            //Log.w("myApp", "[#] FragmentRecordingControls - EventBus: FragmentRecordingControls already registered");
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
        Update();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void ontoggleRecordGeoPoint(View view) {
        if (isAdded()) {
            final Boolean grs = gpsApplication.getRecording();
            boolean newRecordingState = !grs;
            gpsApplication.setRecording(newRecordingState);
            EventBus.getDefault().post(EventBusMSG.UPDATE_TRACK);
            tableLayoutGeoPoints.setBackgroundColor(getResources().getColor(newRecordingState ? R.color.colorPrimary                        : R.color.colorRecControlBackground   ));
            tableLayoutSteps    .setBackgroundColor(getResources().getColor(newRecordingState ? R.color.colorPrimary                        : R.color.colorRecControlBackground   ));
            TVGeoPoints         .setTextColor      (getResources().getColor(newRecordingState ? R.color.textColorRecControlPrimary_Active   : R.color.textColorRecControlPrimary  ));
            TVGeoPointsLabel    .setTextColor      (getResources().getColor(newRecordingState ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));
        }
    }

    public void ontoggleRecordSteps(View view) {
        ontoggleRecordGeoPoint(view);
    }

    public void onPlacemarkRequest(View view) {
        if (isAdded()) {
            final Boolean pr = gpsApplication.getPlacemarkRequest();
            boolean newPlacemarkRequestState = !pr;
            gpsApplication.setPlacemarkRequest(newPlacemarkRequestState);
            tableLayoutPlacemarks.setBackgroundColor(getResources().getColor(newPlacemarkRequestState ? R.color.colorPrimary                        : R.color.colorRecControlBackground   ));
            TVPlacemarks         .setTextColor      (getResources().getColor(newPlacemarkRequestState ? R.color.textColorRecControlPrimary_Active   : R.color.textColorRecControlPrimary  ));
            TVPlacemarksLabel    .setTextColor      (getResources().getColor(newPlacemarkRequestState ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onEvent(Short msg) {
        if (msg == EventBusMSG.UPDATE_TRACK) {
            Update();
        }
    }

    public void Update() {
        if (isAdded()) {
            final Track   track = gpsApplication.getCurrentTrack    ();
            final Boolean grs   = gpsApplication.getRecording       ();
            final Boolean pr    = gpsApplication.getPlacemarkRequest();
            final int     da    = gpsApplication.getDetectedActivity();
            if (track != null) {
                if (TVGeoPoints           != null) TVGeoPoints          .setText           (String.valueOf(track.getNumberOfLocations ()));
                if (TVPlacemarks          != null) TVPlacemarks         .setText           (String.valueOf(track.getNumberOfPlacemarks()));
                if (TVSteps               != null) TVSteps              .setText           (String.valueOf(track.getNumberOfSteps     ()));
                if (IVDetectedActivity    != null) IVDetectedActivity   .setImageLevel     (da);
                if (tableLayoutGeoPoints  != null) tableLayoutGeoPoints .setBackgroundColor(getResources().getColor(grs ? R.color.colorPrimary                        : R.color.colorRecControlBackground   ));
                if (tableLayoutPlacemarks != null) tableLayoutPlacemarks.setBackgroundColor(getResources().getColor(pr  ? R.color.colorPrimary                        : R.color.colorRecControlBackground   ));
                if (tableLayoutSteps      != null) tableLayoutSteps     .setBackgroundColor(getResources().getColor(grs ? R.color.colorPrimary                        : R.color.colorRecControlBackground   ));
                if (TVPlacemarks          != null) TVPlacemarks         .setTextColor      (getResources().getColor(pr  ? R.color.textColorRecControlPrimary_Active   : R.color.textColorRecControlPrimary  ));
                if (TVPlacemarksLabel     != null) TVPlacemarksLabel    .setTextColor      (getResources().getColor(pr  ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));
                if (TVGeoPoints           != null) TVGeoPoints          .setTextColor      (getResources().getColor(grs ? R.color.textColorRecControlPrimary_Active   : R.color.textColorRecControlPrimary  ));
                if (TVGeoPointsLabel      != null) TVGeoPointsLabel     .setTextColor      (getResources().getColor(grs ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));
            }
        }
    }
}
