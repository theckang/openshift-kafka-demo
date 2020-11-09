package org.acme.kafka.streams.aggregator.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acme.kafka.streams.aggregator.streams.GetInformationReturnDataResult;
import org.acme.kafka.streams.aggregator.streams.InteractiveQueries;

@ApplicationScoped
@Path("/information-return")
public class InformationReturnEndpoint {

    @Inject
    InteractiveQueries interactiveQueries;

    @GET
    @Path("/summary")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInformationReturnData() {
        GetInformationReturnDataResult result = interactiveQueries.getInformationReturnSummary();

        if (result.getResult().isPresent()) {
            return Response.ok(result.getResult().get()).build();
        } else {
            return Response.status(Status.NOT_FOUND.getStatusCode(), 
                                   "No data found for information returns").build();
        }
    }

    @GET
    @Path("/summary/{year}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInformationReturnData(@PathParam("year") int year) {
        GetInformationReturnDataResult result = interactiveQueries.getInformationReturnSummary(year);

        if (result.getResult().isPresent()) {
            return Response.ok(result.getResult().get()).build();
        } else {
            return Response.status(Status.NOT_FOUND.getStatusCode(), 
                                   "No data found for information return year " + year).build();
        }
    }
}
