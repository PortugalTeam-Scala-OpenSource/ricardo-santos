import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.time.YearMonth

object OrdersPerYearMonth {

  // the id is a YearMonth, the id is going to be like 2022-01 or 2021-12
  def apply(id: String)(amountOfOrders: BigInt = 0): Behavior[Commands] = Behaviors.receiveMessage {
    case AddOrder(replyTo)  =>
      replyTo.tell(())
      OrdersPerYearMonth(id)(amountOfOrders + 1)
    case RemoveOrder(replyTo) =>
      replyTo.tell(())
      OrdersPerYearMonth(id)(amountOfOrders - 1)
    case HowManyOrders(replyTo) =>
      replyTo.tell(amountOfOrders)
      Behaviors.same
    case _ => Behaviors.ignore
  }


  sealed trait Commands

  case class AddOrder(replyTo: ActorRef[Unit]) extends Commands
  case class RemoveOrder(replyTo: ActorRef[Unit]) extends Commands
  case class HowManyOrders(replyTo: ActorRef[BigInt]) extends Commands



}
