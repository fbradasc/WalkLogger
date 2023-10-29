/*
 * DatabaseHandler - Java Class for Android
 * Created by G.Capelli on 1/5/2016
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.fbradasc.trekking.walklogger.GPSApplication.NOT_AVAILABLE;


/**
 * A SQLite Helper wrapper that allow to easily manage the database that the app
 * uses to store all the recorded data.
 * <br>
 * The GPS Logger database has three tables:
 * <ul>
 *     <li>The table of the Tracks</li>
 *     <li>The table of the locations (trackpoints)</li>
 *     <li>The table of the annotations (placemarks)</li>
 * </ul>
 */
class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables

    // Database Version
    private static final int DATABASE_VERSION = 6;          // Updated to 2 in v2.1.3 (code 14)
    private static final int LOCATION_TYPE_LOCATION = 1;
    private static final int LOCATION_TYPE_PLACEMARK = 2;

    // Database Name
    private static final String DATABASE_NAME = "WalkLogger";

    // -------------------------------------------------------------------------------- Table names
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_TRACKS = "tracks";
    private static final String TABLE_PLACEMARKS = "placemarks";

    // ----------------------------------------------------------------------- Common Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TRACK_ID = "track_id";

    // --------------------------------------------------------------- Location Table Columns names
    private static final String KEY_LOCATION_NUMBER = "nr";
    private static final String KEY_LOCATION_LATITUDE = "latitude";
    private static final String KEY_LOCATION_LONGITUDE = "longitude";
    private static final String KEY_LOCATION_ALTITUDE = "altitude";
    private static final String KEY_LOCATION_SPEED = "speed";
    private static final String KEY_LOCATION_ACCURACY = "accuracy";
    private static final String KEY_LOCATION_BEARING = "bearing";
    private static final String KEY_LOCATION_TIME = "time";
    private static final String KEY_LOCATION_NUMBEROFSATELLITES = "number_of_satellites";
    private static final String KEY_LOCATION_TYPE = "type";
    private static final String KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX = "number_of_satellites_used_in_fix";
    private static final String KEY_LOCATION_NUMBEROFSTEPS = "number_of_steps";
    private static final String KEY_LOCATION_ISNEWPATHSTART = "is_new_path_start";

    // ---------------------------------------------------------------------------- Placemarks adds
    private static final String KEY_LOCATION_NAME = "name";

    // ------------------------------------------------------------------ Track Table Columns names
    private static final String KEY_TRACK_NAME = "name";
    private static final String KEY_TRACK_FROM = "location_from";
    private static final String KEY_TRACK_TO = "location_to";


    private static final String KEY_TRACK_START_LATITUDE        = "start_latitude";
    private static final String KEY_TRACK_START_LONGITUDE       = "start_longitude";
    private static final String KEY_TRACK_START_ALTITUDE        = "start_altitude";
    private static final String KEY_TRACK_START_ACCURACY        = "start_accuracy";
    private static final String KEY_TRACK_START_SPEED           = "start_speed";
    private static final String KEY_TRACK_START_TIME            = "start_time";

    private static final String KEY_TRACK_LASTFIX_TIME = "lastfix_time";

    private static final String KEY_TRACK_END_LATITUDE = "end_latitude";
    private static final String KEY_TRACK_END_LONGITUDE = "end_longitude";
    private static final String KEY_TRACK_END_ALTITUDE = "end_altitude";
    private static final String KEY_TRACK_END_ACCURACY = "end_accuracy";
    private static final String KEY_TRACK_END_SPEED = "end_speed";
    private static final String KEY_TRACK_END_TIME = "end_time";

    private static final String KEY_TRACK_LASTSTEPDST_LATITUDE = "laststepdst_latitude";
    private static final String KEY_TRACK_LASTSTEPDST_LONGITUDE = "laststepdst_longitude";
    private static final String KEY_TRACK_LASTSTEPDST_ACCURACY = "laststepdst_accuracy";

    private static final String KEY_TRACK_LASTSTEPALT_ALTITUDE = "laststepalt_altitude";
    private static final String KEY_TRACK_LASTSTEPALT_ACCURACY = "laststepalt_accuracy";

    private static final String KEY_TRACK_MIN_LATITUDE = "min_latitude";
    private static final String KEY_TRACK_MIN_LONGITUDE = "min_longitude";

    private static final String KEY_TRACK_MAX_LATITUDE = "max_latitude";
    private static final String KEY_TRACK_MAX_LONGITUDE = "max_longitude";

    private static final String KEY_TRACK_DURATION = "duration";
    private static final String KEY_TRACK_DURATION_MOVING = "duration_moving";

    private static final String KEY_TRACK_DISTANCE = "distance";
    private static final String KEY_TRACK_DISTANCE_MOVING = "distance_moving";
    private static final String KEY_TRACK_DISTANCE_INPROGRESS = "distance_in_progress";
    private static final String KEY_TRACK_DISTANCE_LASTALTITUDE = "distance_last_altitude";

    private static final String KEY_TRACK_ALTITUDE_UP = "altitude_up";
    private static final String KEY_TRACK_ALTITUDE_DOWN = "altitude_down";
    private static final String KEY_TRACK_ALTITUDE_MIN = "altitude_min";
    private static final String KEY_TRACK_ALTITUDE_MAX = "altitude_max";
    private static final String KEY_TRACK_ALTITUDE_INPROGRESS = "altitude_in_progress";

    private static final String KEY_TRACK_SPEED_MAX = "speed_max";
    private static final String KEY_TRACK_SPEED_AVERAGE = "speed_average";
    private static final String KEY_TRACK_SPEED_AVERAGEMOVING = "speed_average_moving";

    private static final String KEY_TRACK_NUMBEROFLOCATIONS = "number_of_locations";
    private static final String KEY_TRACK_NUMBEROFSTEPS = "number_of_steps";
    private static final String KEY_TRACK_NUMBEROFPLACEMARKS = "number_of_placemarks";
    private static final String KEY_TRACK_TYPE = "type";

    private static final String KEY_TRACK_VALIDMAP = "validmap";


    private static final int I_ID = 0;
    private static final int I_TRACK_NAME = 1;
    private static final int I_TRACK_FROM = 2;
    private static final int I_TRACK_TO = 3;
    private static final int I_TRACK_START_LATITUDE = 4;
    private static final int I_TRACK_START_LONGITUDE = 5;
    private static final int I_TRACK_START_ALTITUDE = 6;
    private static final int I_TRACK_START_ACCURACY = 7;
    private static final int I_TRACK_START_SPEED = 8;
    private static final int I_TRACK_START_TIME = 9;
    private static final int I_TRACK_LASTFIX_TIME = 10;
    private static final int I_TRACK_END_LATITUDE = 11;
    private static final int I_TRACK_END_LONGITUDE = 12;
    private static final int I_TRACK_END_ALTITUDE = 13;
    private static final int I_TRACK_END_ACCURACY = 14;
    private static final int I_TRACK_END_SPEED = 15;
    private static final int I_TRACK_END_TIME = 16;
    private static final int I_TRACK_LASTSTEPDST_LATITUDE = 17;
    private static final int I_TRACK_LASTSTEPDST_LONGITUDE = 18;
    private static final int I_TRACK_LASTSTEPDST_ACCURACY = 19;
    private static final int I_TRACK_LASTSTEPALT_ALTITUDE = 20;
    private static final int I_TRACK_LASTSTEPALT_ACCURACY = 21;
    private static final int I_TRACK_MIN_LATITUDE = 22;
    private static final int I_TRACK_MIN_LONGITUDE = 23;
    private static final int I_TRACK_MAX_LATITUDE = 24;
    private static final int I_TRACK_MAX_LONGITUDE = 25;
    private static final int I_TRACK_DURATION = 26;
    private static final int I_TRACK_DURATION_MOVING = 27;
    private static final int I_TRACK_DISTANCE = 28;
    private static final int I_TRACK_DISTANCE_INPROGRESS = 29;
    private static final int I_TRACK_DISTANCE_LASTALTITUDE = 30;
    private static final int I_TRACK_ALTITUDE_UP = 31;
    private static final int I_TRACK_ALTITUDE_DOWN = 32;
    private static final int I_TRACK_ALTITUDE_INPROGRESS = 33;
    private static final int I_TRACK_SPEED_MAX = 34;
    private static final int I_TRACK_SPEED_AVERAGE = 35;
    private static final int I_TRACK_SPEED_AVERAGEMOVING = 36;
    private static final int I_TRACK_NUMBEROFLOCATIONS = 37;
    private static final int I_TRACK_NUMBEROFPLACEMARKS = 38;
    private static final int I_TRACK_VALIDMAP = 39;
    private static final int I_TRACK_TYPE = 40;
    private static final int I_TRACK_NUMBEROFSTEPS = 41;
    private static final int I_TRACK_ALTITUDE_MIN = 42;
    private static final int I_TRACK_ALTITUDE_MAX = 43;
    private static final int I_TRACK_DISTANCE_MOVING = 44;

    private static final int I_LOCATION_ID = 0;
    private static final int I_LOCATION_TRACK_ID = 1;
    private static final int I_LOCATION_NUMBER = 2;
    private static final int I_LOCATION_LATITUDE = 3;
    private static final int I_LOCATION_LONGITUDE = 4;
    private static final int I_LOCATION_ALTITUDE = 5;
    private static final int I_LOCATION_SPEED = 6;
    private static final int I_LOCATION_ACCURACY = 7;
    private static final int I_LOCATION_BEARING = 8;
    private static final int I_LOCATION_TIME = 9;
    private static final int I_LOCATION_NUMBEROFSATELLITES = 10;
    private static final int I_LOCATION_TYPE = 11;
    private static final int I_LOCATION_NUMBEROFSATELLITESUSEDINFIX = 12;
    private static final int I_LOCATION_NUMBEROFSTEPS = 13;
    private static final int I_LOCATION_ISNEWPATHSTART = 14;

    private static final int I_PLACEMARK_ID = 0;
    private static final int I_PLACEMARK_TRACK_ID = 1;
    private static final int I_PLACEMARK_NUMBER = 2;
    private static final int I_PLACEMARK_LATITUDE = 3;
    private static final int I_PLACEMARK_LONGITUDE = 4;
    private static final int I_PLACEMARK_ALTITUDE = 5;
    private static final int I_PLACEMARK_SPEED = 6;
    private static final int I_PLACEMARK_ACCURACY = 7;
    private static final int I_PLACEMARK_BEARING = 8;
    private static final int I_PLACEMARK_TIME = 9;
    private static final int I_PLACEMARK_NUMBEROFSATELLITES = 10;
    private static final int I_PLACEMARK_TYPE = 11;
    private static final int I_PLACEMARK_NAME = 12;
    private static final int I_PLACEMARK_NUMBEROFSATELLITESUSEDINFIX = 13;
    private static final int I_PLACEMARK_NUMBEROFSTEPS = 14;

    private boolean isLatLongListNewPathReached = false;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the tables to store Tracks, Locations, and Placemarks.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"    // 0
                + KEY_TRACK_NAME + " TEXT,"                         // 1
                + KEY_TRACK_FROM + " TEXT,"                         // 2
                + KEY_TRACK_TO + " TEXT,"                           // 3
                + KEY_TRACK_START_LATITUDE + " REAL,"               // 4
                + KEY_TRACK_START_LONGITUDE + " REAL,"              // 5
                + KEY_TRACK_START_ALTITUDE + " REAL,"               // 6
                + KEY_TRACK_START_ACCURACY + " REAL,"               // 7
                + KEY_TRACK_START_SPEED + " REAL,"                  // 8
                + KEY_TRACK_START_TIME + " REAL,"                   // 9
                + KEY_TRACK_LASTFIX_TIME + " REAL,"                 // 10
                + KEY_TRACK_END_LATITUDE + " REAL,"                 // 11
                + KEY_TRACK_END_LONGITUDE + " REAL,"                // 12
                + KEY_TRACK_END_ALTITUDE + " REAL,"                 // 13
                + KEY_TRACK_END_ACCURACY + " REAL,"                 // 14
                + KEY_TRACK_END_SPEED + " REAL,"                    // 15
                + KEY_TRACK_END_TIME + " REAL,"                     // 16
                + KEY_TRACK_LASTSTEPDST_LATITUDE + " REAL,"         // 17
                + KEY_TRACK_LASTSTEPDST_LONGITUDE + " REAL,"        // 18
                + KEY_TRACK_LASTSTEPDST_ACCURACY + " REAL,"         // 19
                + KEY_TRACK_LASTSTEPALT_ALTITUDE + " REAL,"         // 20
                + KEY_TRACK_LASTSTEPALT_ACCURACY + " REAL,"         // 21
                + KEY_TRACK_MIN_LATITUDE + " REAL,"                 // 22
                + KEY_TRACK_MIN_LONGITUDE + " REAL,"                // 23
                + KEY_TRACK_MAX_LATITUDE + " REAL,"                 // 24
                + KEY_TRACK_MAX_LONGITUDE + " REAL,"                // 25
                + KEY_TRACK_DURATION + " REAL,"                     // 26
                + KEY_TRACK_DURATION_MOVING + " REAL,"              // 27
                + KEY_TRACK_DISTANCE + " REAL,"                     // 28
                + KEY_TRACK_DISTANCE_INPROGRESS + " REAL,"          // 29
                + KEY_TRACK_DISTANCE_LASTALTITUDE + " REAL,"        // 30
                + KEY_TRACK_ALTITUDE_UP + " REAL,"                  // 31
                + KEY_TRACK_ALTITUDE_DOWN + " REAL,"                // 32
                + KEY_TRACK_ALTITUDE_INPROGRESS + " REAL,"          // 33
                + KEY_TRACK_SPEED_MAX + " REAL,"                    // 34
                + KEY_TRACK_SPEED_AVERAGE + " REAL,"                // 35
                + KEY_TRACK_SPEED_AVERAGEMOVING + " REAL,"          // 36
                + KEY_TRACK_NUMBEROFLOCATIONS + " INTEGER,"         // 37
                + KEY_TRACK_NUMBEROFPLACEMARKS + " INTEGER,"        // 38
                + KEY_TRACK_VALIDMAP + " INTEGER,"                  // 39
                + KEY_TRACK_TYPE + " INTEGER,"                      // 40
                + KEY_TRACK_NUMBEROFSTEPS + " INTEGER,"             // 41
                + KEY_TRACK_ALTITUDE_MIN + " REAL,"                 // 42
                + KEY_TRACK_ALTITUDE_MAX + " REAL,"                 // 43
                + KEY_TRACK_DISTANCE_MOVING + " REAL " + ")";       // 44
        db.execSQL(CREATE_TRACKS_TABLE);

        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"                // 0
                + KEY_TRACK_ID + " INTEGER,"                                    // 1
                + KEY_LOCATION_NUMBER + " INTEGER,"                             // 2
                + KEY_LOCATION_LATITUDE + " REAL,"                              // 3
                + KEY_LOCATION_LONGITUDE + " REAL,"                             // 4
                + KEY_LOCATION_ALTITUDE + " REAL,"                              // 5
                + KEY_LOCATION_SPEED + " REAL,"                                 // 6
                + KEY_LOCATION_ACCURACY + " REAL,"                              // 7
                + KEY_LOCATION_BEARING + " REAL,"                               // 8
                + KEY_LOCATION_TIME + " REAL,"                                  // 9
                + KEY_LOCATION_NUMBEROFSATELLITES + " INTEGER,"                 // 10
                + KEY_LOCATION_TYPE + " INTEGER,"                               // 11
                + KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX + " INTEGER,"        // 12
                + KEY_LOCATION_NUMBEROFSTEPS + " INTEGER,"                      // 13
                + KEY_LOCATION_ISNEWPATHSTART + " INTEGER " + ")";              // 14
        db.execSQL(CREATE_LOCATIONS_TABLE);

        String CREATE_PLACEMARKS_TABLE = "CREATE TABLE " + TABLE_PLACEMARKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"                // 0
                + KEY_TRACK_ID + " INTEGER,"                                    // 1
                + KEY_LOCATION_NUMBER + " INTEGER,"                             // 2
                + KEY_LOCATION_LATITUDE + " REAL,"                              // 3
                + KEY_LOCATION_LONGITUDE + " REAL,"                             // 4
                + KEY_LOCATION_ALTITUDE + " REAL,"                              // 5
                + KEY_LOCATION_SPEED + " REAL,"                                 // 6
                + KEY_LOCATION_ACCURACY + " REAL,"                              // 7
                + KEY_LOCATION_BEARING + " REAL,"                               // 8
                + KEY_LOCATION_TIME + " REAL,"                                  // 9
                + KEY_LOCATION_NUMBEROFSATELLITES + " INTEGER,"                 // 10
                + KEY_LOCATION_TYPE + " INTEGER,"                               // 11
                + KEY_LOCATION_NAME + " TEXT,"                                  // 12
                + KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX + " INTEGER,"        // 13
                + KEY_LOCATION_NUMBEROFSTEPS + " INTEGER" + ")";                // 14
        db.execSQL(CREATE_PLACEMARKS_TABLE);
    }


    private static final int NONE = 0;

    private static final String DATABASE_ALTER_TABLE_LOCATIONS_TO_V2 = "ALTER TABLE "
            + TABLE_LOCATIONS + " ADD COLUMN " + KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX + " INTEGER DEFAULT " +  NOT_AVAILABLE + ";";
    private static final String DATABASE_ALTER_TABLE_PLACEMARKS_TO_V2 = "ALTER TABLE "
            + TABLE_PLACEMARKS + " ADD COLUMN " + KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX + " INTEGER DEFAULT " +  NOT_AVAILABLE + ";";
    private static final String DATABASE_ALTER_TABLE_TRACKS_TO_V3 = "ALTER TABLE "
            + TABLE_TRACKS + " ADD COLUMN " + KEY_TRACK_NUMBEROFSTEPS + " INTEGER DEFAULT " +  NONE + ";";
    private static final String DATABASE_ALTER_TABLE_LOCATIONS_TO_V3 = "ALTER TABLE "
            + TABLE_LOCATIONS + " ADD COLUMN " + KEY_LOCATION_NUMBEROFSTEPS + " INTEGER DEFAULT " +  NONE + ";";
    private static final String DATABASE_ALTER_TABLE_PLACEMARKS_TO_V3 = "ALTER TABLE "
            + TABLE_PLACEMARKS + " ADD COLUMN " + KEY_LOCATION_NUMBEROFSTEPS + " INTEGER DEFAULT " +  NONE + ";";
    private static final String DATABASE_ALTER_TABLE_TRACKS_1_TO_V4 = "ALTER TABLE "
            + TABLE_TRACKS + " ADD COLUMN " + KEY_TRACK_ALTITUDE_MIN + " REAL DEFAULT " +  NOT_AVAILABLE + ";";
    private static final String DATABASE_ALTER_TABLE_TRACKS_2_TO_V4 = "ALTER TABLE "
            + TABLE_TRACKS + " ADD COLUMN " + KEY_TRACK_ALTITUDE_MAX + " REAL DEFAULT " +  NOT_AVAILABLE + ";";
    private static final String DATABASE_ALTER_TABLE_TRACKS_TO_V5 = "ALTER TABLE "
            + TABLE_TRACKS + " ADD COLUMN " + KEY_TRACK_DISTANCE_MOVING + " REAL DEFAULT " +  NOT_AVAILABLE + ";";
    private static final String DATABASE_ALTER_TABLE_LOCATIONS_TO_V6 = "ALTER TABLE "
            + TABLE_LOCATIONS + " ADD COLUMN " + KEY_LOCATION_ISNEWPATHSTART + " INTEGER DEFAULT 0;";

    /**
     * Upgrade the database version, altering the corresponding tables.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Use this function in case of DB version upgrade.

        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACEMARKS);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);

        // Create tables again
        //onCreate(db);

        switch (oldVersion)
        {
            case 1:
                //upgrade from version 1 to 2
                //Log.w("myApp", "[#] DatabaseHandler.java - onUpgrade: from version 1 to 2 ...");
                db.execSQL(DATABASE_ALTER_TABLE_LOCATIONS_TO_V2);
                db.execSQL(DATABASE_ALTER_TABLE_PLACEMARKS_TO_V2);
            case 2:
                //upgrade from version 2 to 3
                //Log.w("myApp", "[#] DatabaseHandler.java - onUpgrade: from version 2 to 3 ...");
                db.execSQL(DATABASE_ALTER_TABLE_TRACKS_TO_V3);
                db.execSQL(DATABASE_ALTER_TABLE_LOCATIONS_TO_V3);
                db.execSQL(DATABASE_ALTER_TABLE_PLACEMARKS_TO_V3);
            case 3:
                //upgrade from version 3 to 4
                db.execSQL(DATABASE_ALTER_TABLE_TRACKS_1_TO_V4);
                db.execSQL(DATABASE_ALTER_TABLE_TRACKS_2_TO_V4);
            case 4:
                //upgrade from version 4 to 5
                db.execSQL(DATABASE_ALTER_TABLE_TRACKS_TO_V5);
            case 5:
                //upgrade from version 5 to 6
                db.execSQL(DATABASE_ALTER_TABLE_LOCATIONS_TO_V6);

                //and so on.. do not add breaks so that switch will
                //start at oldVersion, and run straight through to the latest
        }
        //Log.w("myApp", "[#] DatabaseHandler.java - onUpgrade: DB upgraded to version " + newVersion);
    }

// ----------------------------------------------------------------------- LOCATIONS AND PLACEMARKS

    /**
     * Updates a track record using the given Track data.
     *
     * @param track the Track containing the new values to store
     */
    public void updateTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues trkvalues = new ContentValues();
        trkvalues.put(KEY_TRACK_NAME, track.getName());

        trkvalues.put(KEY_TRACK_FROM, "");
        trkvalues.put(KEY_TRACK_TO, "");

        trkvalues.put(KEY_TRACK_START_LATITUDE, track.getLatitudeStart());
        trkvalues.put(KEY_TRACK_START_LONGITUDE, track.getLongitudeStart());
        trkvalues.put(KEY_TRACK_START_ALTITUDE, track.getAltitudeStart());
        trkvalues.put(KEY_TRACK_START_ACCURACY, track.getAccuracyStart());
        trkvalues.put(KEY_TRACK_START_SPEED, track.getSpeedStart());
        trkvalues.put(KEY_TRACK_START_TIME, track.getTimeStart());

        trkvalues.put(KEY_TRACK_LASTFIX_TIME, track.getTimeLastFix());

        trkvalues.put(KEY_TRACK_END_LATITUDE, track.getLatitudeEnd());
        trkvalues.put(KEY_TRACK_END_LONGITUDE, track.getLongitudeEnd());
        trkvalues.put(KEY_TRACK_END_ALTITUDE, track.getAltitudeEnd());
        trkvalues.put(KEY_TRACK_END_ACCURACY, track.getAccuracyEnd());
        trkvalues.put(KEY_TRACK_END_SPEED, track.getSpeedEnd());
        trkvalues.put(KEY_TRACK_END_TIME, track.getTimeEnd());

        trkvalues.put(KEY_TRACK_LASTSTEPDST_LATITUDE, track.getLatitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_LONGITUDE, track.getLongitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_ACCURACY, track.getAccuracyLastStepDistance());

        trkvalues.put(KEY_TRACK_LASTSTEPALT_ALTITUDE, track.getAltitudeLastStepAltitude());
        trkvalues.put(KEY_TRACK_LASTSTEPALT_ACCURACY, track.getAccuracyLastStepAltitude());

        trkvalues.put(KEY_TRACK_MIN_LATITUDE, track.getLatitudeMin());
        trkvalues.put(KEY_TRACK_MIN_LONGITUDE, track.getLongitudeMin());

        trkvalues.put(KEY_TRACK_MAX_LATITUDE, track.getLatitudeMax());
        trkvalues.put(KEY_TRACK_MAX_LONGITUDE, track.getLongitudeMax());

        trkvalues.put(KEY_TRACK_DURATION, track.getDuration());
        trkvalues.put(KEY_TRACK_DURATION_MOVING, track.getDurationMoving());

        trkvalues.put(KEY_TRACK_DISTANCE, track.getDistance());
        trkvalues.put(KEY_TRACK_DISTANCE_MOVING, track.getDistanceMoving());
        trkvalues.put(KEY_TRACK_DISTANCE_INPROGRESS, track.getDistanceInProgress());
        trkvalues.put(KEY_TRACK_DISTANCE_LASTALTITUDE, track.getDistanceLastAltitude());

        trkvalues.put(KEY_TRACK_ALTITUDE_UP, track.getAltitudeUp());
        trkvalues.put(KEY_TRACK_ALTITUDE_DOWN, track.getAltitudeDown());
        trkvalues.put(KEY_TRACK_ALTITUDE_INPROGRESS, track.getAltitudeInProgress());
        trkvalues.put(KEY_TRACK_ALTITUDE_MIN, track.getAltitudeMin());
        trkvalues.put(KEY_TRACK_ALTITUDE_MAX, track.getAltitudeMax());

        trkvalues.put(KEY_TRACK_SPEED_MAX, track.getSpeedMax());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGE, track.getSpeedAverage());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGEMOVING, track.getSpeedAverageMoving());

        trkvalues.put(KEY_TRACK_NUMBEROFLOCATIONS, track.getNumberOfLocations());
        trkvalues.put(KEY_TRACK_NUMBEROFSTEPS, track.getNumberOfSteps());
        trkvalues.put(KEY_TRACK_NUMBEROFPLACEMARKS, track.getNumberOfPlacemarks());
        trkvalues.put(KEY_TRACK_TYPE, track.getType());

        trkvalues.put(KEY_TRACK_VALIDMAP, track.getValidMap());
        trkvalues.put(KEY_TRACK_DESCRIPTION, track.getDescription());

        try {
            db.beginTransaction();
            db.update(TABLE_TRACKS, trkvalues, KEY_ID + " = ?",
                    new String[] { String.valueOf(track.getId()) });    // Update the corresponding Track
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        //Log.w("myApp", "[#] DatabaseHandler.java - addLocation: Location " + track.getNumberOfLocations() + " added into track " + track.getID());
    }

    /**
     * Adds a new Location to a Track and update the corresponding Track table.
     * The two operations will be done in a single transaction, to avoid any data loss or corruption.
     *
     * @param location the location to add
     * @param track the Track that receives the location
     */
    public void addLocationToTrack(LocationExtended location, Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        Location loc = location.getLocation();

        ContentValues locvalues = new ContentValues();
        locvalues.put(KEY_TRACK_ID, track.getId());
        locvalues.put(KEY_LOCATION_NUMBER, track.getNumberOfLocations());
        locvalues.put(KEY_LOCATION_LATITUDE, loc.getLatitude());
        locvalues.put(KEY_LOCATION_LONGITUDE, loc.getLongitude());
        locvalues.put(KEY_LOCATION_ALTITUDE, loc.hasAltitude() ? loc.getAltitude() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_SPEED, loc.hasSpeed() ? loc.getSpeed() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_ACCURACY, loc.hasAccuracy() ? loc.getAccuracy() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_BEARING, loc.hasBearing() ? loc.getBearing() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_TIME, loc.getTime());
        locvalues.put(KEY_LOCATION_NUMBEROFSATELLITES, location.getNumberOfSatellites());
        locvalues.put(KEY_LOCATION_TYPE, LOCATION_TYPE_LOCATION);
        locvalues.put(KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX, location.getNumberOfSatellitesUsedInFix());
        locvalues.put(KEY_LOCATION_NUMBEROFSTEPS, location.getNumberOfSteps());
        locvalues.put(KEY_LOCATION_ISNEWPATHSTART, location.isNewPathStart() ?  1 : 0);

        ContentValues trkvalues = new ContentValues();
        trkvalues.put(KEY_TRACK_NAME, track.getName());

        trkvalues.put(KEY_TRACK_FROM, "");
        trkvalues.put(KEY_TRACK_TO, "");

        trkvalues.put(KEY_TRACK_START_LATITUDE, track.getLatitudeStart());
        trkvalues.put(KEY_TRACK_START_LONGITUDE, track.getLongitudeStart());
        trkvalues.put(KEY_TRACK_START_ALTITUDE, track.getAltitudeStart());
        trkvalues.put(KEY_TRACK_START_ACCURACY, track.getAccuracyStart());
        trkvalues.put(KEY_TRACK_START_SPEED, track.getSpeedStart());
        trkvalues.put(KEY_TRACK_START_TIME, track.getTimeStart());

        trkvalues.put(KEY_TRACK_LASTFIX_TIME, track.getTimeLastFix());

        trkvalues.put(KEY_TRACK_END_LATITUDE, track.getLatitudeEnd());
        trkvalues.put(KEY_TRACK_END_LONGITUDE, track.getLongitudeEnd());
        trkvalues.put(KEY_TRACK_END_ALTITUDE, track.getAltitudeEnd());
        trkvalues.put(KEY_TRACK_END_ACCURACY, track.getAccuracyEnd());
        trkvalues.put(KEY_TRACK_END_SPEED, track.getSpeedEnd());
        trkvalues.put(KEY_TRACK_END_TIME, track.getTimeEnd());

        trkvalues.put(KEY_TRACK_LASTSTEPDST_LATITUDE, track.getLatitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_LONGITUDE, track.getLongitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_ACCURACY, track.getAccuracyLastStepDistance());

        trkvalues.put(KEY_TRACK_LASTSTEPALT_ALTITUDE, track.getAltitudeLastStepAltitude());
        trkvalues.put(KEY_TRACK_LASTSTEPALT_ACCURACY, track.getAccuracyLastStepAltitude());

        trkvalues.put(KEY_TRACK_MIN_LATITUDE, track.getLatitudeMin());
        trkvalues.put(KEY_TRACK_MIN_LONGITUDE, track.getLongitudeMin());

        trkvalues.put(KEY_TRACK_MAX_LATITUDE, track.getLatitudeMax());
        trkvalues.put(KEY_TRACK_MAX_LONGITUDE, track.getLongitudeMax());

        trkvalues.put(KEY_TRACK_DURATION, track.getDuration());
        trkvalues.put(KEY_TRACK_DURATION_MOVING, track.getDurationMoving());

        trkvalues.put(KEY_TRACK_DISTANCE, track.getDistance());
        trkvalues.put(KEY_TRACK_DISTANCE_MOVING, track.getDistanceMoving());
        trkvalues.put(KEY_TRACK_DISTANCE_INPROGRESS, track.getDistanceInProgress());
        trkvalues.put(KEY_TRACK_DISTANCE_LASTALTITUDE, track.getDistanceLastAltitude());

        trkvalues.put(KEY_TRACK_ALTITUDE_UP, track.getAltitudeUp());
        trkvalues.put(KEY_TRACK_ALTITUDE_DOWN, track.getAltitudeDown());
        trkvalues.put(KEY_TRACK_ALTITUDE_INPROGRESS, track.getAltitudeInProgress());
        trkvalues.put(KEY_TRACK_ALTITUDE_MIN, track.getAltitudeMin());
        trkvalues.put(KEY_TRACK_ALTITUDE_MAX, track.getAltitudeMax());

        trkvalues.put(KEY_TRACK_SPEED_MAX, track.getSpeedMax());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGE, track.getSpeedAverage());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGEMOVING, track.getSpeedAverageMoving());

        trkvalues.put(KEY_TRACK_NUMBEROFLOCATIONS, track.getNumberOfLocations());
        trkvalues.put(KEY_TRACK_NUMBEROFSTEPS, track.getNumberOfSteps());
        trkvalues.put(KEY_TRACK_NUMBEROFPLACEMARKS, track.getNumberOfPlacemarks());
        trkvalues.put(KEY_TRACK_TYPE, track.getType());

        trkvalues.put(KEY_TRACK_VALIDMAP, track.getValidMap());

        try {
            db.beginTransaction();
            db.insert(TABLE_LOCATIONS, null, locvalues);                // Insert the new Location
            db.update(TABLE_TRACKS, trkvalues, KEY_ID + " = ?",
                    new String[] { String.valueOf(track.getId()) });    // Update the corresponding Track
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        //Log.w("myApp", "[#] DatabaseHandler.java - addLocation: Location " + track.getNumberOfLocations() + " added into track " + track.getID());
    }

    /**
     * Adds a new Annotation (Placemark) to a Track and update the corresponding Track table.
     * The two operations will be done in a single transaction, to avoid any data loss or corruption.
     *
     * @param placemark the placemark to add
     * @param track the Track that receives the placemark
     */
    public void addPlacemarkToTrack(LocationExtended placemark, Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        Location loc = placemark.getLocation();

        ContentValues locvalues = new ContentValues();
        locvalues.put(KEY_TRACK_ID, track.getId());
        locvalues.put(KEY_LOCATION_NUMBER, track.getNumberOfPlacemarks());
        locvalues.put(KEY_LOCATION_LATITUDE, loc.getLatitude());
        locvalues.put(KEY_LOCATION_LONGITUDE, loc.getLongitude());
        locvalues.put(KEY_LOCATION_ALTITUDE, loc.hasAltitude() ? loc.getAltitude() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_SPEED, loc.hasSpeed() ? loc.getSpeed() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_ACCURACY, loc.hasAccuracy() ? loc.getAccuracy() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_BEARING, loc.hasBearing() ? loc.getBearing() : NOT_AVAILABLE);
        locvalues.put(KEY_LOCATION_TIME, loc.getTime());
        locvalues.put(KEY_LOCATION_NUMBEROFSATELLITES, placemark.getNumberOfSatellites());
        locvalues.put(KEY_LOCATION_TYPE, LOCATION_TYPE_PLACEMARK);
        locvalues.put(KEY_LOCATION_NAME, placemark.getDescription());
        locvalues.put(KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX, placemark.getNumberOfSatellitesUsedInFix());
        locvalues.put(KEY_LOCATION_NUMBEROFSTEPS, placemark.getNumberOfSteps());

        ContentValues trkvalues = new ContentValues();
        trkvalues.put(KEY_TRACK_NAME, track.getName());
        trkvalues.put(KEY_TRACK_FROM, "");
        trkvalues.put(KEY_TRACK_TO, "");

        trkvalues.put(KEY_TRACK_START_LATITUDE, track.getLatitudeStart());
        trkvalues.put(KEY_TRACK_START_LONGITUDE, track.getLongitudeStart());
        trkvalues.put(KEY_TRACK_START_ALTITUDE, track.getAltitudeStart());
        trkvalues.put(KEY_TRACK_START_ACCURACY, track.getAccuracyStart());
        trkvalues.put(KEY_TRACK_START_SPEED, track.getSpeedStart());
        trkvalues.put(KEY_TRACK_START_TIME, track.getTimeStart());

        trkvalues.put(KEY_TRACK_LASTFIX_TIME, track.getTimeLastFix());

        trkvalues.put(KEY_TRACK_END_LATITUDE, track.getLatitudeEnd());
        trkvalues.put(KEY_TRACK_END_LONGITUDE, track.getLongitudeEnd());
        trkvalues.put(KEY_TRACK_END_ALTITUDE, track.getAltitudeEnd());
        trkvalues.put(KEY_TRACK_END_ACCURACY, track.getAccuracyEnd());
        trkvalues.put(KEY_TRACK_END_SPEED, track.getSpeedEnd());
        trkvalues.put(KEY_TRACK_END_TIME, track.getTimeEnd());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_LATITUDE, track.getLatitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_LONGITUDE, track.getLongitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_ACCURACY, track.getAccuracyLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPALT_ALTITUDE, track.getAltitudeLastStepAltitude());
        trkvalues.put(KEY_TRACK_LASTSTEPALT_ACCURACY, track.getAccuracyLastStepAltitude());
        trkvalues.put(KEY_TRACK_MIN_LATITUDE, track.getLatitudeMin());
        trkvalues.put(KEY_TRACK_MIN_LONGITUDE, track.getLongitudeMin());
        trkvalues.put(KEY_TRACK_MAX_LATITUDE, track.getLatitudeMax());
        trkvalues.put(KEY_TRACK_MAX_LONGITUDE, track.getLongitudeMax());

        trkvalues.put(KEY_TRACK_DURATION, track.getDuration());
        trkvalues.put(KEY_TRACK_DURATION_MOVING, track.getDurationMoving());

        trkvalues.put(KEY_TRACK_DISTANCE, track.getDistance());
        trkvalues.put(KEY_TRACK_DISTANCE_MOVING, track.getDistanceMoving());
        trkvalues.put(KEY_TRACK_DISTANCE_INPROGRESS, track.getDistanceInProgress());
        trkvalues.put(KEY_TRACK_DISTANCE_LASTALTITUDE, track.getDistanceLastAltitude());

        trkvalues.put(KEY_TRACK_ALTITUDE_UP, track.getAltitudeUp());
        trkvalues.put(KEY_TRACK_ALTITUDE_DOWN, track.getAltitudeDown());
        trkvalues.put(KEY_TRACK_ALTITUDE_INPROGRESS, track.getAltitudeInProgress());

        trkvalues.put(KEY_TRACK_SPEED_MAX, track.getSpeedMax());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGE, track.getSpeedAverage());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGEMOVING, track.getSpeedAverageMoving());

        trkvalues.put(KEY_TRACK_NUMBEROFLOCATIONS, track.getNumberOfLocations());
        trkvalues.put(KEY_TRACK_NUMBEROFSTEPS, track.getNumberOfSteps());
        trkvalues.put(KEY_TRACK_NUMBEROFPLACEMARKS, track.getNumberOfPlacemarks());
        trkvalues.put(KEY_TRACK_TYPE, track.getType());

        trkvalues.put(KEY_TRACK_VALIDMAP, track.getValidMap());

        try {
            db.beginTransaction();
            db.insert(TABLE_PLACEMARKS, null, locvalues);         // Insert the new Placemark
            db.update(TABLE_TRACKS, trkvalues, KEY_ID + " = ?",
                    new String[] { String.valueOf(track.getId()) });    // Update the corresponding Track
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        //Log.w("myApp", "[#] DatabaseHandler.java - addLocation: Location " + track.getNumberOfLocations() + " added into track " + track.getID());
    }


    // NOT USED, Commented out
    // Get single Location
