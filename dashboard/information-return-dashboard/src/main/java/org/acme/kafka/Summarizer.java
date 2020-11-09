package org.acme.kafka;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Broadcast;

/**
 * A simple component for consuming from a kafka stream and sending to the "in-memory" "my-data-stream".
 */
@ApplicationScoped
public class Summarizer {
    @Incoming("summary")
    @Outgoing("summary-data-stream")
    @Broadcast
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public String process(String summary) {
        return summary;
    }

}
