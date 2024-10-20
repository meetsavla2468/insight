package com.example.education;

public class Data {
    String timeStamp,url,title;

    public Data(){
    }

    public Data(String timeStamp, String url, String title) {
        this.timeStamp = timeStamp;
        this.url = url;
        this.title = title;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
