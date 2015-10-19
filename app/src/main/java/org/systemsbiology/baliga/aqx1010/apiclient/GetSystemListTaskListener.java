package org.systemsbiology.baliga.aqx1010.apiclient;

import java.util.List;

/**
 * Created by weiju on 10/17/15.
 */
public interface GetSystemListTaskListener {
    void systemListRetrieved(List<AqxSystem> systems);
}
