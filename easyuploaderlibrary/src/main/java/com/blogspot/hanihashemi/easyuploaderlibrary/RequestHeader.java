package com.blogspot.hanihashemi.easyuploaderlibrary;

/**
 * Created by hani on 4/13/15.
 */
public class RequestHeader {
    private String key;
    private String value;

    public RequestHeader(String key, String value) {
        this.setKey(key);
        this.setValue(value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
