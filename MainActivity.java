package com.example.springroll.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.springroll.database.signInUtil.SignInActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import library.UserFunctions;
import library.DateTimeInterpreter;
import library.MonthLoader;
import library.WeekView;
import library.WeekViewEvent;

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Website: http://alamkanak.github.io
 */
public class MainActivity extends AppCompatActivity implements WeekView.EventClickListener, WeekView.EmptyViewClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    /** Class name for log messages. */
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    /** Key for diplay the canlendar view option. */
    private static final int REQUEST_EVENT = 1;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;

    /**
     * JSON Response node names.
     **/
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";

    /** The custom UI Calendar display for setting up the column and date. */
    private WeekView mWeekView;

    /** The UserFunction class to store and get calendar information from WAMP SERVER. */
    private UserFunctions functionManager;

    /** Bundle key for saving/restoring the toolbar title. */
    private final static String BUNDLE_KEY_TOOLBAR_TITLE = "title";

    private ArrayList<WeekViewEvent> mNewEvent; //Test Debug arrayList

    private List<WeekViewEvent> EventFromCAL;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreate...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calander_base);

        //functionManager = new UserFunctions(getApplicationContext());

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // Get the event
        mWeekView.getEventClickListener();

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Initially, there will be no events on the week view because the user has not tapped on
        // it yet.

        EventFromCAL = CalEventManager.get(getApplicationContext()).getEventList();

        functionManager = new UserFunctions(getApplicationContext());

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
        new NetCheck().execute();


    }

    /**
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        //new NetCheck().execute();
        return super.onCreateView(name, context, attrs);
    }*/

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        Log.d(LOG_TAG, "onMonthChange...");
        Log.i(LOG_TAG, "" + EventFromCAL.size());

        //mAuthTask.execute();
        List<WeekViewEvent> repeat = new ArrayList<WeekViewEvent>();


        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : EventFromCAL) {
            if (eventMatches(event, newYear,newMonth)) {
                matchedEvents.add(event);

                if(event.getmRepeatDays().length() > 0){

                    repeat = functionManager.repeatEvent(event);
                    matchedEvents.addAll(repeat);


                }
            }
        }

        EventFromCAL.addAll(matchedEvents);
        return matchedEvents;

    }

    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    /**
     * Get events that were added by tapping on empty view.
     * @param year The year currently visible on the week view.
     * @param month The month currently visible on the week view.
     * @return The events of the given year and month.
     */
    private ArrayList<WeekViewEvent> getNewEvents(int year, int month) {
        Log.d(LOG_TAG," getNewEvents...");
        // Get the starting point and ending point of the given month. We need this to find the
        // events of the given month.
        Calendar startOfMonth = Calendar.getInstance();
        startOfMonth.set(Calendar.YEAR, year);
        startOfMonth.set(Calendar.MONTH, month - 1);
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);
        Calendar endOfMonth = (Calendar) startOfMonth.clone();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getMaximum(Calendar.DAY_OF_MONTH));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);

        // Find the events that were added by tapping on empty view and that occurs in the given
        // time frame.
        ArrayList<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : mNewEvent) {
            if (event.getEndTime().getTimeInMillis() > startOfMonth.getTimeInMillis() &&
                    event.getStartTime().getTimeInMillis() < endOfMonth.getTimeInMillis()) {
                events.add(event);
            }
        }
        return events;
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_EVENT){
            //
        }
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        // Save the title so it will be restored properly to match the view loaded when rotation
        // was changed or in case the activity was destroyed.
    }

    @Override
    public void onResume(){
        Log.d(LOG_TAG,"onResume...");
        super.onResume();
        mWeekView.notifyDatasetChanged();
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            //Meneu option Today
            case R.id.action_today:
                mWeekView.goToToday();
                return true;

            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;

            //Menu option 3 days view
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;

            //Menu option week view
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;

            //Menu option logout
            case R.id.action_logout:
                item.setChecked(!item.isChecked());
                functionManager.logoutUser();
                Intent upanel = new Intent(getApplicationContext(), SignInActivity.class);
                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel);
                /**
                 * Close Main Screen
                 **/
                finish();
                return true;

            //Menu option refresh
            case R.id.action_settings:
                mWeekView.notifyDatasetChanged();
                return true;

            //Menu option compute the study time
            case R.id.action_compute:
                mWeekView.notifyDatasetChanged();
                Intent j = new Intent(this,ComputeActivity.class);
                startActivity(j);
                return true;

            //Menu option add event
            case R.id.action_add_event:
                WeekViewEvent e = new WeekViewEvent();
                Log.i(LOG_TAG,"adding event with id: "+e.getId());
                CalEventManager.get(this).addEvent(e);
                Intent i = new Intent(this,EventActivity.class);
                i.putExtra(EventFragment.EXTRA_EVENT_ID,e.getId());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i,0);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    /**
     * Getting the Calendar Information
     * @param time
     * @return string details of calendar
     */
    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d/%d", time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1,
                time.get(Calendar.DAY_OF_MONTH),time.get(Calendar.YEAR));
    }

    /**
     * Display the event detail on event click
     * @param event: event clicked.
     * @param eventRect: view containing the clicked event.
     */
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this,EventActivity.class);
        i.putExtra(EventFragment.EXTRA_EVENT_ID, event.getId());
        Log.i("onEventClick", "" + EventFragment.EXTRA_EVENT_ID + ", " + event.getId());

        startActivityForResult(i,0);
    }

    /**
     * Display the long press on even click
     * @param event: event clicked.
     * @param eventRect: view containing the clicked event.
     */
    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param time: {@link Calendar} object set with the date and time of the long pressed position on the view.
     */
    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    /**
     * This is currently not working
     * @param time: {@link Calendar} object set with the date and time of the clicked position on the view.
     */
    @Override
    public void onEmptyViewClicked(Calendar time) {
        Toast.makeText(this, "Empty view pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    /**
     * Async Task to check whether internet connection is working
     **/

    private class NetCheck extends AsyncTask<String, String, Boolean> {
        private final String LOG_TAG = NetCheck.class.getSimpleName();
        private ProgressDialog nDialog;


        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG,"onPreExecute...");
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            /*8
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
            */
        }

        @Override
        protected Boolean doInBackground(String... args) {

            /**
             * Gets current device state and checks for working internet connection by trying Google.
             **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

                    e1.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d(LOG_TAG,"onPostExecute...");
            if (success) {
                nDialog.dismiss();
                new AsynchronousProcess().execute();
            } else {
                nDialog.dismiss();
                Toast.makeText(getApplication(),"Error in Network Connection", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            //Empty Construct
        }
    }

    private class AsynchronousProcess extends AsyncTask<String, String, JSONObject> {
        private final String LOG_TAG = AsynchronousProcess.class.getSimpleName();
        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG, "onPreExecute...");
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            //pDialog.setTitle("Contacting Servers");
            //pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            functionManager = new UserFunctions(getApplicationContext());
            JSONObject json = functionManager.getUserShcedule();

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
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");
                        CalEventManager.get(getApplicationContext()).getEventList().clear();
                        JSONArray list = json.getJSONArray("event");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject eventJSON = list.getJSONObject(i);

                            WeekViewEvent e = new WeekViewEvent();
                            e.setId(eventJSON.getLong("event_num"));
                            e.setName(eventJSON.getString("event_title"));
                            e.setLocation(eventJSON.getString("location"));
                            e.setmRepeatDays(eventJSON.getString("days"));
                            e.setmCourse(eventJSON.getInt("course"));
                            e.setmPriority(eventJSON.getInt("priority"));
                            if(eventJSON.getInt("all_day") == 1 ){
                                e.setAllDay(true);
                            }

                            //Parse Time.
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm",Locale.US);
                            Date start = new Date();
                            Date end = new Date();
                            Date done = new Date();
                            try {
                                start = format.parse(eventJSON.getString("s_date"));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                end = format.parse(eventJSON.getString("e_date"));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                if(!eventJSON.getString("done_date").equalsIgnoreCase("null")) {
                                    done = format.parse(eventJSON.getString("done_date"));
                                    Calendar doneTime = Calendar.getInstance();
                                    doneTime.set(Calendar.DAY_OF_MONTH, done.getDate());
                                    doneTime.set(Calendar.HOUR_OF_DAY, done.getHours());
                                    doneTime.set(Calendar.MINUTE, done.getMinutes());
                                    doneTime.set(Calendar.MONTH, done.getMonth());
                                    doneTime.set(Calendar.YEAR, done.getYear() + 1900);
                                    e.setmDoneDate(doneTime);
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                            // Initialize start and end time.
                            Calendar startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH,start.getDate());
                            startTime.set(Calendar.HOUR_OF_DAY, start.getHours());
                            startTime.set(Calendar.MINUTE, start.getMinutes());
                            startTime.set(Calendar.MONTH, start.getMonth());
                            startTime.set(Calendar.YEAR, start.getYear()+1900);
                           //
                            Calendar endTime = Calendar.getInstance();
                            endTime.set(Calendar.DAY_OF_MONTH,end.getDate());
                            endTime.set(Calendar.HOUR_OF_DAY, end.getHours());
                            endTime.set(Calendar.MINUTE, end.getMinutes());
                            endTime.set(Calendar.MONTH, end.getMonth());
                            endTime.set(Calendar.YEAR, end.getYear() + 1900);


                            e.setStartTime(startTime);
                            e.setEndTime(endTime);
                            e.setColor(Color.parseColor(eventJSON.getString("color")));

                            CalEventManager.get(getApplicationContext()).addEvent(e);

                           if(!eventJSON.getString("days").isEmpty()) {
                            //    WeekViewEvent reEvent = e;
                           //     List<WeekViewEvent> subEvent = functionManager.repeatEvent(reEvent);
                            //    for(int g = 0; g < subEvent.size(); g++) {
                            //        CalEventManager.get(getApplicationContext()).getEventList().add(subEvent.get(g));
                            //    }

                            }

                        }
                        mWeekView.notifyDatasetChanged();
                        //mError.setText("Successfully Registered
                        //db.addUser(json_user.getString(KEY_USERNAME),json_user.getString(KEY_CREATE_AT));

                        pDialog.dismiss();

                    }
                    else if (Integer.parseInt(red) ==2){
                        pDialog.dismiss();
                        Toast.makeText(getApplication(),"Application Cannot make another schedule", Toast.LENGTH_SHORT).show();
                        //mError.setText("User already exists");
                    }
                    else if (Integer.parseInt(red) ==3){
                        pDialog.dismiss();
                        Toast.makeText(getApplication(),"JSON ERROR", Toast.LENGTH_SHORT).show();
                        //mError.setText("Invalid username id");
                    }
                }
                else{
                    pDialog.dismiss();
                    Toast.makeText(getApplication(),"Error in Network Connection", Toast.LENGTH_SHORT).show();
                    //mError.setText("Error occured in registration");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
