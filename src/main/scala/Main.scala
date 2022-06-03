import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.{
  StringDeserializer,
  StringSerializer
}

import java.time.YearMonth
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Random
import java.time.temporal.ChronoUnit._

object Main extends App {
  implicit val actorSystem = ActorSystem("example")

  case class Order(date: YearMonth)

  val iterator: Iterator[Order] = new Iterator[Order] {
    override def hasNext: Boolean = true
    override def next(): Order = Order(date =
      YearMonth.of(Random.between(2020, 2022), Random.between(1, 12))
    )
  }

  val orders: Source[Order, NotUsed] =
    Source
      .fromIterator(() => iterator)
      .throttle(1, 1 second)

  val now = YearMonth.now()

  orders
    .map { order =>
      // Can you calculate here the difference between now and the date of the order?
      // if so, please print on console
      println(order)
      val difference = order.date.until(now, MONTHS)
      println("Diffence in months: " + difference )
    }
    .runWith(Sink.ignore)

  /*
   Using Kafka
   val consumerSettings = ConsumerSettings(
    actorSystem,
    new StringDeserializer,
    new StringDeserializer
  )
    .withBootstrapServers("0.0.0.0:9092")
  val subscription = Subscriptions.topics("chat")
  Consumer
    .plainSource(consumerSettings, subscription)
    .map { message =>
      println(message)
      message
    }
    .runWith(Sink.foreach(println)) */

}
