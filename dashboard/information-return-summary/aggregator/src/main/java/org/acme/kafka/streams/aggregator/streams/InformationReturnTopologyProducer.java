package org.acme.kafka.streams.aggregator.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.acme.kafka.streams.aggregator.model.InformationReturnAggregation;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class InformationReturnTopologyProducer {
    @ConfigProperty(name = "keydelim")
    String KEY_DELIM;

    static final String INFORMATION_RETURN_AGG_STORE = "information-return-agg-store";
    static final String INFORMATION_RETURN_SUMMARY_STORE = "information-return-summary-store";

    static final String INFORMATION_RETURN_EVENTS_TOPIC = "information-return-events";
    static final String INFORMATION_RETURN_SUMMARY_TOPIC = "information-return-summary";
    
    @Produces
    public Topology buildTopology() {
        JsonbSerde<InformationReturnAggregation> irAggregationSerde = new JsonbSerde<>(InformationReturnAggregation.class);

        KeyValueBytesStoreSupplier aggStoreSupplier = Stores.persistentKeyValueStore(INFORMATION_RETURN_AGG_STORE);
        KeyValueBytesStoreSupplier summaryStoreSupplier = Stores.persistentKeyValueStore(INFORMATION_RETURN_SUMMARY_STORE);

        StreamsBuilder summaryBuilder = new StreamsBuilder();

        // Consume events, rekey based on year, aggregate and publish to summary topic
        summaryBuilder
            .stream(INFORMATION_RETURN_EVENTS_TOPIC,
                    Consumed.with(Serdes.String(), Serdes.String()))
            .groupBy((returnId, eventString) -> 
                     Integer.valueOf(returnId.split(KEY_DELIM)[1]), /* id<delimn>year, status -> year, status */
                     Grouped.with(Serdes.Integer(),Serdes.String()))
            .aggregate(InformationReturnAggregation::new,
                       (year, eventString, aggregation) -> 
                            aggregation.updateFrom(year, eventString),
                       Materialized.<Integer, InformationReturnAggregation> 
                            as(aggStoreSupplier)
                            .withKeySerde(Serdes.Integer())
                            .withValueSerde(irAggregationSerde))
            .toStream()
            .to(INFORMATION_RETURN_SUMMARY_TOPIC,
                Produced.with(Serdes.Integer(), irAggregationSerde));

        // GlobalKTable to store summary results for fast queries
        summaryBuilder
            .globalTable(INFORMATION_RETURN_SUMMARY_TOPIC, 
                         Consumed.with(Serdes.Integer(), irAggregationSerde), 
                         Materialized.<Integer, InformationReturnAggregation> 
                            as(summaryStoreSupplier)
                            .withKeySerde(Serdes.Integer())
                            .withValueSerde(irAggregationSerde));

        return summaryBuilder.build();
    }
}
