/*
 * FragmentPlacemarkDialog - Java Class for Android
 * Created by G.Capelli on 9/7/2016
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
 */

package org.fbradasc.trekking.walklogger;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;

/**
 * The dialog that appears when the user adds a new Annotation (Placemark).
 */
public class FragmentPlacemarkDialog extends DialogFragment implements View.OnClickListener {

    EditText etDescription;
    EditText Film_Data_EditText;
    EditText P_Data_Sv_EditText;
    EditText P_Data_Ev_EditText;
    EditText P_Data_Fv_EditText;
    EditText P_Data_Tv_EditText;
    EditText P_Data_Av_EditText;
    RadioGroup  RG_ZoneSystem;
    RadioButton RB_ZS_0;
    RadioButton RB_ZS_1;
    RadioButton RB_ZS_2;
    RadioButton RB_ZS_3;
    RadioButton RB_ZS_4;
    RadioButton RB_ZS_5;
    RadioButton RB_ZS_6;
    RadioButton RB_ZS_7;
    RadioButton RB_ZS_8;
    RadioButton RB_ZS_9;
    RadioButton RB_ZS_10;

    static String mDesc      = "";
    static String mFilm_Data = "";
    static String mP_Data_Sv = "";
    static String mP_Data_Ev = "";
    static String mP_Data_Fv = "";
    static String mP_Data_Tv = "";
    static String mP_Data_Av = "";
    static String mP_Data_ZS = "";

    int lastChecked = -1;

    public void onClick(View v) {
        if (((RadioButton) v).isChecked()) {
            if (v.getId() == lastChecked) {
                RG_ZoneSystem.clearCheck();
            }
        }

        lastChecked = RG_ZoneSystem.getCheckedRadioButtonId();
    }

    //@SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder createPlacemarkAlert = new AlertDialog.Builder(getActivity());
        createPlacemarkAlert.setTitle(R.string.dlg_add_annotation);
        createPlacemarkAlert.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add_location_24dp, getActivity().getTheme()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = (View) inflater.inflate(R.layout.fragment_placemark_dialog, null);

        etDescription      = (EditText   ) view.findViewById(R.id.placemark_description);
        Film_Data_EditText = (EditText   ) view.findViewById(R.id.film_data);
        P_Data_Sv_EditText = (EditText   ) view.findViewById(R.id.pdata_speed);
        P_Data_Ev_EditText = (EditText   ) view.findViewById(R.id.pdata_ev);
        P_Data_Fv_EditText = (EditText   ) view.findViewById(R.id.pdata_fv);
        P_Data_Tv_EditText = (EditText   ) view.findViewById(R.id.pdata_tv);
        P_Data_Av_EditText = (EditText   ) view.findViewById(R.id.pdata_av);
        RG_ZoneSystem      = (RadioGroup ) view.findViewById(R.id.rg_zs);
        RB_ZS_0            = (RadioButton) view.findViewById(R.id.rb_zs_0);
        RB_ZS_1            = (RadioButton) view.findViewById(R.id.rb_zs_1);
        RB_ZS_2            = (RadioButton) view.findViewById(R.id.rb_zs_2);
        RB_ZS_3            = (RadioButton) view.findViewById(R.id.rb_zs_3);
        RB_ZS_4            = (RadioButton) view.findViewById(R.id.rb_zs_4);
        RB_ZS_5            = (RadioButton) view.findViewById(R.id.rb_zs_5);
        RB_ZS_6            = (RadioButton) view.findViewById(R.id.rb_zs_6);
        RB_ZS_7            = (RadioButton) view.findViewById(R.id.rb_zs_7);
        RB_ZS_8            = (RadioButton) view.findViewById(R.id.rb_zs_8);
        RB_ZS_9            = (RadioButton) view.findViewById(R.id.rb_zs_9);
        RB_ZS_10           = (RadioButton) view.findViewById(R.id.rb_zs_10);

        RB_ZS_0  .setOnClickListener(this);
        RB_ZS_1  .setOnClickListener(this);
        RB_ZS_2  .setOnClickListener(this);
        RB_ZS_3  .setOnClickListener(this);
        RB_ZS_4  .setOnClickListener(this);
        RB_ZS_5  .setOnClickListener(this);
        RB_ZS_6  .setOnClickListener(this);
        RB_ZS_7  .setOnClickListener(this);
        RB_ZS_8  .setOnClickListener(this);
        RB_ZS_9  .setOnClickListener(this);
        RB_ZS_10 .setOnClickListener(this);

