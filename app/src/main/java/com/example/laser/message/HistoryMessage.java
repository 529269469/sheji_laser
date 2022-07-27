package com.example.laser.message;

/**
 * Created by  on 2021/7/10 14:16.
 */
public class HistoryMessage {
    private String Person;
    private String Bureau;

    public HistoryMessage(String person, String bureau){
        Person = person;
        Bureau = bureau;
    }

    public String getPerson() {
        return Person;
    }

    public String getBureau() {
        return Bureau;
    }
}