//    public LocationExtended getLocation(long id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        LocationExtended extdloc = null;
//        double lcdata_double;
//        float lcdata_float;
//
//        Cursor cursor = db.query(TABLE_LOCATIONS, new String[] {KEY_ID,
//                        KEY_LOCATION_LATITUDE,
//                        KEY_LOCATION_LONGITUDE,
//                        KEY_LOCATION_ALTITUDE,
//                        KEY_LOCATION_SPEED,
//                        KEY_LOCATION_ACCURACY,
//                        KEY_LOCATION_BEARING,
//                        KEY_LOCATION_TIME,
//                        KEY_LOCATION_NUMBEROFSATELLITES,
//                        KEY_LOCATION_NUMBEROFSATELLITESUSEDINFIX}, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//
//            Location lc = new Location("DB");
//            lc.setLatitude(cursor.getDouble(1));
//            lc.setLongitude(cursor.getDouble(2));
//
//            lcdata_double = cursor.getDouble(3);
//            if (lcdata_double != NOT_AVAILABLE) lc.setAltitude(lcdata_double);
//            //else lc.removeAltitude();
//
//            lcdata_float = cursor.getFloat(4);
//            if (lcdata_float != NOT_AVAILABLE) lc.setSpeed(lcdata_float);
//            //else lc.removeSpeed();
//
//            lcdata_float = cursor.getFloat(5);
//            if (lcdata_float != NOT_AVAILABLE) lc.setAccuracy(lcdata_float);
//            //else lc.removeAccuracy();
//
//            lcdata_float = cursor.getFloat(6);
//            if (lcdata_float != NOT_AVAILABLE) lc.setBearing(lcdata_float);
//            //else lc.removeBearing();
//
//            lc.setTime(cursor.getLong(7));
//
//
//            extdloc = new LocationExtended(lc);
//            extdloc.setNumberOfSatellites(cursor.getInt(8));
//            extdloc.setNumberOfSatellitesUsedInFix(cursor.getInt(9));
//
//            cursor.close();
//        }
//        return extdloc != null ? extdloc : null;
//    }


    /**
     * Returns a list of Locations associated to a specified Track,
     * with Location ID from startNumber to endNumber.
     * Both limits are included.
     *
     * @param trackID the ID of the Track
     * @param startNumber the start number of the location (included)
     * @param endNumber the end number of the location (included)
     *
     * @return the list of Locations
     */
    public List<LocationExtended> getLocationsList(long trackID, long startNumber, long endNumber) {
        List<LocationExtended> locationList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " WHERE "
                + KEY_TRACK_ID + " = " + trackID + " AND "
                + KEY_LOCATION_NUMBER + " BETWEEN " + startNumber + " AND " + endNumber
                + " ORDER BY " + KEY_LOCATION_NUMBER;

        //Log.w("myApp", "[#] DatabaseHandler.java - getLocationList(" + trackID + ", " + startNumber + ", " +endNumber + ") ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        double lcdata_double;
        float lcdata_float;

        if (cursor != null) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Location lc = new Location("DB");
                    lc.setLatitude(cursor.getDouble(I_LOCATION_LATITUDE));
                    lc.setLongitude(cursor.getDouble(I_LOCATION_LONGITUDE));

                    lcdata_double = cursor.getDouble(I_LOCATION_ALTITUDE);
                    if (lcdata_double != NOT_AVAILABLE) lc.setAltitude(lcdata_double);
                    //else lc.removeAltitude();

                    lcdata_float = cursor.getFloat(I_LOCATION_SPEED);
                    if (lcdata_float != NOT_AVAILABLE) lc.setSpeed(lcdata_float);
                    //else lc.removeSpeed();

                    lcdata_float = cursor.getFloat(I_LOCATION_ACCURACY);
                    if (lcdata_float != NOT_AVAILABLE) lc.setAccuracy(lcdata_float);
                    //else lc.removeAccuracy();

                    lcdata_float = cursor.getFloat(I_LOCATION_BEARING);
                    if (lcdata_float != NOT_AVAILABLE) lc.setBearing(lcdata_float);
                    //else lc.removeBearing();

                    lc.setTime(cursor.getLong(I_LOCATION_TIME));

                    LocationExtended extdloc = new LocationExtended(lc);
                    extdloc.setNumberOfSatellites(cursor.getInt(I_LOCATION_NUMBEROFSATELLITES));
                    extdloc.setNumberOfSatellitesUsedInFix(cursor.getInt(I_LOCATION_NUMBEROFSATELLITESUSEDINFIX));
                    extdloc.setNumberOfSteps(cursor.getInt(I_LOCATION_NUMBEROFSTEPS));
                    extdloc.isNewPathStart(cursor.getInt(I_LOCATION_ISNEWPATHSTART)==1);

                    locationList.add(extdloc); // Add Location to list
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return locationList;
    }

    /**
     * Returns a list of Annotations (Placemarks) associated to a specified Track,
     * with Placemark ID from startNumber to endNumber.
     * Both limits are included.
     *
     * @param trackID the ID of the Track
     * @param startNumber the start number of the placemark (included)
     * @param endNumber the end number of the placemark (included)
     *
     * @return the list of placemarks
     */
    public List<LocationExtended> getPlacemarksList(long trackID, long startNumber, long endNumber) {

        List<LocationExtended> placemarkList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_PLACEMARKS + " WHERE "
                + KEY_TRACK_ID + " = " + trackID + " AND "
                + KEY_LOCATION_NUMBER + " BETWEEN " + startNumber + " AND " + endNumber
                + " ORDER BY " + KEY_LOCATION_NUMBER;

        //Log.w("myApp", "[#] DatabaseHandler.java - getLocationList(" + trackID + ", " + startNumber + ", " +endNumber + ") ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        double lcdata_double;
        float lcdata_float;

        if (cursor != null) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Location lc = new Location("DB");
                    lc.setLatitude(cursor.getDouble(I_PLACEMARK_LATITUDE));
                    lc.setLongitude(cursor.getDouble(I_PLACEMARK_LONGITUDE));

                    lcdata_double = cursor.getDouble(I_PLACEMARK_ALTITUDE);
                    if (lcdata_double != NOT_AVAILABLE) lc.setAltitude(lcdata_double);
                    //else lc.removeAltitude();

                    lcdata_float = cursor.getFloat(I_PLACEMARK_SPEED);
                    if (lcdata_float != NOT_AVAILABLE) lc.setSpeed(lcdata_float);
                    //else lc.removeSpeed();

                    lcdata_float = cursor.getFloat(I_PLACEMARK_ACCURACY);
                    if (lcdata_float != NOT_AVAILABLE) lc.setAccuracy(lcdata_float);
                    //else lc.removeAccuracy();

                    lcdata_float = cursor.getFloat(I_PLACEMARK_BEARING);
                    if (lcdata_float != NOT_AVAILABLE) lc.setBearing(lcdata_float);
                    //else lc.removeBearing();

                    lc.setTime(cursor.getLong(I_PLACEMARK_TIME));

                    LocationExtended extdloc = new LocationExtended(lc);
                    extdloc.setNumberOfSatellites(cursor.getInt(I_PLACEMARK_NUMBEROFSATELLITES));
                    extdloc.setNumberOfSatellitesUsedInFix(cursor.getInt(I_PLACEMARK_NUMBEROFSATELLITESUSEDINFIX));
                    extdloc.setNumberOfSteps(cursor.getInt(I_PLACEMARK_NUMBEROFSTEPS));
                    extdloc.setDescription(cursor.getString(I_PLACEMARK_NAME));

                    placemarkList.add(extdloc); // Add Location to list
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return placemarkList;
    }

    /**
     * Returns a list of LatLng (a subset of Location data that contains only Latitude and Longitude)
     * associated to a specified Placemark, with Location ID from startNumber to endNumber.
     * Both limits are included.
     * <p>
     * This method is used to obtain the data for drawing the thumbnail of a track.
     *
     * @param trackID the ID of the Track
     * @param startNumber the start number of the corresponding location (included)
     * @param endNumber the end number of the corresponding location (included)
     *
     * @return the list of LatLng
     */
    public List<LatLng> getPlacemarksLatLngList(long trackID, long startNumber, long endNumber) {

        List<LatLng> latlngList = new ArrayList<>();

        String selectQuery = "SELECT " + KEY_TRACK_ID + ","
                + KEY_LOCATION_LATITUDE + ","
                + KEY_LOCATION_LONGITUDE + ","
                + KEY_LOCATION_NUMBER
                + " FROM " + TABLE_PLACEMARKS + " WHERE "
                + KEY_TRACK_ID + " = " + trackID + " AND "
                + KEY_LOCATION_NUMBER + " BETWEEN " + startNumber + " AND " + endNumber
                + " ORDER BY " + KEY_LOCATION_NUMBER;

        //Log.w("myApp", "[#] DatabaseHandler.java - getLocationList(" + trackID + ", " + startNumber + ", " +endNumber + ") ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    LatLng latlng = new LatLng();
                    latlng.Latitude = cursor.getDouble(1);
                    latlng.Longitude = cursor.getDouble(2);

                    latlngList.add(latlng); // Add Location to list
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return latlngList;
    }

    public boolean isLatLongListNewPathReached() {
        return isLatLongListNewPathReached;
    }

    /**
     * Returns a list of LatLng (a subset of Location data that contains only Latitude and Longitude)
     * associated to a specified Track, with Location ID from startNumber to endNumber.
     * Both limits are included.
     * <p>
     * This method is used to obtain the data for drawing the thumbnail of a track.
     *
     * @param trackID the ID of the Track
     * @param startNumber the start number of the corresponding location (included)
     * @param endNumber the end number of the corresponding location (included)
     *
     * @return the list of LatLng
     */
    public List<LatLng> getLatLngList(long trackID, long startNumber, long endNumber) {

        List<LatLng> latlngList = new ArrayList<>();

        String selectQuery = "SELECT " + KEY_TRACK_ID + ","
                                       + KEY_LOCATION_LATITUDE + ","
                                       + KEY_LOCATION_LONGITUDE + ","
                                       + KEY_LOCATION_ISNEWPATHSTART + ","
                                       + KEY_LOCATION_NUMBER
                + " FROM " + TABLE_LOCATIONS + " WHERE "
                + KEY_TRACK_ID + " = " + trackID + " AND "
                + KEY_LOCATION_NUMBER + " BETWEEN " + startNumber + " AND " + endNumber
                + " ORDER BY " + KEY_LOCATION_NUMBER;

        //Log.w("myApp", "[#] DatabaseHandler.java - getLocationList(" + trackID + ", " + startNumber + ", " +endNumber + ") ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        isLatLongListNewPathReached = false;

        boolean can_break = false;

        if (cursor != null) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    LatLng latlng = new LatLng();
                    latlng.latitude = cursor.getDouble(1);
                    latlng.longitude = cursor.getDouble(2);

                    latlngList.add(latlng); // Add Location to list

                    if (can_break && (cursor.getInt(3) == 1)) {
                        isLatLongListNewPathReached = true;
                        break;
                    }
                    can_break = true;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return latlngList;
    }


    // NOT USED, Commented out
    // Get the total amount of Locations stored in the DB
