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
 * a [Spring cloud connector](http://cloud.spring.io/spring-cloud-connectors/spring-cloud-connectors.html) for Simple Hive
 * a sample client which showcases the broker, service and connector in action
 
They come together to fulfill help in these situations: 
> "I am developing an application and it depends on Hive. What now?"

> "Somebody mentioned _Cloud Foundry_, _services_ and _brokers_ and I didn't get it"

The intention of _Simple Hive_ is to solve both. Firstly, it satisfies the need for an easy-to-run 
Hive service. Secondly, it explores how Cloud Foundry services and brokers can be leveraged. We've
learned a thing or two about Cloud Foundry and want to share.

Lastly, we've had fun doing this in Kotlin.

## (Simple) Hive

To put it in nutshell, [Apache Hive](https://hive.apache.org/) provides an SQL-like interface to your Hadoop store.
Usually, the required infrastructure is a little involved. You need to configure Hadoop, etc. This is where _Simple Hive_ steps in.
It abstracts all dependencies into a single runnable Spring-Boot application. All data is stored in a
local directory and in-memory database. That means that all data is lost once the app is restarted. This is
not necessarily in agreement with [Cloud Foundry's consideration for cloud-native applications](https://docs.cloudfoundry.org/devguide/deploy-apps/prepare-to-deploy.html#filesystem).
But it just works and keeps things simple.

Simple Hive is using _Hive 1.2.1_.

## Cloud Foundry services and brokers

Let's say your app depends on a service(a database for instance). You could deploy and configure that service yourself.
However, that can be tedious and error-prone. Cloud Foundry can do the heavy-lifting for you. It provides the concept
of _services_(things apps can depend on) and _service brokers_(things that manage these services for you). All that is
left to the application developer is to inform Cloud Foundry about its dependencies. The provisioning of instances
and the wiring up is taken care of.

In our case, Simple Hive is the service we are depending on. We also want a broker to manage instances of it for our
applications. A broker can be any kind of application that implements the
[Service Broker API](https://docs.cloudfoundry.org/services/api.html#api-overview) and it can deployed anywhere.
It has two responsibilities:

 * Manage service instance creation and deletion.
 
   (In our case it is the creation and deletion of a database within Simple Hive.)
    
 * Help apps bind and unbind themselves to and from instances of the service.
 
   (In our case it is simply about sharing the database connection url with the application. Nothing happens on unbind.) 
 
At the time of writing, interestingly enough, the _service broker API_ is about [to be standardized](https://www.openservicebrokerapi.org/).
See another example of a service broker [here](https://github.com/spring-cloud-samples/cloudfoundry-service-broker/tree/master/src/main/java/org/springframework/cloud/servicebroker/mongodb).
Furthermore, it is worth pointing out that the service broker is a very powerful abstraction. It is certainly not limited
to the case of databases. For example, it could just as well manage test accounts in your payment providers sandbox.

There's [a framework](https://github.com/spring-cloud/spring-cloud-cloudfoundry-service-broker) for implementing brokers
in Java with Spring. It will greatly simplify your efforts. What you will get is a Spring Boot app that you can deploy
to your Cloud Foundry. However, there are other ways to achieve the same goal, with [Ruby](https://github.com/cloudfoundry-samples/github-service-broker-ruby)
for instance.

There is a [deployment script](broker/deploy.sh) for the Simple Hive service broker.
However, it simply comes down to this command:
```bash
cf create-service-broker <brokername> <username> <password> <broker-url>
```

## Cloud connector

When we bind an app to a service instance, Cloud Foundry injects information about the instance into the app's environment.
In the case of Simple Hive you will find a database url in `VCAP`:
```json
{
 "VCAP_SERVICES": {
  "simple-hive": [
   {
    "credentials": {
     "uri": "hive2://admin:secret-admin-password@simple-hive-service.local.pcfdev.io:80/your-db;transportMode=http;httpPath=simple-hive"
    },
    "label": "simple-hive",
    ...
    ]
   }
  ]
 }
}
```

In order to help Spring automatically detect that URL and create a `DataSource` for us, we've implemented a `HiveConnector`
and registered it in [Spring Cloud Connectors](http://cloud.spring.io/spring-cloud-connectors/spring-cloud-connectors.html).
The connector is situated in its own module `hive-connector`.

Spring Cloud Connectors has some built-in connectors for the the most popular use-cases, e.g. Postgres, SMTP, etc. If your
service is not listed you can build your own. Refer to the [documentation](http://cloud.spring.io/spring-cloud-connectors/spring-cloud-spring-service-connector.html#_connecting_to_generic_services)
for guidance.

## The sample client

The sample client showcases all three parties in action. Its purpose is to record ping pong wins for players.
It writes wins to Simple Hive and reads a count from it. (You don't need the sample client to run Simple Hive.)

Let's say you deployed everything(`./deploy.sh`) to your [PCF dev](https://pivotal.io/pcf-dev) and the sample client is
reachable at `sample-client.local.pcfdev.io`. Let's say Alfred has won a game of ping pong. We can record Alfred's win like so:
```bash
curl -XPOST http://sample-client.local.pcfdev.io/alfred
```

We can get a count of his wins by `GET`ting the same endpoint:
```bash
curl -XGET http://sample-client.local.pcfdev.io/alfred
```

## Usage & deployment

You can easily deploy the service, broker and sample client to any CF. Assuming your logged in to CF, just set an admin password for the broker
and run the deployment script:
```bash
BROKER_ADMIN_PASSWORD=<secret-password> ./deploy.sh
```

You can confirm your deployment with `./test.sh`.

You can also deploy each party individually:
```bash
./service/deploy.sh

./broker/deploy.sh  # make sure BROKER_ADMIN_PASSWORD is set  

./sample-client/deploy.sh
```