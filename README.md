# Simple Hive

![simple-hive-logo](https://docs.google.com/drawings/d/1oxi8BlLNEbHX0-vTDHRrdu1fgh55Q7FkP8uq66YA51A/pub?h=100)

This is a _simple_ Hive as a service for Cloud Foundry. The service broker is included. It is _simple_
because it is meant for client development and testing. It neither has authentication, nor authorization
and data will be gone after restarting the service.

On top of that it serves as an example for leveraging Cloud Foundry's concept of services and their brokers.

Disclaimer:
 * this is not production-grade software
 * the service instance is not secured

## What you will find inside
We've created three applications:
 * a simple Hive service implementation
 * a service broker
 * a sample client which exercises both the service and the broker
 
They come together to fulfill help in these situations: 
> "I am developing an application and it depends on Hive. What now?"

> "Somebody mentioned _Cloud Foundry_, _services_ and _brokers_ and I didn't get it"

The intention of _Simple Hive_ is to solve both. Firstly, it satisfies the need for an easy-to-run
Hive service. Secondly, it explores how the Cloud Foundry services and brokers can be leveraged. We've
learned a thing or two about Cloud Foundry and want to share.

Lastly, we've had fun doing this in Kotlin.

## (Simple) Hive

To put it in nutshell, [Apache Hive](https://hive.apache.org/) provides an SQL-like interface to your Hadoop store. Usually, the required
infrastructure is a little involved. You need to configure Hadoop, etc. This is where _Simple Hive_ steps in.
It abstracts from all dependencies as a single runnable Spring-Boot application. All data is stored in a
local directory and in-memory database. That means that all data is lost once the app is restarted. This is
not necessarily in agreement with [Cloud Foundry's consideration for cloud-native applications](https://docs.cloudfoundry.org/devguide/deploy-apps/prepare-to-deploy.html#filesystem).
But it just works and keeps things simple.

Simple Hive is using _Hive 1.2.1_.

## Cloud Foundry services and brokers

note to self: (Cloud Foundry is a PaaS. That means it abstracts from the underlying infrastructure and deploying apps is simple.)

If your app depends on a service like a database, you can take care of deployment and configuration yourself.
However, that can be tedious and error-prone. Cloud Foundry can do the heavy-lifting for you. It provides the concept
of _services_(things apps can depend on) and _service brokers_(things that manage service instances). All that is
left to the application developer is to inform Cloud Foundry about its dependencies. They provisioning of instances
and the wiring up is taken care of then.

In our case, Hive is the service we are depending on. We also want a broker to manage it for our applications.
Brokers can be any kind of application deployed anywhere as long as it implements the [Service Broker API](https://docs.cloudfoundry.org/services/api.html#api-overview).
It has two responsibilities:
 * Manage service instance creation and deletion.
 
   (In our case it is the creation and deletion of a database within Simple Hive.)
    
 * Help apps bind and unbind themselves to and from instances of the service.
 
   (In our case it is simply about sharing the database connection Url with the application. Nothing happens when we unbind.) 
 
Interestingly enough, the _service broker API_ is about [to be standardized](https://www.openservicebrokerapi.org/).
See another example of a service broker [here](https://github.com/spring-cloud-samples/cloudfoundry-service-broker/tree/master/src/main/java/org/springframework/cloud/servicebroker/mongodb).

## The sample client

The sample client showcases all three parties at play.
Its purpose is to record ping pong wins for players.
It writes those to Simple Hive and reads them from it.
The sample client is not necessary to run Simple Hive.

## Usage

You can easily deploy the service, broker and sample client to any CF.
```bash
cf login  # login to your CF
BROKER_ADMIN_PASSWORD=<secret-password> ./deploy.sh
./test.sh
```

You can deploy each party individually like so:
```bash
./service/deploy.sh
BROKER_ADMIN_PASSWORD=<secret-password> ./broker/deploy.sh
./sample-client/deploy.sh
```