//    public long getLocationsTotalCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        long result = cursor.getCount();
//        cursor.close();
//        return result;
//    }


    // NOT USED, Commented out
    // Get the number of Locations of a Track
//    public long getLocationsCount(long TrackID) {
//        String countQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " WHERE " + KEY_TRACK_ID + " = " + TrackID;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        long result = cursor.getCount();
//        cursor.close();
//        return result;
//    }


    // NOT USED, Commented out
    // Get last Location ID
//    public long getLastLocationID(long TrackID) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        long result = 0;
//
//        String query = "SELECT " + KEY_ID + " FROM " + TABLE_LOCATIONS +
//                " WHERE " + KEY_TRACK_ID + " = " + TrackID + " ORDER BY " + KEY_ID + " DESC LIMIT 1";
//
//        //Log.w("myApp", "[#] DatabaseHandler.java - getLastLocationID(): " + query);
//
//        Cursor cursor = db.rawQuery(query, null);
//
//        if (cursor != null) {
//            //Log.w("myApp", "[#] !null");
//            if (cursor.moveToFirst()) {
//                result = cursor.getLong(0);
//            } //else Log.w("myApp", "[#] DatabaseHandler.java - Location table is empty");
//            cursor.close();
//        }
//        //Log.w("myApp", "[#] RETURN getLastTrack()");
//        return result;
//    }




