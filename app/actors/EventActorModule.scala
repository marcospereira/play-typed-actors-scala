package actors

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter._
import com.google.inject.{AbstractModule, TypeLiteral}
import javax.inject.{Inject, Provider}

class EventActorProvider @Inject()(actorSystem: ActorSystem) extends Provider[ActorRef[Event]] {
  override def get(): ActorRef[Event] = {
    actorSystem.spawn[Event](EventBehavior.behavior, "foo-actor")
  }
}

class EventActorModule extends AbstractModule {
  override def configure(): Unit = {
    bind(new TypeLiteral[ActorRef[Event]]() {})
      .toProvider(classOf[EventActorProvider])
      .asEagerSingleton()
  }
}
