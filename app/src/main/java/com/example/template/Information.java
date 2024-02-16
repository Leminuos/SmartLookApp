package com.example.template;

public class Information {
    public String ID;
    public String name;
    public String time;
    public String date;

    public Information(String ID, String name, String time, String date) {
        this.ID = ID;
        this.name = name;
        this.time = time;
        this.date = date;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Information{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
