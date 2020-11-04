from flask import Flask, request, abort
from xml.parsers.expat import ExpatError
from xmltodict import parse

application = Flask(__name__)

# Load env vars

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

        return "Message received: " + doc['xml']['message']
