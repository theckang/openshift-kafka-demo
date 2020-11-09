package org.acme.kafka.streams.aggregator.model;

import java.util.HashSet;
import java.util.Set;

public class InformationReturnStatusAggregation {
    public String returnId;
    public Set<String> returnStatus;

    public InformationReturnStatusAggregation updateFrom(String returnId, String returnEvent) {
        this.returnId = returnId;
        if(returnStatus == null) {
            returnStatus = new HashSet<String>();
        }
        returnStatus.add(returnEvent);

        return this;
    }
}
