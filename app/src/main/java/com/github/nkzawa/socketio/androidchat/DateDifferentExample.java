package com.github.nkzawa.socketio.androidchat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ASUS on 7/13/2016.
 */
public class DateDifferentExample {


    void calculateDifference(String dateStart,String dateStop){

        /*String dateStart = "01/14/2012 09:29:58";
        String dateStop = "01/15/2012 10:31:48";*/

        Date d1 = null;
        Date d2 = null;

        try {

            //HH converts hour in 24 hours format (0-23), day calculation
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
