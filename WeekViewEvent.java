package library;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static library.WeekViewUtil.isSameDay;

/**
 * Source by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://april-shower.com
 * Edited: TM group
 */
public class WeekViewEvent {

    /** Initializes WeekViewEvent values*/
    private long mId;
    private long mRepeatId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private Calendar mDoneDate;
    private String mName;
    private String mNote;
    private String mLocation;
    private String mRepeatDays;
    private int mColor, mPriority, mCourse;
    private boolean mAllDay;


    /** Initializes JSONObject params*/
    private static final String JSON_ID = "id";
    private static final String JSON_S_TIME = "startTime";
    private static final String JSON_E_TIME = "endTime";
    private static final String JSON_TITLE = "title";
    private static final String JSON_NOTE = "note";
    private static final String JSON_LOCATION = "location";
    private static final String JSON_COLOR = "color";
    private static final String JSON_PRIORITY = "priority";
    private static final String JSON_BOOLEAN_ALLDAY = "allDay";
    private static final String JSON_DONE_DATE = "doneDate";
    private static final String JSON_REPEAT = "days";
    private static final String JSON_COURSE = "course";

    /**
     * Convert WeekViewEvent attribute into Json Object
     * @return JSONObject json object of the week view event
     * @throws JSONException
     */
    public JSONObject toJSON()throws JSONException{
        JSONObject json = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.US);

        json.put(JSON_ID,""+ mId);
        json.put(JSON_TITLE, mName);
        if(mStartTime != null)
            json.put(JSON_S_TIME, format.format(mStartTime.getTime()));
        else
            json.put(JSON_S_TIME, "");
        if(mEndTime != null)
            json.put(JSON_E_TIME, format.format(mEndTime.getTime()));
        else
            json.put(JSON_E_TIME, "");
        if(mDoneDate != null)
            json.put(JSON_DONE_DATE,format.format(mDoneDate.getTime()));
        else
            json.put(JSON_DONE_DATE,"");
        json.put(JSON_LOCATION, mLocation);
        json.put(JSON_BOOLEAN_ALLDAY, ""+mAllDay);
        json.put(JSON_NOTE, ""+mNote);
        json.put(JSON_PRIORITY, mPriority);
        json.put(JSON_COURSE,""+mCourse);
        json.put(JSON_COLOR, String.format("#%06X", (0xFFFFFF & mColor)));
        json.put(JSON_REPEAT,""+mRepeatDays);

