package org.avisandover.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventBrief {
    private Date eventDate;
    private String eventName;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy, HH:mm");
        String dateStr = null;
        try {
            if(eventDate.equals(df.parse("Mon, Jan 01, 1000, 00:00"))) {
                dateStr = "TBD";
            } else {
                dateStr = df.format(eventDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(dateStr + "\n").append(eventName);
        return sb.toString();
    }
}
