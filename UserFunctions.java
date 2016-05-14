package library;

import android.content.Context;
import android.util.Log;

import com.example.springroll.database.CalEventManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import library.WeekViewEvent;

/**
 * Created by SpringRoll on 1/5/2016.
 */
public class UserFunctions {
    private final static String LOG_TAG = UserFunctions.class.getSimpleName();
    private JSONParser jsonParser;
    private static UserFunctions singleton = null;
    private Context context;
    private DatabaseHandler db;
    //URL of the PHP API
    private static String loginURL = "http://152.117.180.231/webapp/";
    private static String registerURL = "http://152.117.180.231/webapp/";
    private static String scheduleURL = "http://152.117.180.231/webapp/";

    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String updateEvents_tag = "update_event";
    private static String addEvents_tag = "add_event";
    private static String getEvents_tag = "get_event";
    private static String deleteEvents_tag = "delete_event";

    /**
     * Constructor.
     * @param context context.
     */
    public UserFunctions(final Context context){
        assert (singleton ==null);
        singleton = this;
        this.context = context;

        db = new DatabaseHandler(context);
        jsonParser = new JSONParser();
    }

    /**
     * Gets the singleton instance of this class.
     * @return instance
     */
    public synchronized static UserFunctions getUserFunctionManager() {
        return singleton;
    }


