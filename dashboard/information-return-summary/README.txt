# pre-reqs
kafka-streams-quickstart: see kafka-streams-quickstart/README.txt
export KAFKA_BOOTSTRAP_SERVER=192.168.1.214:9092

# start the application
mvn clean install
java -Dquarkus.http.port=8081 -jar aggregator/target/information-return-summary-aggregator-1.0-SNAPSHOT-runner.jar

# use debzium tools to view aggregation topic and web service
docker run --tty --rm -i --network ks debezium/tooling:1.1

kafkacat -b 192.168.1.214:9092 -C -o beginning -q -t information-return-summary

http 192.168.1.214:8081/information-return/summary
http 192.168.1.214:8081/information-return/summary/2020

# OpenShift
mvn clean package \
-Dquarkus.kubernetes.deploy=true \
-Dquarkus.openshift.env-vars.kafka-bootstrap-server.value=my-cluster-kafka-bootstrap:9092

oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-25-rhel7:1.5.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic information-return-events --property parse.key=true --property key.separator=":"
If you don't see a command prompt, try pressing enter.
>1-2020:Submitted
>1-2021:Submitted
>1-2020:Validated
>2-2020:Submitted
>1-2020:Accepted
>2-2020:Validated
>2-2020:Rejected
etc