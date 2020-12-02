package com.cocode.data;

import java.io.Serializable;

public class Unit implements Serializable {
    public static final int LOG_DATA = 0;
    public static final int FILE_DATA = 1;
    public static final int CHAT_DATA = 2;
    public static final int ENTER_DATA = 3;
    public static final int FILE_TREE_DATA = 4;
    public static final int VOICE_DATA = 5;

    private int type;
    private String userName;
    private String title;
    private Object value;

    public Unit(int type, String userName, String title, Object value) {
        this.type = type;
        this.userName = userName;
        this.title = title;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
