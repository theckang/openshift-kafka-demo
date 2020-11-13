# description
Web application that consumes the information-return-summary kafka topic and
displays the continuously updated status using server side eventing

# pre-reqs
kafka-streams: see kafka-streams-quickstart/README.txt
information-return-summary: see information-return-summary/README.txt

# start the application
mvn clean install
java -jar target/information-return-dashboard-1.0-SNAPSHOT-runner.jar

# watch the summary updates in browser
http://localhost:8080/index.html

# OpenShift

mvn clean package -Dquarkus.kubernetes.deploy=true -Dquarkus.openshift.env-vars.kafka-bootstrap-server.value=my-cluster-kafka-bootstrap:9092 -Dquarkus.openshift.env-vars.summary-service-url.value=http://information-return-summary-aggregator-summary.apps.cluster-5cd4.5cd4.example.opentlc.com/information-return
