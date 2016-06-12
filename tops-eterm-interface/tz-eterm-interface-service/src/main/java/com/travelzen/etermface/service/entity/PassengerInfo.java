package com.travelzen.etermface.service.entity;

public class PassengerInfo {

    private int idIndex;
    private int followAdtIndex;
    private String surName;
    private String surNameWithAppellation;
    private String cerNo;
    private String nationality = "CN";
    private String birthDay;
    // M
    private String gender;
    private String cerType = "P";
    private String cerCountry = "CN";
    // 2018-04-19
    private String cerValidity;
    // ADT
    private String passengerType;
    // 联系电话
    private String telephone;

    public PassengerInfo() {
    }

    public PassengerInfo(String surName, String cerNo, String cerType, String cerCountry, String cerValidity, String nationality, String birthDay, String gender, String passengerType, String telephone) {
        this.surName = surName;
        this.cerNo = cerNo;
        this.nationality = nationality;
        this.birthDay = birthDay;
        this.gender = gender;
        this.cerType = cerType;
        this.cerCountry = cerCountry;
        this.cerValidity = cerValidity;
        this.passengerType = passengerType;
        this.telephone = telephone;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public int getFollowAdtIndex() {
        return followAdtIndex;
    }

    public void setFollowAdtIndex(int followAdtIndex) {
        this.followAdtIndex = followAdtIndex;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getCerNo() {
        return cerNo;
    }

    public void setCerNo(String cerNo) {
        this.cerNo = cerNo;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCerType() {
        return cerType;
    }

    public void setCerType(String cerType) {
        this.cerType = cerType;
    }

    public String getCerCountry() {
        return cerCountry;
    }

    public void setCerCountry(String cerCountry) {
        this.cerCountry = cerCountry;
    }

    public String getCerValidity() {
        return cerValidity;
    }

    public void setCerValidity(String cerValidity) {
        this.cerValidity = cerValidity;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSurNameWithAppellation() {
        return surNameWithAppellation;
    }

    public void setSurNameWithAppellation(String surNameWithAppellation) {
        this.surNameWithAppellation = surNameWithAppellation;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    }

}
