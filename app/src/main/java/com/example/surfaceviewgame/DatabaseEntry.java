package com.example.surfaceviewgame;

public class DatabaseEntry {
    private String uuid;
    private String tag;
    private Long score;
    private String date;
    private Long minis;
    private Long total;

    public DatabaseEntry() {}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getMinis() {
        return minis;
    }

    public void setMinis(Long minis) {
        this.minis = minis;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
