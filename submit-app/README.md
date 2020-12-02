## Setup

Run the app on OpenShift

```bash
oc new-app python:3.6~https://github.com/theckang/openshift-kafka-demo --name submit-app \
--context-dir=submit-app --env KAFKA_HOST=my-cluster-kafka-bootstrap:9092 --env \
KAFKA_INTAKE_TOPIC=information-return-intake --env KAFKA_SUBMITTED_TOPIC=information-return-submitted \
--env KAFKA_EVENTS_TOPIC=information-return-events
```
