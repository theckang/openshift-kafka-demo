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