    /**
     * Function to Login
     * @param username username
     * @param password password
     * @return return the json object with username and password
     */
    public JSONObject loginUser(String username, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    /**
     * Function to  Register
     * @param username username
     * @param password password
     * @return return the json object with username and password
     */
    public JSONObject registerUser(String username, String password){
        // Building Parameters
        List <NameValuePair> params= new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    /**
    private String mFilename;
    public void saveEvents(ArrayList<WeekViewEvent> mEvents)throws JSONException, IOException {
        // Build an array in JSON
        JSONArray array = new JSONArray();
        for (WeekViewEvent c : mEvents)
            array.put(c.toJSON());
        // Write the file to disk
        Writer writer = null;
        try {
            OutputStream out = context
                    .openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }*/

    public JSONObject addUserSchedule(JSONObject event){
        //Get Username
        String username = db.getUserDetails().get("username");

        // Building Parameters
        List <NameValuePair> params= new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", addEvents_tag));
        params.add(new BasicNameValuePair("username", "admin"));
        params.add(new BasicNameValuePair("event", event.toString()));
        Log.i("addUserSchedule", event.toString());
        JSONObject json = jsonParser.getJSONFromUrl(scheduleURL, params);
        return json;
    }

    public JSONObject updateUserSchedule(JSONObject event){
        //Get Username
        String username = db.getUserDetails().get("username");

        // Building Parameters
        List <NameValuePair> params= new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", updateEvents_tag));
        params.add(new BasicNameValuePair("username", "admin"));
        params.add(new BasicNameValuePair("event", event.toString()));
        Log.i("addUserSchedule", event.toString());
        JSONObject json = jsonParser.getJSONFromUrl(scheduleURL, params);
        return json;
    }

    public JSONObject getUserShcedule(){
        //Get Username
        String username = db.getUserDetails().get("username");
        // Building Parameters
        List <NameValuePair> params= new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", getEvents_tag));
        params.add(new BasicNameValuePair("username", "admin"));
        //params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(scheduleURL, params);
        return json;
    }

    public JSONObject deleteSchedule(String eventID){
        //Get Username
        String username = db.getUserDetails().get("username");
        // Building Parameters
        List <NameValuePair> params= new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", deleteEvents_tag));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("event", ""+eventID));
        JSONObject json = jsonParser.getJSONFromUrl(scheduleURL, params);
        return json;
    }

    public List<WeekViewEvent> repeatEvent(WeekViewEvent e){
        Log.d(LOG_TAG,"repeatEvent..");
        List<WeekViewEvent> repeat = new ArrayList<WeekViewEvent>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
        ArrayList<Integer> daysToAdd = new ArrayList<Integer>();
        int toAdd = 0;


        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm",Locale.US);
        //String currentday = dayFormat.format(e.getStartTime().getTime());
        WeekViewEvent ev;

        int diff;
        //WeekViewEvent event = new WeekViewEvent();
        if(e.getmDoneDate() == null){
            diff = 30;
            //Log.i(LOG_TAG,"inside if..."+diff);

        }else {
            diff = e.getmDoneDate().get(Calendar.DAY_OF_MONTH) - e.getStartTime().get(Calendar.DAY_OF_MONTH);
            //Log.i(LOG_TAG, "inside else..." + diff + " " + e.getmDoneDate().getTime().toString());
        }

        Date start = e.getStartTime().getTime();
        Date s = e.getStartTime().getTime();


        Calendar startTime = Calendar.getInstance();
        //Calendar startTime =  e.getStartTime();
        startTime.set(Calendar.DAY_OF_MONTH,start.getDate());
        startTime.set(Calendar.HOUR_OF_DAY, start.getHours());
        startTime.set(Calendar.MINUTE, start.getMinutes());
        startTime.set(Calendar.MONTH, start.getMonth());
        startTime.set(Calendar.YEAR, start.getYear() + 1900);


        int m = e.getStartTime().get(Calendar.DAY_OF_MONTH);
        //Log.i(LOG_TAG, "begin of loop...S--" + diff + " " + start.toString());


        Date end = e.getEndTime().getTime();
        Date q = e.getEndTime().getTime();


        Calendar endTime = Calendar.getInstance();
        //Calendar endTime = e.getEndTime();
        endTime.set(Calendar.DAY_OF_MONTH,end.getDate());
        endTime.set(Calendar.HOUR_OF_DAY, end.getHours());
        endTime.set(Calendar.MINUTE, end.getMinutes());
        endTime.set(Calendar.MONTH, end.getMonth());
        endTime.set(Calendar.YEAR, end.getYear() + 1900);

        int n = e.getEndTime().get(Calendar.DAY_OF_MONTH);


        //Log.i(LOG_TAG, "begin of loop...E--" + diff + " " + end.toString());

        for (int i = 1; i < diff; i++) {
           // m = e.getStartTime().get(Calendar.DAY_OF_MONTH);

          //  n = e.getEndTime().get(Calendar.DAY_OF_MONTH);

            startTime.add(Calendar.DAY_OF_MONTH, 1);
            endTime.add(Calendar.DAY_OF_MONTH, 1);



            String letter = dayFormat.format(startTime.getTime());
            Log.i(LOG_TAG, "inside loop..." + i +"--" + letter);

            String matchedLetter  = "G";
            switch (letter){
                case "Mon":{
                    matchedLetter = "M";
                    break;
                }
                case "Tue":{
                    matchedLetter = "T";
                    break;
                }
                case "Wed":{
                    matchedLetter = "W";
                    break;
                }
                case "Thu":{
                    matchedLetter = "R";
                    break;
                }
                case "Fri":{
                    matchedLetter = "F";
                    break;
                }
                case "Sat":{
                    matchedLetter = "A";
                    break;
                }
                case "Sun":{
                    matchedLetter = "S";
                    break;
                }
            }

            System.out.println(matchedLetter + "    " + e.getmRepeatDays().contains(matchedLetter));
            if (e.getmRepeatDays().contains(matchedLetter)  ){
                Log.i(LOG_TAG, "inside loop...S--" + diff + " " + startTime.getTime().toString());

                Log.i(LOG_TAG, "inside loop...E--" + diff + " " + endTime.getTime().toString());

                ev = new WeekViewEvent(e);
                ev.setmPrepeatID(e.getId());
                ev.setmRepeatDays("");
                daysToAdd.add(i);
                System.out.println(i);
                toAdd = 0;

                if(ev == null)
                    System.out.println("ev is null for some reason");

                repeat.add(ev);
                //Log.i(LOG_TAG, "inside loop...Event_Mother--"+e.getStartTime().getTime() + " +++ "+ e.getEndTime().getTime());
            }

            toAdd++;
        }

        for(int h = 0; h < repeat.size(); h++){
            Calendar st = Calendar.getInstance();
            st.set(Calendar.DAY_OF_MONTH, e.getStartTime().get(Calendar.DAY_OF_MONTH) + daysToAdd.get(h));
            st.set(Calendar.HOUR_OF_DAY, e.getStartTime().get(Calendar.HOUR_OF_DAY));
            st.set(Calendar.MONTH, e.getStartTime().get(Calendar.MONTH));
            st.set(Calendar.MINUTE, e.getStartTime().get(Calendar.MINUTE));
            st.set(Calendar.YEAR, e.getStartTime().get(Calendar.YEAR));


            Calendar et = Calendar.getInstance();
            et.set(Calendar.DAY_OF_MONTH, e.getStartTime().get(Calendar.DAY_OF_MONTH) + daysToAdd.get(h));
            et.set(Calendar.HOUR_OF_DAY, e.getEndTime().get(Calendar.HOUR_OF_DAY));
            et.set(Calendar.MONTH, e.getEndTime().get(Calendar.MONTH));
            et.set(Calendar.MINUTE, e.getEndTime().get(Calendar.MINUTE));
            et.set(Calendar.YEAR, e.getEndTime().get(Calendar.YEAR));

            WeekViewEvent parent = CalEventManager.get(context).getSingleEvent(repeat.get(h).getmPrepeatID());
            WeekViewEvent child = new WeekViewEvent(e, st, et);
            child.setmPrepeatID(e.getId());

            if(parent.getmRepeatDays().charAt(child.findDaysIndex(parent.getStartTime())) == 'X')
                child.setName("REMOVEME!!!");

            repeat.set(h, child);
            //System.out.println("index of days : " + repeat.get(h).getName()+ "  " + repeat.get(h).findDaysIndex(parent.getStartTime()));

            System.out.println(repeat.get(h).getStartTime().getTime().toString());
        }


        for(int j = 0; j < repeat.size(); j++){
            if(repeat.get(j).getName().equals("REMOVEME!!!"))
                repeat.remove(j);
        }

        return repeat;
    }

    private int getDay(Calendar object){
        return -1;
    }

    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * @param
     * @return
     */
    public boolean logoutUser(){
        Log.d(LOG_TAG, "logoutUser");
        db.resetTables();
        return true;
    }
}