        return json;

    }

    public WeekViewEvent(WeekViewEvent w){
        mId = generateUniqueId();
        mStartTime = w.getStartTime();
        mEndTime = w.getEndTime();
        mName = w.getName();
        mLocation = w.getLocation();
        mDoneDate = w.getmDoneDate();
        mPriority = w.getmPriority();
        mAllDay = w.isAllDay();
        mColor = w.getColor();
        mCourse = w.getmCourse();
        mRepeatDays = w.getmRepeatDays();
        mRepeatId = w.getmPrepeatID();
    }

    /**
     * Default Empty constructor
     */
    public WeekViewEvent(){
        mId = generateUniqueId();
        mStartTime = null;
        mEndTime = null;
        mName = "";
        mNote = "";
        mLocation = "";
        mDoneDate = null;
        mPriority = 0;
        mAllDay = false;
        mColor = Color.parseColor("#f8b552");
        mCourse = 0;
        mRepeatDays = "";
        mRepeatId = -1;
    }

    public WeekViewEvent(WeekViewEvent w, Calendar st, Calendar et){
        mId = generateUniqueId();
        mStartTime = st;
        mEndTime = et;
        mName = w.getName();
        mLocation = w.getLocation();
        mDoneDate = w.getmDoneDate();
        mPriority = w.getmPriority();
        mAllDay = w.isAllDay();
        mColor = w.getColor();
        mCourse = w.getmCourse();
        mRepeatDays = w.getmRepeatDays();
        mRepeatId = w.getmPrepeatID();
    }



    /**
     * Creating random unique id number on long format
     * @return unique long id number
     */
    public Long generateUniqueId(){
        long val = -1;
        do {
            final UUID uid = UUID.randomUUID();
            final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
            buffer.putLong(uid.getLeastSignificantBits());
            buffer.putLong(uid.getMostSignificantBits());
            final BigInteger bi = new BigInteger(buffer.array());
            val = bi.longValue();
        } while (val < 0);
        return val;
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param startYear Year when the event starts.
     * @param startMonth Month when the event starts.
     * @param startDay Day when the event starts.
     * @param startHour Hour (in 24-hour format) when the event starts.
     * @param startMinute Minute when the event starts.
     * @param endYear Year when the event ends.
     * @param endMonth Month when the event ends.
     * @param endDay Day when the event ends.
     * @param endHour Hour (in 24-hour format) when the event ends.
     * @param endMinute Minute when the event ends.
     */
    public WeekViewEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        this.mId = id;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.set(Calendar.YEAR, startYear);
        this.mStartTime.set(Calendar.MONTH, startMonth-1);
        this.mStartTime.set(Calendar.DAY_OF_MONTH, startDay);
        this.mStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.mStartTime.set(Calendar.MINUTE, startMinute);

        this.mEndTime = Calendar.getInstance();
        this.mEndTime.set(Calendar.YEAR, endYear);
        this.mEndTime.set(Calendar.MONTH, endMonth-1);
        this.mEndTime.set(Calendar.DAY_OF_MONTH, endDay);
        this.mEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.mEndTime.set(Calendar.MINUTE, endMinute);

        this.mName = name;
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param location The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     * @param allDay Is the event an all day event.
     */
    public WeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime, boolean allDay) {
        this.mId = id;
        this.mName = name;
        this.mLocation = location;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mAllDay = allDay;
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param location The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime) {
        this(id, name, location, startTime, endTime, false);
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime) {
        this(id, name, null, startTime, endTime);
    }

    /////////////////////////////////////////////
    //
    //           Getter and Setter
    //
    /////////////////////////////////////////////

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    public void setAllDay(boolean allDay) {
        this.mAllDay = allDay;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }

    public int getmPriority() {
        return mPriority;
    }

    public void setmPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public String getmRepeatDays() {
        return mRepeatDays;
    }

    public void setmRepeatDays(String mRepeatDays) {
        this.mRepeatDays = mRepeatDays;
    }

    public int getmCourse() {
        return mCourse;
    }

    public void setmCourse(int mCourse) {
        this.mCourse = mCourse;
    }

    public Calendar getmDoneDate() {
        return mDoneDate;
    }

    public void setmDoneDate(Calendar mDoneDate) {
        this.mDoneDate = mDoneDate;
    }

    public long getmPrepeatID() {
        return mRepeatId;
    }

    public void setmPrepeatID(long mPrepeatID) {
        this.mRepeatId = mPrepeatID;
    }


    public int findDaysIndex(Calendar sdate){
        int ind = 0;

       // int stInd = mRepeatDays.length();
        int duration = mStartTime.get(Calendar.DAY_OF_MONTH) - sdate.get(Calendar.DAY_OF_MONTH) ;

        for(int i = 1; i < duration; i++){
            Calendar st = Calendar.getInstance();
            st.set(Calendar.DAY_OF_MONTH, sdate.get(Calendar.DAY_OF_MONTH) + i);
            st.set(Calendar.HOUR_OF_DAY, sdate.get(Calendar.HOUR_OF_DAY));
            st.set(Calendar.MONTH, sdate.get(Calendar.MONTH));
            st.set(Calendar.MINUTE, sdate.get(Calendar.MINUTE));
            st.set(Calendar.YEAR, sdate.get(Calendar.YEAR));

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            char c = dayFormat.format(st.getTime()).toUpperCase().charAt(0);

            if(dayFormat.format(st.getTime()).equals("Saturday"))
                c = 'A';

            if(dayFormat.format(st.getTime()).equals("Thursday"))
                c = 'R';

            if(mRepeatDays.contains(""+ c)){
                ind++;
            }

        }

        return ind;
    }


    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekViewEvent that = (WeekViewEvent) o;

        return mId == that.mId;

    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    /**
     * This function splits the WeekViewEvent in WeekViewEvents by day
     * @return event list of WeekViewEvent
     */
    public List<WeekViewEvent> splitWeekViewEvents(){

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        // The first millisecond of the next day is still the same day. (no need to split events for this).
        Calendar endTime = (Calendar) this.getEndTime().clone();
        endTime.add(Calendar.MILLISECOND, -1);
        if (!isSameDay(this.getStartTime(), endTime)) {
            endTime = (Calendar) this.getStartTime().clone();
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            WeekViewEvent event1 = new WeekViewEvent(this.getId(), this.getName(), this.getLocation(), this.getStartTime(), endTime, this.isAllDay());
            event1.setColor(this.getColor());
            events.add(event1);

            // Add other days.
            Calendar otherDay = (Calendar) this.getStartTime().clone();
            otherDay.add(Calendar.DATE, 1);
            while (!isSameDay(otherDay, this.getEndTime())) {
                Calendar overDay = (Calendar) otherDay.clone();
                overDay.set(Calendar.HOUR_OF_DAY, 0);
                overDay.set(Calendar.MINUTE, 0);
                Calendar endOfOverDay = (Calendar) overDay.clone();
                endOfOverDay.set(Calendar.HOUR_OF_DAY, 23);
                endOfOverDay.set(Calendar.MINUTE, 59);
                WeekViewEvent eventMore = new WeekViewEvent(this.getId(), this.getName(), null, overDay, endOfOverDay, this.isAllDay());
                eventMore.setColor(this.getColor());
                events.add(eventMore);

                // Add next day.
                otherDay.add(Calendar.DATE, 1);
            }

            // Add last day.
            Calendar startTime = (Calendar) this.getEndTime().clone();
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            WeekViewEvent event2 = new WeekViewEvent(this.getId(), this.getName(), this.getLocation(), startTime, this.getEndTime(), this.isAllDay());
            event2.setColor(this.getColor());
            events.add(event2);
        }
        else{
            events.add(this);
        }

        return events;
    }
}
