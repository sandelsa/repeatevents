package com.example.springroll.database;

/**
 * Created by SpringRoll on 4/23/2016.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import library.UserFunctions;
import library.DateTimePickerDialog;
import library.WeekViewEvent;

/**
 * Created by SpringRoll on 3/30/2016.
 */
public class EventFragment extends Fragment{
    /** Class name for log messages. */
    private final static String LOG_TAG = EventFragment.class.getSimpleName();

    /** Extra Argument for event id*/
    public static final String EXTRA_EVENT_ID = "com.example.springroll.database.event_id";

    /** The UserFunction class to store and get calendar information from WAMP SERVER. */
    private UserFunctions functionManager;

    /**
     * JSON Response node names.
     **/
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";

    private WeekViewEvent mEvent;

    private boolean mSaveExists = false, mSave = false, mEdit = false, mCancel = false,mDelete = false, mAllDay;
    private Button mStartDateButton;
    private Button mEndDateButton;
    private Button mDoneDateButton;
    private Button mDeleteButton;
    private EditText mTitle, mLocation, mNotes;
    private Switch mAllDaySwitch;
    private Calendar calendar, mFromDate, mToDate, mDoneDate;
    private int year, month, day, hour,minute,am_pm = -2;
    //private RatingBar mPriority;

    private RadioGroup mPriority;
    //AlarmSender al;
    //private RadioButton zero, one, two, three, four, five;
    private CheckBox m,t,w,r,f,a,s, mCourse;

    private boolean edit = false;
    private boolean viewing = true;

    /**
     *
     * @param eventId
     * @return
     */
    public static EventFragment newInstance(long eventId) {
        Log.d(LOG_TAG,"newInstance...");
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_EVENT_ID, eventId);

        //Create a new Fragment
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate...");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        long eventId = (long)getArguments().getSerializable(EXTRA_EVENT_ID);
        mEvent = CalEventManager.get(getActivity()).getSingleEvent(eventId);
//        mAllDay = mEvent.isAllDay();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreateView...");
        View v = inflater.inflate(R.layout.activity_fragment_event,container,false);


            if (NavUtils.getParentActivityName(getActivity()) != null) {
                if(getActivity().getActionBar() != null)
                    this.getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }

        // Declare the component Title text filed
        mTitle = (EditText)v.findViewById(R.id.event_title);

        if(mEvent == null)
            System.out.println("event is null for some reason");

        if(mEvent.getName().length() > 0){
            mSaveExists = true;
        }

