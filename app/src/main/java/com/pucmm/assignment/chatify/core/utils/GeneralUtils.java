package com.pucmm.assignment.chatify.core.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.Timestamp;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class GeneralUtils {
    // Returns a formatted date string in the format "dd/MM/yyyy".
    public static String getFormattedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    // Returns a formatted time string in the format "HH:mm".
    public static String getTimeIn24HourFormat(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(date);
    }

    // Returns a formatted time string in the format "dd/MM/yyyy HH:mm".
    public static String getFullFormattedDate(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return timeFormat.format(date);
    }

    // Checks if a given timestamp is older than a day.
    public static boolean isOlderThanADay(Timestamp timestamp) {
        Date today = new Date(), date = timestamp.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, 1);

        return today.after(cal.getTime());
    }

    // Returns the access token for the Firebase Messaging API
    public static String getAccessToken() throws IOException {
        final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
        final String[] SCOPES = { MESSAGING_SCOPE };

        InputStream serviceAccountStream = GeneralUtils.class
                .getClassLoader().getResourceAsStream("service-account.json");
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccountStream)
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }
}
