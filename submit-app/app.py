import os
from kafka import KafkaProducer

intake_topic = os.getnev("KAFKA_INTAKE_TOPIC")
submitted_topic = os.getenv("KAFKA_SUBMITTED_TOPIC")
events_topic = os.getenv("KAFKA_EVENTS_TOPIC")

consumer = KafkaConsumer(intake_topic, bootstrap_servers=os.getenv("KAFKA_HOST"))
producer = KafkaProducer(bootstrap_servers=os.getenv("KAFKA_HOST"))

for msg in consumer:
    print(msg)

#producer.send(submitted_topic, message)       # Asynchronous, order not guaranteed
#producer.send(events_topic, message)
