Glekko = Global + Pekko

Glekko is a Scala library that allows you to register and lookup actors by names
in a Pekko cluster. See:

* `Erlang's "global" module <http://erlang.org/doc/man/global.html>`_
* `Pekko's cluster feature <https://pekko.apache.org/docs/pekko/current/cluster-usage.html>`_

Glekko is used in `Xitrum <http://xitrum-framework.github.io/>`_ to implement
its distributed `SockJS <https://github.com/sockjs/sockjs-client>`_ feature.

See `Glekko's Scaladoc <http://xitrum-framework.github.io/glekko>`_.

SBT
---

::

  libraryDependencies += "tv.cntt" %% "glekko" % "1.0.0"

Create registry
---------------

::

  import org.apache.pekko.actor.ActorSystem
  import glekko.Registry

  val system    = ActorSystem("MyClusterSystem")
  val proxyName = "my proxy name"
  val registry  = Registry.start(system, proxyName)

* You can start multiple registry actors. They must have different ``proxyName``.
* For convenience, ``proxyName`` can be any String, you don't have to URI-escape it.

Register actor by props
-----------------------

::

  // For convenience, ``actorName`` can be any String, you don't have to URI-escape it.
  val actorName = "my actor name"

  // Props to create the actor you want to register.
  val props = ...

  registry ! Registry.Register(actorName, props)

If the named actor exists, the registry will just return it. You will receive:

::

  Registry.Found(actorName, actorRef)

Otherwise ``props`` will be used to create the actor locally (when the actor
dies, it will be unregistered automatically). You will receive:

::

  Registry.Created(actorName, actorRef)

If you don't need to differentiate ``Found`` and ``Created``:

::

  registry ! Registry.Register(actorName, props)
  context.become {
    case msg: Registry.FoundOrCreated =>
      val actorName = msg.name
      val actorRef  = msg.ref
  }

Register actor by ref
---------------------

::

  registry ! Registry.Register(actorName, actorRefToRegister)

If the actor has not been registered, or has already been registered with the
same name, you will receive:

::

  Registry.Registered(actorName, actorRef)

Otherwise if there's another actor that has been registered with the name, you
will receive:

::

  Registry.Conflict(actorName, otherActorRef, actorRefToRegister)

In this case, you may need to stop ``actorRefToRegister``, depending on your
application logic.

Lookup actor by name
--------------------

Send:

::

  registry ! Registry.Lookup(actorName)

You will receive:

::

  Registry.Found(actorName, actorRef)

Or:

::

  Registry.NotFound(actorName)

Tell
----

If you don't want to lookup and keep the actor reference:

::

  registry ! Registry.Tell(actorName, msg)

::

  registry ! Registry.Tell(actorName, props, msg)

* If the named actor exists, msg will be sent to it.
* Otherwise, `props` will be used to create the named actor, and msg will be sent to it.

Cluster
-------

Glekko can run in Pekko non-cluster mode (local or remote). While developing, you
can run Pekko in local mode, then later config Pekko to run in cluster mode.

In cluster mode, Glekko uses
`Pekko's Cluster Singleton Pattern <https://pekko.apache.org/docs/pekko/current/cluster-singleton.html>`_
to maintain an actor that stores the name -> actorRef lookup table.

Pekko config file for a node should look like ``config_example/application.conf``
(note ``MyClusterSystem`` in the source code example above and in the config file).
