package org.acme.kafka.streams.aggregator.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.acme.kafka.streams.aggregator.model.InformationReturnStatusAggregation;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class InformationReturnTopologyProducer {
    static final String INFORMATION_RETURN_AGG_STORE = "information-return-agg-store";
    static final String INFORMATION_RETURN_STATUS_STORE = "information-return-status-store";

    static final String INFORMATION_RETURN_EVENTS_TOPIC = "information-return-events";
    static final String INFORMATION_RETURN_STATUS_TOPIC = "information-return-status";
    
    @Produces
    public Topology buildTopology() {
        JsonbSerde<InformationReturnStatusAggregation> irStatusAggregationSerde = new JsonbSerde<>(InformationReturnStatusAggregation.class);

        KeyValueBytesStoreSupplier aggStoreSuplier = Stores.persistentKeyValueStore(INFORMATION_RETURN_AGG_STORE);
        KeyValueBytesStoreSupplier statusStoreSupplier = Stores.persistentKeyValueStore(INFORMATION_RETURN_STATUS_STORE);

        StreamsBuilder statusBuilder = new StreamsBuilder();

        // Consume events, aggregate by id and publish to status topic
        statusBuilder
            .stream(INFORMATION_RETURN_EVENTS_TOPIC,
                    Consumed.with(Serdes.String(), Serdes.String()))
            .groupBy((returnId, eventString) -> 
                     returnId, 
                     Grouped.with(Serdes.String(),Serdes.String()))
            .aggregate(InformationReturnStatusAggregation::new,
                       (returnId, eventString, aggregation) -> 
                            aggregation.updateFrom(returnId, eventString),
                       Materialized.<String, InformationReturnStatusAggregation> 
                            as(aggStoreSuplier)
                            .withKeySerde(Serdes.String())
                            .withValueSerde(irStatusAggregationSerde))
            .toStream()
            .to(INFORMATION_RETURN_STATUS_TOPIC,
                Produced.with(Serdes.String(), irStatusAggregationSerde));

        // GlobalKTable to store status results for fast queries
        statusBuilder
            .globalTable(INFORMATION_RETURN_STATUS_TOPIC, 
                         Consumed.with(Serdes.String(), irStatusAggregationSerde), 
                         Materialized.<String, InformationReturnStatusAggregation> 
                         as(statusStoreSupplier)
                         .withKeySerde(Serdes.String())
                         .withValueSerde(irStatusAggregationSerde));

        return statusBuilder.build();
    }
}
