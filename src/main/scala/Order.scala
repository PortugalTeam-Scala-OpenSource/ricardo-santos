import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import java.time.YearMonth

object Order {
  case class Product(date: YearMonth)
  case class Item(product: Product)


  sealed trait Commands

  case class GetState(replyTo: ActorRef[Seq[Item]]) extends Commands


  object Empty {
    case class CreateOrder(items: Seq[Item], replyTo: ActorRef[Unit]) extends Commands
    def apply(id: String): Behavior[Commands] =
      Behaviors.receive{
        case (context, commands: Commands) => commands match {
          case CreateOrder(items, replyTo) =>
            replyTo.tell(())
            Created.apply(id)(items)
          case GetState(replyTo) =>
            replyTo.tell(Seq())
            Behaviors.same
        }
      }
  }


  object Created {
    def apply(id: String)(items: Seq[Item]): Behavior[Commands] =
      Behaviors.receiveMessage {
        case GetState(replyTo) =>
          replyTo.tell(items)
          Behaviors.same
        case cmd: Empty.CreateOrder =>
          cmd.replyTo.tell(())
          Behaviors.ignore
      }
  }


}
