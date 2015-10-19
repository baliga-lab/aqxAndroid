package org.systemsbiology.baliga.aqx1010.apiclient;

import java.util.Date;

public class AqxSystemDetails {
    public Date creationDate, startDate;
    public String name, aqxTechnique;
    public NameAndCount[] aquaticOrganisms, crops;

    public AqxSystemDetails(String name, Date creationDate, Date startDate,
                            String aqxTechnique,
                            NameAndCount[] aquaticOrganisms, NameAndCount[] crops) {
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.name = name;
        this.aqxTechnique = aqxTechnique;
        this.aquaticOrganisms = aquaticOrganisms;
        this.crops = crops;
    }
}