        if(mEvent.getName() != null) {
           mTitle.setText(mEvent.getName());
        }

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEvent.setName(s.toString());
                Log.d(LOG_TAG, "onText_title_Changed...");
                Log.i("onTextChanged",s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This space intentionally left blank
            }
        });//End Title object

        // Declare the component Location text filed
        mLocation = (EditText)v.findViewById(R.id.event_location);

        if(mEvent.getLocation() != null){
            mLocation.setText(mEvent.getLocation());
        }

        mLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEvent.setLocation(s.toString());
                Log.d(LOG_TAG, "onText_location_Changed...");
                Log.i("onTextChanged", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This space intentionally left blank
            }
        });// End Location object

        //
        mAllDaySwitch = (Switch)v.findViewById(R.id.switch1);
        mAllDaySwitch.setChecked(mAllDay);
        mAllDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAllDay = true;
                    mEvent.setAllDay(true);
                    mStartDateButton.setText(setString(am_pm, mStartDateButton, mAllDay));
                    mEndDateButton.setText(setString(am_pm, mEndDateButton, mAllDay));
                } else {
                    mAllDay = false;
                    mEvent.setAllDay(false);
                    mStartDateButton.setText(setString(am_pm, mStartDateButton, mAllDay));
                    mEndDateButton.setText(setString(am_pm, mEndDateButton, mAllDay));
                }
            }
        });

        mCourse = (CheckBox)v.findViewById(R.id.courseBox);
        if(mEvent.getmCourse() == 1){
            mCourse.setChecked(true);
        }else{
            mCourse.setChecked(false);
        }
        mCourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEvent.setmCourse(1);
                } else {
                    mEvent.setmCourse(0);
                }
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' --- 'HH:mm", Locale.US);

        // Declare the component Start date button
        mStartDateButton = (Button)v.findViewById(R.id.start_date_button);
        if(mEvent.getStartTime() != null){
            mStartDateButton.setText(format.format(mEvent.getStartTime().getTime()));
        }else {
            mStartDateButton.setText(setString(am_pm, mStartDateButton, mAllDay));
        }
        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onStartButtonChanged...");
                DatePicker pick = new DatePicker(am_pm, mStartDateButton, mAllDay);
                mStartDateButton.setText(pick.getmText());
                mFromDate = pick.getCalObject();
                mEvent.setStartTime(mFromDate);
                Log.i("onButtonChanged", "" + mEvent.getStartTime().getTime().getYear());
                //Need to fix this
                if (mFromDate.getTime().before(Calendar.getInstance().getTime())) {
                    // mSave = false;
                    //mStartDateButton.setPaintFlags(mStartDateButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    // mSave = true;
                    //mStartDateButton.setPaintFlags(mStartDateButton.getPaintFlags()&(~ Paint.STRIKE_THRU_TEXT_FLAG));
                    //mStartDateButton.setPaintFlags(0);
                }
            }
        });// End StartDate object


        // Declare the component End date object
        mEndDateButton = (Button)v.findViewById(R.id.end_date_button);
        if(mEvent.getEndTime() != null){
            mEndDateButton.setText(format.format(mEvent.getEndTime().getTime()));
        }else {
            mEndDateButton.setText(setString(am_pm, mEndDateButton, mAllDay));
        }

        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onEndButtonChanged...");
                DatePicker pick = new DatePicker(am_pm,mEndDateButton,mAllDay);
                mEndDateButton.setText(pick.getmText());
                mToDate = pick.getCalObject();
                mEvent.setEndTime(mToDate);
                Log.i("onButtonChanged", mToDate.toString());
                // Need to fix this
                if (mToDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                    //mSave = false;
                   // mEndDateButton.setPaintFlags(mEndDateButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    //mSave = true;
                    //mEndDateButton.setPaintFlags(0);
                }
            }
        });// End EndDate object


        m = (CheckBox)v.findViewById(R.id.mon);
        if(mEvent.getmRepeatDays().indexOf('M') >= 0 )
            m.setChecked(true);

        t = (CheckBox)v.findViewById(R.id.tue);
        if(mEvent.getmRepeatDays().indexOf('T') >= 0 )
            t.setChecked(true);

        w = (CheckBox)v.findViewById(R.id.wed);
        if(mEvent.getmRepeatDays().indexOf('W') >= 0 )
            w.setChecked(true);

        r = (CheckBox)v.findViewById(R.id.thu);
        if(mEvent.getmRepeatDays().indexOf('R') >= 0 )
            r.setChecked(true);

        f = (CheckBox)v.findViewById(R.id.fri);
        if(mEvent.getmRepeatDays().indexOf('F') >= 0 )
            f.setChecked(true);

        a = (CheckBox)v.findViewById(R.id.sat);
        if(mEvent.getmRepeatDays().indexOf('A') >= 0 )
            a.setChecked(true);

        s = (CheckBox)v.findViewById(R.id.sun);
        if(mEvent.getmRepeatDays().indexOf('S') >= 0 )
            s.setChecked(true);

        //if(rep().isEmpty()){
            //v.findViewById(R.id.done_date_view).setVisibility(View.GONE);
        //}else {
            //onBoxClick(v);
        //}

        // Declare the component End date object
        mDoneDateButton = (Button)v.findViewById(R.id.done_date_button);
        if(mEvent.getmDoneDate() != null && mEvent.getmDoneDate().getTimeInMillis() > 0){
            v.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
            mDoneDateButton.setText(format.format(mEvent.getmDoneDate().getTime()));
        }else {
            //mEndDateButton.setText(setString(am_pm, mEndDateButton, mAllDay));
            //v.findViewById(R.id.done_date_view).setVisibility(View.GONE);
        }

        mDoneDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onEndButtonChanged...");
                DatePicker pick = new DatePicker(am_pm,mDoneDateButton,mAllDay);
                mDoneDateButton.setText(pick.getmText());
                mDoneDate = pick.getCalObject();
                mEvent.setmDoneDate(mDoneDate);
                Log.i("onButtonChanged", mDoneDate.toString());
                // Need to fix this
                if (mDoneDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                    //mSave = false;
                    // mEndDateButton.setPaintFlags(mEndDateButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    //mSave = true;
                    //mEndDateButton.setPaintFlags(0);
                }
            }
        });// End EndDate object


        mPriority = (RadioGroup)v.findViewById(R.id.radioGroop);


        if(mEvent.getmPriority() == 0) {
            mPriority.check(R.id.radio0);
        }
        else if(mEvent.getmPriority() == 1) {
            mPriority.check(R.id.radio1);
        }
        else if(mEvent.getmPriority() == 2) {
            mPriority.check(R.id.radio2);
        }
        else if(mEvent.getmPriority() == 3) {
            mPriority.check(R.id.radio3);
        }
        else if(mEvent.getmPriority() == 4) {
            mPriority.check(R.id.radio4);
        }
        else if(mEvent.getmPriority() == 5) {
            mPriority.check(R.id.radio5);
        }

        mPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio0){
                    mEvent.setmPriority(0);
                }else if(checkedId == R.id.radio1){
                    mEvent.setmPriority(1);
                }else if(checkedId == R.id.radio2){
                    mEvent.setmPriority(2);
                }else if(checkedId == R.id.radio3){
                    mEvent.setmPriority(3);
                }else if(checkedId == R.id.radio4){
                    mEvent.setmPriority(4);
                }else{
                    mEvent.setmPriority(5);
                }
            }
        });

        /**
        mNotes = (EditText)v.findViewById(R.id.notescroll);
        mNotes.setText(mEvent.getmNote());
        //mNotes.setEnabled(false);
        mNotes.setEnabled(true);
        //mNotes.setTextColor(Color.BLACK);
        mNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEvent.setmNote(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        mDeleteButton = (Button)v.findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDelete = true;

                if((Long)mEvent.getmPrepeatID() != null){
                    char[] dayArray = mEvent.getmRepeatDays().toCharArray();
                    dayArray[mEvent.findDaysIndex(CalEventManager.get(getActivity()).getSingleEvent(mEvent.getmPrepeatID()).getStartTime())] = 'X';
                    mEvent.setmRepeatDays(String.valueOf(dayArray));
                }




                new NetCheck().execute();


                CalEventManager.get(getActivity()).deleteEvent(mEvent);


                if(NavUtils.getParentActivityIntent(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            }
        });


        return v;
    }

    //////////////////////////////////////////
    //
    //  Getter & Setter
    //
    //////////////////////////////////////////
    public Button getmDeleteButton() {
        return mDeleteButton;
    }

    public String rep(){
        String newRepeat = "";
        if(m.isChecked()){
            newRepeat += "M";
        }

        if(t.isChecked()){
            newRepeat += "T";
        }

        if(w.isChecked()){
            newRepeat += "W";
        }

        if(r.isChecked()){
            newRepeat += "R";
        }

        if(f.isChecked()){
            newRepeat += "F";
        }

        if(a.isChecked()){
            newRepeat += "A";
        }

        if(s.isChecked()){
            newRepeat += "S";
        }

        return newRepeat;
    }

    public void onBoxClick(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.mon:
                if (checked)
                    view.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
                //else
                // Remove the meat
                break;
            case R.id.tue:
                if (checked)
                    view.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
                //else
                // I'm lactose intolerant
                break;
            case R.id.wed:
                if (checked)
                    view.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
                //else
            case R.id.thu:
                if (checked)
                    view.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
                //else
            case R.id.fri:
                if (checked)
                    view.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
                //else
            case R.id.sat:
                if (checked)
                    view.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
                //else
            case R.id.sun:
                if (checked)
                    view.findViewById(R.id.done_date_view).setVisibility(View.VISIBLE);
                //else

        }
    }

    /**
     *
     * @param am_pm
     * @param b
     * @param mAllDay
     * @return
     */
    public StringBuilder setString(int am_pm,Button b,Boolean mAllDay){
        StringBuilder text;
        if(mAllDay){
            text = new StringBuilder().append(month).append("/").append(day).append("/").append(year);
        }
        else if(am_pm != -1 && b.getId() == R.id.start_date_button){
            text = new StringBuilder().append(month).append("/").append(day).append("/").append(year).append("  -  ").append(hour).append(":").append(minute).append(" ").append(am_pm == Calendar.AM ? "AM" : "PM");
        }else{
            text = new StringBuilder().append(month).append("/").append(day).append("/").append(year).append("  -  ").append(hour+1).append(":").append(minute).append(" ").append(am_pm == Calendar.AM ? "AM" : "PM");
        }
        return text;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //menu.findItem(android.R.id.home).setIcon(R.mipmap.icon_home);
        if(mSave) {
            //menu.findItem(R.id.delete_event).setVisible(true);
            menu.findItem(R.id.edit_event).setVisible(true);
            menu.findItem(R.id.cancel_event).setVisible(true);
            menu.findItem(R.id.save_event).setVisible(true);
        }
        else {
            menu.findItem(R.id.delete_event).setVisible(false);
            menu.findItem(R.id.edit_event).setVisible(false);
            menu.findItem(R.id.cancel_event).setVisible(false);
            menu.findItem(R.id.save_event).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(mEvent.getStartTime() == null || mEvent.getEndTime() == null)
                    CalEventManager.get(getActivity()).deleteEvent(mEvent);
                if(NavUtils.getParentActivityIntent(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            case R.id.save_event:
                mSave = true;
                mEvent.setmRepeatDays(rep());
                new NetCheck().execute();
                CalEventManager.get(getActivity()).deleteEvent(mEvent);
                if(NavUtils.getParentActivityIntent(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Save the instance
        //CalEventManager.get(getActivity()).saveEvents();
       // if(mSave) {
            //new NetCheck().execute();
        //}
    }

    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK, null);
    }


    ///////////////////////////////
    //
    //
    //
    //////////////////////////////
    private class DatePicker implements DateTimePickerDialog.DateTimeListener{


        private Button b;
        private int am_pm;
        private boolean mAllDay;
        StringBuilder mText;
        Calendar calObject;

        public DatePicker(int am_pm,Button b, boolean mAllDay){
            this.b = b;
            this.am_pm = am_pm;
            this.mAllDay = mAllDay;
            calObject = Calendar.getInstance();
            DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getActivity(), false, this);
            pickerDialog.show();
        }

        @Override
        public void onDateTimeSelected(int year, int month, int day, int hour, int min, int am_pm) {
            if(mAllDay){
                mText = new StringBuilder().append(month).append("/").append(day).append("/").append(year);
            }
            else if(am_pm != -1 && b.getId() == R.id.start_date_button){
                mText = new StringBuilder().append(month).append("/").append(day).append("/").append(year).append("  -  ").append(hour).append(":").append(minute).append(" ").append(am_pm == Calendar.AM ? "AM" : "PM");
            }else{
                mText = new StringBuilder().append(month).append("/").append(day).append("/").append(year).append("  -  ").append(hour).append(":").append(minute).append(" ").append(am_pm == Calendar.AM ? "AM" : "PM");
            }
            calObject.set(Calendar.HOUR_OF_DAY, hour);
            calObject.set(Calendar.MINUTE, min);
            calObject.set(Calendar.MONTH, month-1);
            calObject.set(Calendar.YEAR, year);
            calObject.set(Calendar.DATE, day);
            b.setText(mText);
            Log.i("setString", mText.toString());
            Log.i("setString",calObject.getTime().toString());
            Log.i("setString","Hourse: " + hour);
        }

        ////////////////////////////////////
        //
        //      Getter and Setter
        //
        ////////////////////////////////////
        public Button getB() {
            return b;
        }

        public void setB(Button b) {
            this.b = b;
        }

        public int getAm_pm() {
            return am_pm;
        }

        public void setAm_pm(int am_pm) {
            this.am_pm = am_pm;
        }

        public boolean ismAllDay() {
            return mAllDay;
        }

        public void setmAllDay(boolean mAllDay) {
            this.mAllDay = mAllDay;
        }

        public StringBuilder getmText() {
            return mText;
        }

        public void setmText(StringBuilder mText) {
            this.mText = mText;
        }

        public Calendar getCalObject() {
            return calObject;
        }

        public void setCalObject(Calendar calObject) {
            this.calObject = calObject;
        }
    }


    /**
     * Async Task to check whether internet connection is working
     **/
    private class NetCheck extends AsyncTask<String, String, Boolean> {
        private final String LOG_TAG = NetCheck.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG, "onPreExecute...");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            /**
             * Gets current device state and checks for working internet connection by trying Google.
             **/
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d(LOG_TAG,"onPostExecute...");
            //mError.setText("");

            if (success) {
                //nDialog.dismiss();
                new AsynchronousProcess().execute();
            } else {
                Toast.makeText(getActivity(), "Error in Network Connection", Toast.LENGTH_SHORT).show();
                //mError.setText("Error in Network Connection");
            }
        }

        @Override
        protected void onCancelled() {
            //super.onCancelled();
            //mError.setText("");

        }
    }

    private class AsynchronousProcess extends AsyncTask<String, String, JSONObject> {
        private final String LOG_TAG = AsynchronousProcess.class.getSimpleName();
        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String password, username;
        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG, "onPreExecute...");
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            functionManager = new UserFunctions(getActivity());
            JSONObject json = null;
            try {
                if(mDelete) {
                    json = functionManager.deleteSchedule(""+mEvent.getId());
                    Log.i(LOG_TAG,json.toString());
                }
                else {
                    if(mSaveExists){
                        json = functionManager.updateUserSchedule(mEvent.toJSON());
                    }else {
                        json = functionManager.addUserSchedule(mEvent.toJSON());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            Log.d(LOG_TAG,"OnPostExecute the JSON...");
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    String res = json.getString(KEY_SUCCESS);

                    String red = json.getString(KEY_ERROR);

                    if(Integer.parseInt(res) == 1){
                        //pDialog.setTitle("Getting Data");
                        //pDialog.setMessage("Loading Info");
                        /** need to be done at login too*/
                        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        //JSONObject response;
                        //if(mDelete){
                            //response = json.getJSONObject("event");
                        //}else {
                        if(!mDelete) {
                            if(!mSaveExists) {
                                JSONObject response = json.getJSONObject("eventAdded");
                            }else{
                                JSONObject response = json.getJSONObject("eventAdded");
                            }
                            //}
                            //Log.i(LOG_TAG, response.toString());
                        }


                        //mError.setText("Successfully Registered");
                        //db.addUser(json_user.getString(KEY_USERNAME),json_user.getString(KEY_CREATE_AT));

                        //pDialog.dismiss();

                    }
                    else if (Integer.parseInt(red) ==2){
                        //pDialog.dismiss();
                        Toast.makeText(getActivity(),"Application Cannot make another schedule", Toast.LENGTH_SHORT).show();
                    }
                    else if (Integer.parseInt(red) ==3){
                        //pDialog.dismiss();
                        Toast.makeText(getActivity(),"JSON ERROR", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    //pDialog.dismiss();
                    Toast.makeText(getActivity(),"Error in Network Connection", Toast.LENGTH_SHORT).show();
                    //mError.setText("Error occured in registration");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}



