import os
from flask import Flask, request, abort
from xml.parsers.expat import ExpatError
from xmltodict import parse
from kafka import KafkaProducer

application = Flask(__name__)

# producer = KafkaProducer(compression_type='gzip')
producer = KafkaProducer(bootstrap_servers=os.getenv("KAFKA_HOST"))
intake_topic = os.getenv("KAFKA_INTAKE_TOPIC")

@application.route('/data', methods=['POST'])
def push_data():
    """Pushes a XML message to a Kafka topic"""
    if request.method == 'POST':

        if request.headers['Content-Type'] != 'application/xml':
            abort(415)

        try:
            doc = parse(request.data)
            message = doc['xml']['message']
        except (KeyError, ExpatError):
            abort(400)

        producer.send(intake_topic, message.encode('UTF-8'))       # Asynchronous, order not guaranteed

        return "Message received: " + doc['xml']['message']
