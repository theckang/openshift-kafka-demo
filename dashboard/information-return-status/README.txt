# pre-reqs
kafka-streams-quickstart: see kafka-streams-quickstart/README.txt

# start the application
mvn clean install
java -Dquarkus.http.port=8082 -jar aggregator/target/information-return-status-aggregator-1.0-SNAPSHOT-runner.jar

# use debzium tools to view aggregation topic and web service
docker run --tty --rm -i --network ks debezium/tooling:1.1

kafkacat -b 192.168.1.214:9092 -C -o beginning -q -t information-return-status

http 192.168.1.214:8082/information-return/status/1-2020
