import os
from kafka import KafkaConsumer, KafkaProducer
from pymongo import MongoClient

intake_topic = os.getenv("KAFKA_INTAKE_TOPIC")
submitted_topic = os.getenv("KAFKA_SUBMITTED_TOPIC")
events_topic = os.getenv("KAFKA_EVENTS_TOPIC")

consumer = KafkaConsumer(intake_topic, bootstrap_servers=os.getenv("KAFKA_HOST"))
producer = KafkaProducer(bootstrap_servers=os.getenv("KAFKA_HOST"))

mongo_client = MongoClient(host=os.getenv("MONGODB_HOST"), username=os.getenv("MONGODB_USER"), password=os.getenv("MONGODB_PASSWORD"))
db = mongo_client.testdb

for msg in consumer:

    message = msg.value
    information_return = {
        'message': message
    }
    db.information_returns.insert_one(information_return)

    producer.send(submitted_topic, message)       # Asynchronous, order not guaranteed
    producer.send(events_topic, message)
