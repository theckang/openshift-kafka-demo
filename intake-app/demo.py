import os, uuid
from flask import Flask, request, abort
from xml.parsers.expat import ExpatError
from xmltodict import parse
from kafka import KafkaProducer

application = Flask(__name__)

producer = KafkaProducer(bootstrap_servers=os.getenv("KAFKA_HOST"), compression_type='gzip')
intake_topic = os.getenv("KAFKA_INTAKE_TOPIC")

@application.route('/data', methods=['POST'])
def push_data():
    """Pushes a XML message to a Kafka topic"""
    if request.method == 'POST':

        if request.headers['Content-Type'] != 'application/xml':
            abort(415)

        try:
            doc = parse(request.data)
        except (ExpatError):
            abort(400)

        producer.send(intake_topic, key = uuid.uuid4().bytes, value=request.data)       # Asynchronous, order not guaranteed

        return "XML received: " + request.data.decode('UTF-8')
