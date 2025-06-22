package glekko

import org.apache.pekko.actor.{ActorRef, ActorSystem, Props}
import org.apache.pekko.event.Logging
import com.typesafe.config.ConfigFactory

// Only things in this file are public and appear in the Scaladoc.
// Other things are private.

object Registry {
  case class Register(name: String, props_ref: Either[Props, ActorRef])
  object Register {
    def apply(name: String, props: Props)    = new Register(name, Left(props))
    def apply(name: String, ref:   ActorRef) = new Register(name, Right(ref))
  }

  case class Registered(name: String, ref: ActorRef)

  case class Conflict(name: String, ref: ActorRef, failedRef: ActorRef)

  //----------------------------------------------------------------------------

  case class Lookup(name: String)

  abstract class FoundOrCreated { def name: String; def ref: ActorRef }
  case class Found(name: String, ref: ActorRef) extends FoundOrCreated
  case class Created(name: String, ref: ActorRef) extends FoundOrCreated

  case class NotFound(name: String)

  //----------------------------------------------------------------------------

  case class Tell(name: String, propso: Option[Props], msg: Any)
  object Tell {
    def apply(name: String, msg: Any)               = new Tell(name, None, msg)
    def apply(name: String, props: Props, msg: Any) = new Tell(name, Some(props), msg)
  }

  //----------------------------------------------------------------------------

  val clusterMode: Boolean = {
    val config   = ConfigFactory.load()
    val provider = config.getString("pekko.actor.provider")
    provider == "org.apache.pekko.cluster.ClusterActorRefProvider"
  }

  /** @return The registry actor */
  def start(system: ActorSystem, proxyName: String): ActorRef = {
    // Local mode:
    // localRegistry

    // Cluster mode:
    // clusterSingletonProxy -> clusterSingletonManager -> clusterSingletonRegistry
    //
    // When registering, actor ref is created from props locally at
    // clusterSingletonProxy.

    val log = Logging.getLogger(system, this)
    if (clusterMode) {
      log.info(s"""Glekko actor registry "$proxyName" starts in cluster mode""")
      system
        .actorOf(Props(classOf[ClusterSingletonProxy], proxyName)
        .withMailbox("pekko.actor.mailbox.unbounded-deque-based"))
    } else {
      log.info(s"""Glekko actor registry "$proxyName" starts in local mode""")
      system.actorOf(Props(classOf[LocalRegistry]))
    }
  }
}
