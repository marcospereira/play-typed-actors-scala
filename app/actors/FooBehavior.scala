package actors

import java.lang.reflect.Method

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.{Behaviors, MutableBehavior}
import akka.actor.typed.{ActorRef, Behavior}
import com.google.inject.name.Names
import com.google.inject.util.Providers
import com.google.inject.{AbstractModule, Binder, TypeLiteral}
import javax.inject.{Inject, Provider}

case class Bar(name: String)

// Using an object to demonstrate two usages of Akka Typed API.
object FooBehavior {

  val behavior: Behavior[String] =
    Behaviors.receiveMessage {
      msg =>
        println(s"foo => $msg")
        Behaviors.same
    }
}

class FooBarBehavior extends MutableBehavior[Bar] {
  override def onMessage(msg: Bar): Behavior[Bar] = {
    println(s"foobar => $msg")
    this
  }
}
