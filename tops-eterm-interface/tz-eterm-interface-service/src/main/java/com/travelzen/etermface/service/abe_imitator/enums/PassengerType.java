package com.travelzen.etermface.service.abe_imitator.enums;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum PassengerType {

    ADT("adult"), CHD("children"), BAB("baby");


    private String passengerType;

    private PassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getValue() {
        return passengerType;
    }

    public static PassengerType passengerType(String birthDateStr) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);

        int babyYear = year - 2;
        calendar.set(Calendar.YEAR, babyYear);
        Date babyLimit = calendar.getTime();

        int chdYear = year - 12;
        calendar.set(Calendar.YEAR, chdYear);
        Date chdLimit = calendar.getTime();

        SimpleDateFormat sdfStart = new SimpleDateFormat("yyyyMMdd");
        Date birthDate = null;
        try {
            birthDate = (Date) sdfStart.parse(birthDateStr);
        } catch (ParseException e) {
            // log
            e.printStackTrace();
            return PassengerType.ADT;
        }
        calendar.setTime(birthDate);
        if (birthDate.after(babyLimit)) {
            return PassengerType.BAB;
        } else if (birthDate.after(chdLimit)) {
            return PassengerType.CHD;
        }
        return PassengerType.ADT;

    }

    public static int age(String birthDateStr) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);

        SimpleDateFormat sdfStart = new SimpleDateFormat("yyyyMMdd");
        Date birthDate = null;
        try {
            birthDate = (Date) sdfStart.parse(birthDateStr);
        } catch (ParseException e) {
            // log
            e.printStackTrace();
            return 11;
        }

        calendar.setTime(birthDate);
        int birthYear = calendar.get(Calendar.YEAR);
        return year - birthYear;
    }
}
