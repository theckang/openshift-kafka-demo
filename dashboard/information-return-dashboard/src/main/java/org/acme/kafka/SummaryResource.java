package org.acme.kafka;

import java.time.Year;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * A simple resource retrieving the "in-memory" "my-data-stream" and sending the items to a server sent event.
 */
@Path("/summary")
public class SummaryResource {
    @Inject
    @RestClient
    SummaryService summaryService;

    @Inject
    @Channel("summary-data-stream")
    Publisher<String> summary;

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS) // denotes that server side events (SSE) will be produced
    @SseElementType("text/plain") // denotes that the contained data, within this SSE, is just regular text/plain data
    //@SseElementType("application/json")
    public Publisher<String> stream() {
        return summary;
    }

    @GET
    @Path("/latest")
    @Produces("text/plain")
    public String latest() {
        return summaryService.getSummary(Year.now().toString()).toString();
    }
}
