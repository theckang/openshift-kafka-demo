## Setup

Run the app on OpenShift

```bash
oc new-app python:3.6~https://github.com/theckang/openshift-kafka-demo --name intake-app --context-dir=intake-app \
--env KAFKA_HOST=my-cluster-kafka-bootstrap:9092 --env KAFKA_INTAKE_TOPIC=information-return-intake
```

Start consumer on OpenShift

```bash
oc run kafka-consumer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-25-rhel7:1.5.0 --rm=true \
--restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 \
 --property print.key=true --property key.separator="-" --topic information-return-intake --from-beginning
```

Send sample requests

```bash
oc expose svc intake-app
DEMO_URL=$(oc get route intake-app --template='{{.spec.host}}/data')
curl -H "Content-type: application/xml" -X POST $DEMO_URL -d '<xml><message>This is a test</message></xml>'
```


### Alternative consumer (fully remote)

Download the latest releaseo of [Kafka](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.6.0/kafka_2.13-2.6.0.tgz)

```bash
tar -xzf kafka_2.13-2.6.0.tgz
```

Extract certificates and place them in your chain of trust
> Note: The OpenShift external route to the bootstrap server requires TLS.

```bash
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- > ca.crt
keytool -import -trustcacerts -alias root -file ca.crt -keystore truststore.jks -storepass password -noprompt
```

Start local consumer

```bash
BOOTSTRAP_SERVER_URL=$(oc get routes my-cluster-kafka-bootstrap -o=jsonpath='{.status.ingress[0].host}{"\n"}'):443
./kafka_2.13-2.6.0/bin/kafka-console-consumer.sh --bootstrap-server $BOOTSTRAP_SERVER_URL \
--consumer-property security.protocol=SSL --consumer-property ssl.truststore.password=password \
--consumer-property ssl.truststore.location=./truststore.jks --topic information-return-intake
```
