# OpenShift AMQ Streams (Kafka) Demo
Demo to showcase AMQ Streams (Kafka) resiliency and parallelism on OpenShift

## Architecture

TODO

* HTTP/XML based service that takes requests and pushes to Kafka topic
* Resilient consumers (simulate consumer crash and revival)
* Multiple consumers for different use cases (e.g. transactional vs analytical)
* Meta-topic and dashboard for status tracking

## Setup

Clone the repo

```bash
git clone https://github.com/theckang/openshift-kafka-demo.git
```

Install AMQ Streams via OperatorHub.  After installing, verify that it succeeded:

```bash
oc get pods
```

You should see

```
NAME                                                  READY   STATUS    RESTARTS   AGE
amq-streams-cluster-operator-v1.5.3-xxxxxxxxx-xxxxx   1/1     Running   0          4m34s
```

Create the Kafka cluster and topic
> Note: This cluster has plain and external route listeners enabled on the cluster

```bash
oc create -f openshift-kafka-demo/kafka/my-cluster.yaml
oc create -f openshift-kafka-demo/kafka/my-topic.yaml
```

Verify pods

```bash
oc get pods
```

> Output (sample)

```
NAME                                                  READY   STATUS    RESTARTS   AGE
amq-streams-cluster-operator-v1.5.3-xxxxxxxxx-xxxxx   1/1     Running   0          4m34s
my-cluster-entity-operator-xxxxxxxxxx-xxxxx           2/2     Running   0          2m23s
my-cluster-kafka-0                                    2/2     Running   0          3m32s
my-cluster-kafka-1                                    2/2     Running   0          3m32s
my-cluster-kafka-2                                    2/2     Running   0          3m32s
my-cluster-zookeeper-0                                1/1     Running   0          4m18s
my-cluster-zookeeper-1                                1/1     Running   0          4m18s
my-cluster-zookeeper-2                                1/1     Running   0          4m18s
```

Verify topic

```bash
oc get kafkatopic
```

> Output

```bash
NAME       PARTITIONS   REPLICATION FACTOR
my-topic   3            3
```

Run the app on OpenShift

```bash
oc new-app python:3.6~https://github.com/theckang/openshift-kafka-demo --name kafka-demo --context-dir=app \
--env KAFKA_HOST=my-cluster-kafka-bootstrap:9092 --env KAFKA_TOPIC=my-topic
```

Start consumer on OpenShift

```bash
oc run kafka-consumer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-25-rhel7:1.5.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```

Send sample requests

```bash
oc expose svc kafka-demo
DEMO_URL=$(oc get route kafka-demo --template='{{.spec.host}}/data')
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
./kafka_2.13-2.6.0/bin/kafka-console-consumer.sh --bootstrap-server $BOOTSTRAP_SERVER_URL --consumer-property security.protocol=SSL --consumer-property ssl.truststore.password=password --consumer-property ssl.truststore.location=./truststore.jks --topic my-topic
```
