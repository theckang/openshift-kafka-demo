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

Run locally

```bash
pip install -r requirements.txt
export FLASK_APP=app.py
flask run
```

Send sample request

```bash
curl -H "Content-type: application/xml" -X POST http://localhost:5000/data -d '<xml><message>This is a test</message></xml>'
```

