import os
from kafka import KafkaConsumer, KafkaProducer

intake_topic = os.getenv("KAFKA_INTAKE_TOPIC")
submitted_topic = os.getenv("KAFKA_SUBMITTED_TOPIC")
events_topic = os.getenv("KAFKA_EVENTS_TOPIC")

consumer = KafkaConsumer(intake_topic, bootstrap_servers=os.getenv("KAFKA_HOST"))
producer = KafkaProducer(bootstrap_servers=os.getenv("KAFKA_HOST"))

for msg in consumer:

    # store message in MongoDB

    producer.send(submitted_topic, msg.value)       # Asynchronous, order not guaranteed
    producer.send(events_topic, msg.value)

