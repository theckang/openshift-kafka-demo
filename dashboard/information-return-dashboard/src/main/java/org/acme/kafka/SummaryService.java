package org.acme.kafka;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@ApplicationScoped
public interface SummaryService {
    @GET
    @Path("/summary")
    @Produces("application/json")
    String getSummary();
}
