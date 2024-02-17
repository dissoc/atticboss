# atticboss-artemis

This allows AtticBoss-based systems
([Immutant](http://immutant.org/), [TorqueBox](http://torquebox.org/))
to use [ActiveMQ Artemis](http://activemq.apache.org/artemis/) instead
of [HornetQ](http://hornetq.org/) for messaging.

## What is Artemis?

Artemis is the next generation of ActiveMQ, and is based on a merging
of the ActiveMQ and HornetQ code bases.

## Usage

In theory, you should be able to exclude
`org.projectodd.atticboss/atticboss-messaging-hornetq` from your
dependencies, and depend directly on
`org.projectodd.atticboss/atticboss-artemis`. In reality, it may be
a tetch more complicated.

### With Immutant

First, you'll need to depend on
[Immutant 2.1.0](http://immutant.org/news/2015/09/01/announcing-2-1-0/)
or newer, then make a few adjustments to your `:dependencies`:

```clojure
:dependencies [[org.immutant/messaging "2.1.3"
                :exclusions [org.projectodd.atticboss/atticboss-messaging-hornetq]]
               [org.projectodd.atticboss/atticboss-artemis "0.2.0"]]
```

Note that this will only currently work outside of WildFly, since
Artemis is not yet available in the container, and this project
doesn't yet provide the proper bindings to access it even if it was
available.

### With TorqueBox

TBD - it will likely require a gem, since the
`atticboss-messaging-hornetq` is embedded within the
`torquebox-messaging` gem.

### Overriding the default configuration

If you need to customize the configuration, you just need to provide a
`broker.xml` on the classpath. You can use
[our default one](https://github.com/projectodd/atticboss-artemis/blob/master/src/main/resources/default-broker.xml)
as a base, and refer to the
[Artemis docs](http://activemq.apache.org/artemis/docs.html) for help.

## License

atticboss-artemis is licensed under the Apache License, v2. See
[LICENSE](LICENSE) for details.
