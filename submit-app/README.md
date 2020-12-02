## Setup

Deploy MongoDB on OpenShift

```bash
oc new-app mongodb-persistent -p MONGODB_USER=user -p MONGODB_PASSWORD=password -p MONGODB_DATABASE=testdb
```

Run the app on OpenShift

```bash
oc new-app python:3.6~https://github.com/theckang/openshift-kafka-demo --name submit-app \
--context-dir=submit-app --env KAFKA_HOST=my-cluster-kafka-bootstrap:9092 --env \
KAFKA_INTAKE_TOPIC=information-return-intake --env KAFKA_SUBMITTED_TOPIC=information-return-submitted \
--env KAFKA_EVENTS_TOPIC=information-return-events --env MONGODB_HOST=mongodb --env MONGODB_USER=user \
--env MONGODB_PASSWORD=password
```

Start consumers on OpenShift

```bash
oc run kafka-consumer-events -ti --image=registry.redhat.io/amq7/amq-streams-kafka-25-rhel7:1.5.0 --rm=true \
--restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 \
 --property print.key=true --property key.separator="-" --topic information-return-events --from-beginning
```

```bash
oc run kafka-consumer-submitted -ti --image=registry.redhat.io/amq7/amq-streams-kafka-25-rhel7:1.5.0 --rm=true \
--restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 \
 --property print.key=true --property key.separator="-" --topic information-return-submitted --from-beginning
```

Query MongoDB:

```bash
oc exec -it $(oc get pod -l name=mongodb -o jsonpath='{.items[0].metadata.name}') -- mongo -u user -p password testdb
db.information_returns.find( {} )
```