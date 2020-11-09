package org.acme.kafka.streams.aggregator.streams;

import java.util.Optional;

public class GetInformationReturnDataResult {

    private static GetInformationReturnDataResult NOT_FOUND = new GetInformationReturnDataResult(null);

    private final String result;

    private GetInformationReturnDataResult(String result) {
        this.result = result;
    }

    public static GetInformationReturnDataResult found(String data) {
        return new GetInformationReturnDataResult(data);
    }

    public static GetInformationReturnDataResult notFound() {
        return NOT_FOUND;
    }

    public Optional<String> getResult() {
        return Optional.ofNullable(result);
    }
}
