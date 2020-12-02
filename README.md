# OpenShift AMQ Streams (Kafka) Demo
Demo to showcase AMQ Streams (Kafka) resiliency and parallelism on OpenShift

## Architecture

TODO

* HTTP/XML based service that takes requests and pushes to Kafka topic
* Resilient consumers (simulate consumer crash and revival)
* Multiple consumers for different use cases (e.g. transactional vs analytical)
* Meta-topic and dashboard for status tracking
