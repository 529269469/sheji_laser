package com.example.laser.message;

/**
 * Created by  on 2019/9/11.
 */
public class RockerMessage {

    private String name;
    private String message;

    public RockerMessage(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
