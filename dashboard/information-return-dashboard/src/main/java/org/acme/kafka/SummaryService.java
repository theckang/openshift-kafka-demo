package org.acme.kafka;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@RegisterRestClient
public interface SummaryService {
    @GET
    @Path("/summary/{year}")
    @Produces("application/json")
    String getSummary(@PathParam("year") String year);
}