        P_Data_Ev_EditText.postDelayed(new Runnable()
        {
            public void run()
            {
                if (isAdded()) {
                    if (!mFilm_Data.isEmpty()) Film_Data_EditText.setText(mFilm_Data);
                    if (!mP_Data_Sv.isEmpty()) P_Data_Sv_EditText.setText(mP_Data_Sv);
                    if (!mP_Data_Ev.isEmpty()) P_Data_Ev_EditText.setText(mP_Data_Ev);
                    if (!mP_Data_Tv.isEmpty()) P_Data_Tv_EditText.setText(mP_Data_Tv);
                    if (!mP_Data_Av.isEmpty()) P_Data_Av_EditText.setText(mP_Data_Av);
                    if (!mP_Data_Fv.isEmpty()) P_Data_Fv_EditText.setText(mP_Data_Fv);
                    if (!mDesc     .isEmpty()) etDescription     .setText(mDesc     );

                    RG_ZoneSystem.clearCheck();

                    if (mP_Data_ZS == "0"   ) { RB_ZS_0 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "I"   ) { RB_ZS_1 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "II"  ) { RB_ZS_2 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "III" ) { RB_ZS_3 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "IV"  ) { RB_ZS_4 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "V"   ) { RB_ZS_5 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "VI"  ) { RB_ZS_6 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "VII" ) { RB_ZS_7 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "VIII") { RB_ZS_8 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "IX"  ) { RB_ZS_9 .setChecked(true);}
                    else
                    if (mP_Data_ZS == "X"   ) { RB_ZS_10.setChecked(true);}

                    P_Data_Ev_EditText.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(P_Data_Ev_EditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 200);

        createPlacemarkAlert.setView(view)
                .setPositiveButton(R.string.dlg_button_add, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (isAdded()) {
                            mFilm_Data = Film_Data_EditText.getText().toString().trim();
                            mP_Data_Ev = P_Data_Ev_EditText.getText().toString().trim();
                            mP_Data_Tv = P_Data_Tv_EditText.getText().toString().trim();
                            mP_Data_Av = P_Data_Av_EditText.getText().toString().trim();
                            mP_Data_Fv = P_Data_Fv_EditText.getText().toString().trim();
                            mP_Data_Sv = P_Data_Sv_EditText.getText().toString().trim();
                            mDesc      = etDescription     .getText().toString().trim();
                            mP_Data_ZS = "";

                            switch (RG_ZoneSystem.getCheckedRadioButtonId())
                            {
                                case R.id.rb_zs_0 : mP_Data_ZS = "0"   ; break;
                                case R.id.rb_zs_1 : mP_Data_ZS = "I"   ; break;
                                case R.id.rb_zs_2 : mP_Data_ZS = "II"  ; break;
                                case R.id.rb_zs_3 : mP_Data_ZS = "III" ; break;
                                case R.id.rb_zs_4 : mP_Data_ZS = "IV"  ; break;
                                case R.id.rb_zs_5 : mP_Data_ZS = "V"   ; break;
                                case R.id.rb_zs_6 : mP_Data_ZS = "VI"  ; break;
                                case R.id.rb_zs_7 : mP_Data_ZS = "VII" ; break;
                                case R.id.rb_zs_8 : mP_Data_ZS = "VIII"; break;
                                case R.id.rb_zs_9 : mP_Data_ZS = "IX"  ; break;
                                case R.id.rb_zs_10: mP_Data_ZS = "X"   ; break;
                                default           : mP_Data_ZS = ""    ; break;
                            }

                            String placemarkDescription =
                                    mFilm_Data + "\n" +
                                    mP_Data_Sv + ","  +
                                    mP_Data_Ev + ","  +
                                    mP_Data_ZS + "\n" +
                                    mP_Data_Tv + ","  +
                                    mP_Data_Av + ","  +
                                    mP_Data_Fv + "\n" +
                                    mDesc;
                            final GPSApplication gpsApp = GPSApplication.getInstance();
                            gpsApp.setPlacemarkDescription(placemarkDescription.trim());
                            EventBus.getDefault().post(EventBusMSG.ADD_PLACEMARK);
                            //Log.w("myApp", "[#] FragmentPlacemarkDialog.java - posted ADD_PLACEMARK: " + placemarkDescription);
                        }
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return createPlacemarkAlert.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
