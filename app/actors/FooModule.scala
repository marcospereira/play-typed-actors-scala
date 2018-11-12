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

// This is the most simple use case where we just have an ActorRef[String]
class FooActorProvider extends Provider[ActorRef[String]] {

  @Inject private var actorSystem: ActorSystem = _

  override def get(): ActorRef[String] = {
    actorSystem.spawn[String](FooBehavior.behavior, "foo-actor-else")
  }
}

// This actor injects our own case class.
class FooBarActorProvider extends Provider[ActorRef[Bar]] {

  @Inject private var actorSystem: ActorSystem = _

  override def get(): ActorRef[Bar] = {
    actorSystem.spawn[Bar](new FooBarBehavior(), "foo-actor-else-bar")
  }
}

class FooActorModule extends AbstractModule {
  override def configure(): Unit = {
    bind(new TypeLiteral[ActorRef[String]]() {})
      .annotatedWith(Names.named("foo-actor"))
      .toProvider(Providers.guicify(new FooActorProvider))
      .asEagerSingleton()

    // We are not using `annotatedWith` here since we are solely trusting
    // the types to do the injection.
    bind(new TypeLiteral[ActorRef[Bar]]() {})
      .toProvider(Providers.guicify(new FooBarActorProvider))
      .asEagerSingleton()
  }
}

// Not used at all right now. The idea is that we should provide
// a way to easily inject typed actors like we do for untyped actors.
// For example, having code like:
// 
// class MyActorModule extends AbstractModule with AkkaTypedInjector {
//   override def configure() = {
//     bindTypedActor[ActorRef[Bar]]("actor-name", MyActorBehavior.behavior)
//   }
// }
trait AkkaTypedInjector {
  self: AbstractModule =>

  private def accessBinder: Binder = {
    val method: Method = classOf[AbstractModule].getDeclaredMethod("binder")
    if (!method.isAccessible) {
      method.setAccessible(true)
    }
    method.invoke(this).asInstanceOf[Binder]
  }

  import scala.reflect.runtime.universe._

  def bindTypedActor[B, A <: ActorRef[B]](actorName: String, behavior: => Behavior[B] = Behaviors.empty)(implicit actorTypeTag: TypeTag[A]): Unit = {
    accessBinder
      // how to transform a TypeTag/ClassTag/etc into a java.lang.reflect.Type?
      .bind(new TypeLiteral[A]() {})
      .toProvider(Providers.guicify[A](ActorTypedProvider[B, A](actorName, behavior)))
  }

  private case class ActorTypedProvider[B, A <: ActorRef[B] : TypeTag](actorName: String, behavior: Behavior[B]) extends Provider[A] {

    @Inject private var actorSystem: ActorSystem = _

    override def get(): A = {
      actorSystem.spawn[B](behavior, actorName).asInstanceOf[A]
    }
  }
}