package org.systemsbiology.baliga.aqx1010.apiclient;

import java.util.Date;

public class AqxSystemDetails {
    public Date creationDate;
    public String name;

    public AqxSystemDetails(String name, Date creationDate) {
        this.creationDate = creationDate;
        this.name = name;
    }
}
