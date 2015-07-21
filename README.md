# Kafka to OpenTSDB

This project takes messages from a Kafka queue and inserts them into an OpenTSDB database. 
The project is written in Scala and uses the Play framework and the actor (AKKA) model. 

# Background
The project was built using the Typesafe Activator IDE and Eclipse. 
Activator is the best place to start. 

The project is built using sbt. The build file is build.sbt. The build dependencies are all defined in there. 

{note}
If changing anything, be careful with the Scala, Play etc versions you specify. There are significant changes between library versions, so the dependencies are sensitive to eachother. 
{note}

## Config
The config is defined in src/main/resources/application.conf. 
The Kafka consumer is defined in there - including the zookeeper address and the kafka server address. 
The project uses the Kafka server on 10.10.24.130, which is the Openstack machine. 
Change this if you want to use a different Kafka server. 

You can ignore the producer configuration. 

## Code
The code is under src/main/scala/example/core.
The main logic is in HelloAkkaKafka.scala

It consists of an App, which you can run from the command line. 
The App creates an actor, which inserts JSON messages into OpenTSDB. 
The App starts a Kafka consumer, which consumes from the topic 'testTopic', and for each
message invokes the actor. 

The actor plays around with the JSON, adding account ID, and then inserts the message into the DB. 
The message format I have been using looks like 
```
{ "metric": "sys.cpu.nice", "timestamp": 1437482234, "value": 18, "tags": { "host": "web01", "dc": "lga"}}

The openTSDB is on http://sparkie02.aepona.com:10002. It is configured to automatically create a metric if it doesn't already exist. 

The code 
```scala
val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

and the 'builder' and 'client' lines that follow are required because we are not running this from inside a play container - we are running it from the command line. 


## Usage

1. Build the project from sbt or Typesafe activator. 
2. Go into the project level directory 
3. sbt run
..* You will be given a number of options 
..1. HelloAkkaJava
..2. example.core.ConsumerStreamExample
..3. example.core.HelloAkkaKafka
..4. example.core.HelloAkkaScala
4. Choose example.core.HelloAkkaKafka (3)

That will start the app and it will listen for messages on the Kafka queue

The easiest way to prime the Kafka queue is
1. Log into 10.10.24.130 (root/password) on another terminal
2. cd /data/kafka_2.10-0.8.2.1
3. bin/kafka-console-producer.sh --broker-list localhost:9092 --topic testTopic
..* This starts the producer waiting for messages
4. Enter a message, e.g.
..* { "metric": "sys.cpu.nice", "timestamp": 1437482234, "value": 18, "tags": { "host": "web01", "dc": "lga"}}

You will see output on the app terminal confirming whether the message is processed. 




