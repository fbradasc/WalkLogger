/**
 * EventBusMSG - Java Class for Android
 * Created by G.Capelli (BasicAirData) on 05/08/17.
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

public class EventBusMSG {

    static final short APP_RESUME                       =   1;  // Sent to components on app resume
    static final short APP_PAUSE                        =   2;  // Sent to components on app pause
    static final short NEW_TRACK                        =   3;  // Request to create a new track
    static final short UPDATE_FIX                       =   4;  // Notify that a new fix is available
    static final short UPDATE_TRACK                     =   5;  // Notify that the current track stats are updated
    static final short UPDATE_TRACKLIST                 =   6;  // Notify that the tracklist is changed
    static final short UPDATE_SETTINGS                  =   7;  // Tell that settings are changed
    static final short REQUEST_ADD_PLACEMARK            =   8;  // The user ask to add a placemark
    static final short ADD_PLACEMARK                    =   9;  // The placemark is available
    static final short APPLY_SETTINGS                   =  10;  // The new settings must be applied
    static final short TOAST_TRACK_EXPORTED             =  11;  // The exporter has finished to export the track, shows toast
    static final short UPDATE_JOB_PROGRESS              =  13;  // Update the progress of the current Job
    static final short NOTIFY_TRACKS_DELETED            =  14;  // Notify that some tracks are deleted
    static final short UPDATE_ACTIONBAR                 =  15;  // Notify that the actionbar must be updated
    static final short REFRESH_TRACKLIST                =  16;  // Refresh the tracklist, without update it from DB
    static final short REFRESH_TRACKTYPE                =  17;  // Refresh the track type on the Edit Details dialog

    static final short DELETE_TRACK                     =  20;  // Delete the track (given id)
    static final short EXPORT_TRACK                     =  21;  // Export the track (given id)
    static final short VIEW_TRACK                       =  22;  // View the track (given id)
    static final short SHARE_TRACK                      =  23;  // Share the track (given id)
    static final short TRACKLIST_DESELECT               =  24;  // The user deselect (into the tracklist) the track with a given id
    static final short TRACKLIST_SELECT                 =  25;  // The user select (into the tracklist) the track with a given id
    static final short INTENT_SEND                      =  26;  // Request to share
    static final short TOAST_UNABLE_TO_WRITE_THE_FILE   =  27;  // Exporter fails to export the Track (given id)

    static final short ACTIVITY_DETECTED                =  31;  // Sent to components on activity change detection

    static final short SHARE_PLACEMARKS                 =  40;  // Share the track's placemarks (given id)
    static final short INTENT_SEND_PLACEMARKS           =  41;  // Request to
    static final short TRACKLIST_RANGE_SELECTION        =  42;  // Select/Deselect a range of tracks
    static final short ACTION_EDIT_TRACK                =  45;  // Edit the selected track

    static final short ACTION_BULK_DELETE_TRACKS        =  50;  // Delete the selected tracks
    static final short ACTION_BULK_EXPORT_TRACKS        =  51;  // Export the selected tracks
    static final short ACTION_BULK_VIEW_TRACKS          =  52;  // View the selected tracks
    static final short ACTION_BULK_SHARE_TRACKS         =  53;  // Share the selected tracks
    static final short ACTION_BULK_SHARE_PLACEMARKS     =  54;  // Share the selected tracks
}
