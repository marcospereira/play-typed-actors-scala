package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

case class Event(name: String)

// Using an object to demonstrate two usages of Akka Typed API.
object EventBehavior {

  val behavior: Behavior[Event] =
    Behaviors.receiveMessage {
      msg =>
        println(s"foo => ${msg.name}")
        Behaviors.same
    }
}
