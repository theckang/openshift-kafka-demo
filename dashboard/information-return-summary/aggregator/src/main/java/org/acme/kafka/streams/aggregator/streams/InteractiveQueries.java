package org.acme.kafka.streams.aggregator.streams;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;

import org.acme.kafka.streams.aggregator.model.InformationReturnAggregation;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@ApplicationScoped
public class InteractiveQueries {

    @Inject
    KafkaStreams streams;

    public GetInformationReturnDataResult getInformationReturnSummary() {
        List<InformationReturnAggregation> irAggList 
            = new ArrayList<InformationReturnAggregation>();

        KeyValueIterator<Integer, InformationReturnAggregation> results 
            = getInformationReturnSummaryStore().all();
        results.forEachRemaining((result)->irAggList.add(result.value));
        results.close();

        return GetInformationReturnDataResult.found(JsonbBuilder.create().toJson(irAggList));
    }

    public GetInformationReturnDataResult getInformationReturnSummary(int year) {
        InformationReturnAggregation result = getInformationReturnSummaryStore().get(year);

        if (result != null) {
            return GetInformationReturnDataResult.found(JsonbBuilder.create().toJson(result));
        } else {
            return GetInformationReturnDataResult.notFound();
        }
    }

    private ReadOnlyKeyValueStore<Integer, InformationReturnAggregation> getInformationReturnSummaryStore() {
        while (true) {
            try {
                return streams.store(StoreQueryParameters
                              .fromNameAndType(InformationReturnTopologyProducer.INFORMATION_RETURN_SUMMARY_STORE, 
                                               QueryableStoreTypes.keyValueStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}
