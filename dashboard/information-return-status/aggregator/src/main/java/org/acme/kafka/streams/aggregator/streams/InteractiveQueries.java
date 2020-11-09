package org.acme.kafka.streams.aggregator.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;

import org.acme.kafka.streams.aggregator.model.InformationReturnStatusAggregation;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@ApplicationScoped
public class InteractiveQueries {
    @Inject
    KafkaStreams streams;

    public GetInformationReturnDataResult getInformationReturnStatus(String id) {
        InformationReturnStatusAggregation result = getInformationReturnStatusStore().get(id);

        if (result != null) {
            return GetInformationReturnDataResult.found(JsonbBuilder.create().toJson(result));
        } else {
            return GetInformationReturnDataResult.notFound();
        }
    }

    private ReadOnlyKeyValueStore<String, InformationReturnStatusAggregation> getInformationReturnStatusStore() {
        while (true) {
            try {
                return streams.store(StoreQueryParameters
                              .fromNameAndType(InformationReturnTopologyProducer.INFORMATION_RETURN_STATUS_STORE, 
                                               QueryableStoreTypes.keyValueStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}
