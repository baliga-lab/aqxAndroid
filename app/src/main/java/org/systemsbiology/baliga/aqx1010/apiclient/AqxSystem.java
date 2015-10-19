package org.systemsbiology.baliga.aqx1010.apiclient;

public class AqxSystem {
    public String name;
    public String uid;
    public AqxSystem(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }
    public String toString() { return name; }
}
