package com.example.laser.ui.entity;

public class ServerReceiveEntity {
    private String mode;
    private String isStarted;
    private String PersonName;
    private String Bullets;
    private String isAtuoPrint;
    private String PrintAddress;

    public ServerReceiveEntity(String mode, String isStarted, String personName, String bullets, String isAtuoPrint, String printAddress) {
        this.mode = mode;
        this.isStarted = isStarted;
        PersonName = personName;
        Bullets = bullets;
        this.isAtuoPrint = isAtuoPrint;
        PrintAddress = printAddress;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getMode() {
        return mode;
    }

    public void setIsStarted(String isStarted) {
        this.isStarted = isStarted;
    }
    public String getIsStarted() {
        return isStarted;
    }

    public void setPersonName(String PersonName) {
        this.PersonName = PersonName;
    }
    public String getPersonName() {
        return PersonName;
    }

    public void setBullets(String Bullets) {
        this.Bullets = Bullets;
    }
    public String getBullets() {
        return Bullets;
    }

    public void setIsAtuoPrint(String isAtuoPrint) {
        this.isAtuoPrint = isAtuoPrint;
    }
    public String getIsAtuoPrint() {
        return isAtuoPrint;
    }

    public void setPrintAddress(String PrintAddress) {
        this.PrintAddress = PrintAddress;
    }
    public String getPrintAddress() {
        return PrintAddress;
    }
}
