package org.acme.kafka.streams.aggregator.model;

public class InformationReturnAggregation {
    public int year;
    public int submitted;
    public int validated;
    public int accepted;
    public int rejected;

    public InformationReturnAggregation updateFrom(Integer returnYear, String returnEvent) {
        this.year = returnYear;
        switch (returnEvent) {
            case "Submitted":
                submitted++;
                break;
            case "Validated":
                validated++;
                break;
            case "Accepted":
                accepted++;
                break;
            case "Rejected":
                rejected++;
                break;
            default:
                System.out.println("Invalid Event:" + returnEvent);
        }
        return this;
    }
}
