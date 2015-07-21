package example.core

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import scala.concurrent.duration._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent._
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.Play.current

// case object KGreet
// This actor is invoked each time a message is consumed from the Kafka queue. 
// The actor prints out the message and inserts its content into OpenTSDB 

case class MessagePrinter(message: String)
// case class KGreeting(message: String)

class MessageActor extends Actor {
  var outMessage = ""

  def printBody(body: String): String = {
    println("Body is : " + body)
    return body;
  }

  def processJsonMessage(body: String): String = {

    return body
  }

  def receive = {
    case MessagePrinter(message) => {
      println("MessagePrinter")
      println(s"Message is : $message")

      val jsonMessage: JsValue = Json.parse(message)
      val metric: String = (jsonMessage \ "metric").as[String]
      val tags = (jsonMessage \ "tags")
      println("Metric value is : " + metric)
      println("List of tags is : " + tags.toString())

      val accountId: String = "Seamus01"

      val updatedJson: JsObject = tags.as[JsObject] + ("accountId" -> Json.toJson(accountId))

      val updatedTags: JsValue = updatedJson.as[JsValue]

      //     val updatedJsonMessage : JsObject = jsonMessage.as[JsObject] ++ updatedJson
      val updatedJsonMessage: JsObject = jsonMessage.as[JsObject] ++ Json.obj("tags" -> updatedJson)

      val updatedMessage: String = Json.stringify(updatedJsonMessage)
      println("Updated Message is :" + updatedJsonMessage)

      // We need the line below because we are not executing this from within the play framework. 
      // It is required so we have a context for the async behavior
      val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

      // Again, we need these line below because we are not executing this from within the play framework, 
      // and so we can't just use the WSClient

      val builder = new com.ning.http.client.AsyncHttpClientConfig.Builder()
      val client = new play.api.libs.ws.ning.NingWSClient(builder.build())

      // The following is just a test call to the OpenTSDB to get config, in order to understand
      // how the WSClient library works, especially the async handling. 
      //val complexHolder: WSRequestHolder =
      /*     client.url("http://sparkie02.aepona.com:10002/api/config/").withHeaders("Accept" -> "application/json")
        .withRequestTimeout(10000).get().map {
          response =>
            println("Response Body is : " + response.body)
        }
*/

      // Post the data to the OpenTSDB.
      // Everything up to the 'post' is the invocation of the request to the OpenTSDB.
      // That results in a Future[WSResponse]
      // The '.map' asynchronously handles the response from the OpenTSDB. 

      client.url("http://sparkie02.aepona.com:10002/api/put?details").withHeaders("Accept" -> "application/json",
        "Content-Type" -> "application/json")
        .withRequestTimeout(10000).post(updatedMessage).map {
          response =>
            println("Response Body is : " + response.body)
        }

    }
  }

}

object HelloAkkaKafka extends App {

  println(s"In HelloAkkaKafka")
  // Create the 'helloakka' actor system
  val system = ActorSystem("helloakka")

  // Create the 'Message Processing' actor
  val messageActor = system.actorOf(Props[MessageActor], "messageActor")

  // Create an "actor-in-a-box"
  val inbox = Inbox.create(system)

  // Start the Kafka consumer

  println(s"Starting the Kafka Consumer")

  val topicNames = "testTopic"
  val consumer = SingleTopicConsumer(topicNames)

  // consumer.read().foreach(println)

  // consumer.read().foreach((c: String) => println(c))
  consumer.read().foreach((kafkaMessage: String) => messageActor.tell(MessagePrinter(kafkaMessage), ActorRef.noSender))

}

