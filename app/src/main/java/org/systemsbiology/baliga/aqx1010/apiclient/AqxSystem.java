package org.systemsbiology.baliga.aqx1010.apiclient;

import android.graphics.Bitmap;

public class AqxSystem {
    public String name;
    public String uid;
    public Bitmap thumbnail;
    public AqxSystem(String name, String uid, Bitmap thumbnail) {
        this.name = name;
        this.uid = uid;
        this.thumbnail = thumbnail;
    }
    public String toString() { return name; }
}
