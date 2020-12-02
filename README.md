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

Create the Kafka cluster and topics
> Note: This cluster has plain and external route listeners enabled on the cluster

```bash
oc create -f openshift-kafka-demo/kafka/my-cluster.yaml
oc create -f openshift-kafka-demo/kafka/information-return-topics.yaml
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
information-return-events      3            3
information-return-intake      3            3
information-return-status      3            3
information-return-submitted   3            3
information-return-summary     3            3
```