// ----------------------------------------------------------------------------------------- TRACKS

    /**
     * Deletes the track with the specified ID.
     * The method deletes also Placemarks and Locations associated to the specified track.
     *
     * @param trackID the ID of the Track
     */
    public void DeleteTrack(long trackID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE_PLACEMARKS, KEY_TRACK_ID + " = ?",
                    new String[] { String.valueOf(trackID) });    // Delete track's Placemarks
            db.delete(TABLE_LOCATIONS, KEY_TRACK_ID + " = ?",
                    new String[] { String.valueOf(trackID) });    // Delete track's Locations
            db.delete(TABLE_TRACKS, KEY_ID + " = ?",
                    new String[] { String.valueOf(trackID) });    // Delete track
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        //Log.w("myApp", "[#] DatabaseHandler.java - addLocation: Location " + track.getNumberOfLocations() + " added into track " + track.getID());
    }

    /**
     * Adds a new track to the Database.
     *
     * @param track the Track to be written into the new record
     *
     * @return the ID of the new Track
     */
    public long addTrack(Track track) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues trkvalues = new ContentValues();
        trkvalues.put(KEY_TRACK_NAME, track.getName());
        trkvalues.put(KEY_TRACK_FROM, "");
        trkvalues.put(KEY_TRACK_TO, "");

        trkvalues.put(KEY_TRACK_START_LATITUDE, track.getLatitudeStart());
        trkvalues.put(KEY_TRACK_START_LONGITUDE, track.getLongitudeStart());
        trkvalues.put(KEY_TRACK_START_ALTITUDE, track.getAltitudeStart());
        trkvalues.put(KEY_TRACK_START_ACCURACY, track.getAccuracyStart());
        trkvalues.put(KEY_TRACK_START_SPEED, track.getSpeedStart());
        trkvalues.put(KEY_TRACK_START_TIME, track.getTimeStart());

        trkvalues.put(KEY_TRACK_LASTFIX_TIME, track.getTimeLastFix());

        trkvalues.put(KEY_TRACK_END_LATITUDE, track.getLatitudeEnd());
        trkvalues.put(KEY_TRACK_END_LONGITUDE, track.getLongitudeEnd());
        trkvalues.put(KEY_TRACK_END_ALTITUDE, track.getAltitudeEnd());
        trkvalues.put(KEY_TRACK_END_ACCURACY, track.getAccuracyEnd());
        trkvalues.put(KEY_TRACK_END_SPEED, track.getSpeedEnd());
        trkvalues.put(KEY_TRACK_END_TIME, track.getTimeEnd());

        trkvalues.put(KEY_TRACK_LASTSTEPDST_LATITUDE, track.getLatitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_LONGITUDE, track.getLongitudeLastStepDistance());
        trkvalues.put(KEY_TRACK_LASTSTEPDST_ACCURACY, track.getAccuracyLastStepDistance());

        trkvalues.put(KEY_TRACK_LASTSTEPALT_ALTITUDE, track.getAltitudeLastStepAltitude());
        trkvalues.put(KEY_TRACK_LASTSTEPALT_ACCURACY, track.getAccuracyLastStepAltitude());

        trkvalues.put(KEY_TRACK_MIN_LATITUDE, track.getLatitudeMin());
        trkvalues.put(KEY_TRACK_MIN_LONGITUDE, track.getLongitudeMin());

        trkvalues.put(KEY_TRACK_MAX_LATITUDE, track.getLatitudeMax());
        trkvalues.put(KEY_TRACK_MAX_LONGITUDE, track.getLongitudeMax());

        trkvalues.put(KEY_TRACK_DURATION, track.getDuration());
        trkvalues.put(KEY_TRACK_DURATION_MOVING, track.getDurationMoving());

        trkvalues.put(KEY_TRACK_DISTANCE, track.getDistance());
        trkvalues.put(KEY_TRACK_DISTANCE_MOVING, track.getDistanceMoving());
        trkvalues.put(KEY_TRACK_DISTANCE_INPROGRESS, track.getDistanceInProgress());
        trkvalues.put(KEY_TRACK_DISTANCE_LASTALTITUDE, track.getDistanceLastAltitude());

        trkvalues.put(KEY_TRACK_ALTITUDE_UP, track.getAltitudeUp());
        trkvalues.put(KEY_TRACK_ALTITUDE_DOWN, track.getAltitudeDown());
        trkvalues.put(KEY_TRACK_ALTITUDE_INPROGRESS, track.getAltitudeInProgress());
        trkvalues.put(KEY_TRACK_ALTITUDE_MIN, track.getAltitudeMin());
        trkvalues.put(KEY_TRACK_ALTITUDE_MAX, track.getAltitudeMax());

        trkvalues.put(KEY_TRACK_SPEED_MAX, track.getSpeedMax());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGE, track.getSpeedAverage());
        trkvalues.put(KEY_TRACK_SPEED_AVERAGEMOVING, track.getSpeedAverageMoving());

        trkvalues.put(KEY_TRACK_NUMBEROFLOCATIONS, track.getNumberOfLocations());
        trkvalues.put(KEY_TRACK_NUMBEROFSTEPS, track.getNumberOfSteps());
        trkvalues.put(KEY_TRACK_NUMBEROFPLACEMARKS, track.getNumberOfPlacemarks());
        trkvalues.put(KEY_TRACK_TYPE, track.getType());

        trkvalues.put(KEY_TRACK_VALIDMAP, track.getValidMap());

        long TrackID;
        // Inserting Row
        TrackID = (db.insert(TABLE_TRACKS, null, trkvalues));

        //Log.w("myApp", "[#] DatabaseHandler.java - addTrack " + TrackID);

        return TrackID; // Insert this in the track ID !!!
    }

    private Track loadTrack(Cursor cursor) {
        Track track = new Track();
        track.FromDB (
            cursor.getLong(I_ID),
            cursor.getString(I_TRACK_NAME),
            cursor.getString(I_TRACK_FROM),
            cursor.getString(I_TRACK_TO),

            cursor.getDouble(I_TRACK_START_LATITUDE),
            cursor.getDouble(I_TRACK_START_LONGITUDE),
            cursor.getDouble(I_TRACK_START_ALTITUDE),
            cursor.getFloat(I_TRACK_START_ACCURACY),
            cursor.getFloat(I_TRACK_START_SPEED),
            cursor.getLong(I_TRACK_START_TIME),

            cursor.getLong(I_TRACK_LASTFIX_TIME),

            cursor.getDouble(I_TRACK_END_LATITUDE),
            cursor.getDouble(I_TRACK_END_LONGITUDE),
            cursor.getDouble(I_TRACK_END_ALTITUDE),
            cursor.getFloat(I_TRACK_END_ACCURACY),
            cursor.getFloat(I_TRACK_END_SPEED),
            cursor.getLong(I_TRACK_END_TIME),

            cursor.getDouble(I_TRACK_LASTSTEPDST_LATITUDE),
            cursor.getDouble(I_TRACK_LASTSTEPDST_LONGITUDE),
            cursor.getFloat(I_TRACK_LASTSTEPDST_ACCURACY),

            cursor.getDouble(I_TRACK_LASTSTEPALT_ALTITUDE),
            cursor.getFloat(I_TRACK_LASTSTEPALT_ACCURACY),

            cursor.getDouble(I_TRACK_MIN_LATITUDE),
            cursor.getDouble(I_TRACK_MIN_LONGITUDE),
            cursor.getDouble(I_TRACK_MAX_LATITUDE),
            cursor.getDouble(I_TRACK_MAX_LONGITUDE),

            cursor.getLong(I_TRACK_DURATION),
            cursor.getLong(I_TRACK_DURATION_MOVING),

            cursor.getFloat(I_TRACK_DISTANCE),
            cursor.getFloat(I_TRACK_DISTANCE_MOVING),
            cursor.getFloat(I_TRACK_DISTANCE_INPROGRESS),
            cursor.getLong(I_TRACK_DISTANCE_LASTALTITUDE),

            cursor.getDouble(I_TRACK_ALTITUDE_UP),
            cursor.getDouble(I_TRACK_ALTITUDE_DOWN),
            cursor.getDouble(I_TRACK_ALTITUDE_INPROGRESS),

            cursor.getDouble(I_TRACK_ALTITUDE_MIN),
            cursor.getDouble(I_TRACK_ALTITUDE_MAX),

            cursor.getFloat(I_TRACK_SPEED_MAX),
            cursor.getFloat(I_TRACK_SPEED_AVERAGE),
            cursor.getFloat(I_TRACK_SPEED_AVERAGEMOVING),

            cursor.getLong(I_TRACK_NUMBEROFLOCATIONS),
            cursor.getLong(I_TRACK_NUMBEROFPLACEMARKS),
            cursor.getLong(I_TRACK_NUMBEROFSTEPS),

            cursor.getInt(I_TRACK_VALIDMAP),
            cursor.getInt(I_TRACK_TYPE)
        );

        return track;
    }

    // Get Track
    public Track getTrack(long TrackID) {

        Track track = null;

        String selectQuery = "SELECT  * FROM " + TABLE_TRACKS + " WHERE "
                + KEY_ID + " = " + TrackID;

        //Log.w("myApp", "[#] DatabaseHandler.java - getTrackList(" + startNumber + ", " +endNumber + ") ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Log.w("myApp", "[#] DatabaseHandler.java - getTrack(" + TrackID + ")");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                track = loadTrack(cursor);
            }
            cursor.close();
        }
        return track;
    }


    // Get last TrackID
    public long getLastTrackID() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;

        String query = "SELECT " + KEY_ID + " FROM " + TABLE_TRACKS + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        //Log.w("myApp", "[#] DatabaseHandler.java - getLastTrackID(): " + query);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            //Log.w("myApp", "[#] !null");
            if (cursor.moveToFirst()) {
                result = cursor.getLong(0);
            } //else Log.w("myApp", "[#] Tracks table is empty");
            cursor.close();
        }
        //Log.w("myApp", "[#] RETURN getLastTrack()");
        //Log.w("myApp", "[#] DatabaseHandler.java - LastTrackID = " + result);
        return result;
    }


    // Get last TrackID
    public Track getLastTrack() {
        return getTrack(getLastTrackID());
    }


    // Getting a list of Tracks, with number between startNumber and endNumber
    // Please note that limits both are inclusive!
    public List<Track> getTracksList(long startNumber, long endNumber) {

        List<Track> trackList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_TRACKS + " WHERE "
                + KEY_ID + " BETWEEN " + startNumber + " AND " + endNumber
                + " ORDER BY " + KEY_ID + " DESC";

        //Log.w("myApp", "[#] DatabaseHandler.java - getTrackList(" + startNumber + ", " +endNumber + ") ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    trackList.add(loadTrack(cursor));             // Add Track to list
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return trackList;
    }


    public void CorrectGPSWeekRollover() {
        String CorrectLocationsQuery = "UPDATE " + TABLE_LOCATIONS + " SET " + KEY_LOCATION_TIME + " = " + KEY_LOCATION_TIME + " + 619315200000 WHERE "
                + KEY_LOCATION_TIME + " <= 1388534400000 ";               // 01/01/2014 00:00:00.000
        String CorrectPlacemarksQuery = "UPDATE " + TABLE_PLACEMARKS + " SET " + KEY_LOCATION_TIME + " = " + KEY_LOCATION_TIME + " + 619315200000 WHERE "
                + KEY_LOCATION_TIME + " <= 1388534400000 ";               // 01/01/2014 00:00:00.000
        String CorrectNamesQuery = "SELECT " + KEY_ID + "," + KEY_TRACK_NAME + " FROM " + TABLE_TRACKS + " WHERE "
                + KEY_TRACK_NAME + " LIKE '199%'";

        class IdAndName {
            long id;
            String Name;
        }

        ArrayList <IdAndName> Names = new ArrayList<>();

        //Log.w("myApp", "[#] DatabaseHandler.java - getTrackList(" + startNumber + ", " +endNumber + ") ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();

        // Correction of Locations
        db.execSQL(CorrectLocationsQuery);

        // Correction of Placemarks
        db.execSQL(CorrectPlacemarksQuery);


        // Correction of Track Names
        Cursor cursor = db.rawQuery(CorrectNamesQuery, null);

        if (cursor != null) {
            int i = 0;
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);  // date and time formatter
                    SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
                    try {
                        Date d = SDF.parse(cursor.getString(1));
                        long timeInMilliseconds = d.getTime();
                        long timeInMilliseconds_Corrected = timeInMilliseconds + 619315200000L;
                        String Name_Corrected = SDF.format(timeInMilliseconds_Corrected);
                        //Log.w("myApp", "[#] GPSApplication.java - NAME CORRECTED FROM " + cursor.getString(0) + " TO " + Name_Corrected);
                        IdAndName IN = new IdAndName();
                        IN.id = cursor.getLong(0);
                        IN.Name = Name_Corrected;
                        Names.add(IN);
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }
                    i++;
                } while (cursor.moveToNext());
            }
            Log.w("myApp", "[#] DatabaseHandler.java - CorrectGPSWeekRollover NAMES = " + i);
            cursor.close();
        }

        for (IdAndName N : Names) {
            Log.w("myApp", "[#] GPSApplication.java - CORRECTING TRACK " + N.id + " = " + N.Name);
            db.execSQL("UPDATE " + TABLE_TRACKS + " SET " + KEY_TRACK_NAME + " = \"" + N.Name + "\" WHERE " + KEY_ID + " = " + N.id);
        }
    }


    // Getting the list of all Tracks in the DB
    /* NOT USED, COMMENTED OUT !!
    public List<Track> getTracksList() {

        List<Track> trackList = new ArrayList<Track>();

        String selectQuery = "SELECT  * FROM " + TABLE_TRACKS
                + " ORDER BY " + KEY_ID + " DESC";

        //Log.w("myApp", "[#] DatabaseHandler.java - getTrackList() ==> " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    trackList.add(loadTrack(cursor));             // Add Track to list
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return trackList;
    } */
}
