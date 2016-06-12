package com.travelzen.farerule.comparator;

public class ComparatorTest {

    public static void main(String[] args) {
        LogBase.logBack();

        Comparator jv = new Comparator();
        long updateTime = 0;
        String airCompany = "";
        jv.goVersus(updateTime, airCompany);
    }
}
