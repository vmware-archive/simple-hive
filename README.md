# Simple Hive

![simple-hive-logo](https://docs.google.com/drawings/d/1oxi8BlLNEbHX0-vTDHRrdu1fgh55Q7FkP8uq66YA51A/pub?h=100)

This is a _simple_ Hive as a service for Cloud Foundry. This is for you if you find yourself in either of these situations:

> "I am developing an application and it depends on Hive. What now?"

> "Somebody mentioned _Cloud Foundry_, _services_ and _brokers_ and I didn't get it."

Simple Hive is meant to be easy-to-use, for client development and testing. It is stand-alone and can be run locally
or deployed to Cloud Foundry. It neither has authentication, nor authorization and data will be gone after restarting the service.

We have used the project also to explore how CF services and brokers work. We've learned a thing or two about CF and want to share.

Disclaimer:
 * this is not production-grade software
 * the service instance is not secured

## Contents
* [(Simple) Hive](#simple-hive-1)
* [Cloud Foundry services and brokers](#cf-services-and-brokers)
* [Cloud connector](#cloud-connector)
* [A sample client](#a-sample-client)
* [Usage & deployment](#usage--deployment)

## (Simple) Hive

To put it in nutshell, [Apache Hive](https://hive.apache.org/) provides an SQL-like interface to your Hadoop store.
Usually, the required infrastructure is a little involved. You need to configure Hadoop, etc. This is where _Simple Hive_ steps in.
It abstracts all dependencies into a single runnable Spring-Boot application which can be simply deployed to any CF. 
All data stored in a local directory and in-memory database. All data is reset on service restart. This is
not necessarily in agreement with [CF's consideration for cloud-native applications](https://docs.cloudfoundry.org/devguide/deploy-apps/prepare-to-deploy.html#filesystem).
But it just works and keeps things simple.

_Simple Hive is using Hive 1.2.1._

## Cloud Foundry services and brokers

Let's say your app depends on a service, a database for instance. You could deploy and configure that database yourself.
However, getting all the configuration right is tedious and error-prone. CF can do the heavy-lifting for you. It provides two very helpful things:
 * _services_: things apps can depend on, and
 * _service brokers_: things that manage these services for you
All that is left to the application developer is to inform CF about its dependencies. The provisioning of instances
and the wiring up is taken care of. Usually, one would bind apps and their services via CF's `manifest.yml`.

In our case, Simple Hive is the service we want to depend on. We also want a broker to manage instances of it for our
applications. A broker can be any kind of application that implements the [Service Broker API](https://docs.cloudfoundry.org/services/api.html#api-overview).
Also, it can be deployed anywhere as long as we can talk to it. A broker has three responsibilities:

 * Manage a catalog of _plans_ that your service offers, e.g. free, paid, more memory, less memory, ...
   (The Simple Hive broker offers only one plan. It does not incur cost.)

 * Manage service instance creation and deletion.
   (In our case it is the creation and deletion of a database within Simple Hive.)
    
 * Help apps bind and unbind themselves to and from instances of the service.
   (In our case it is simply about sharing the database connection url with the application. Nothing happens on unbind.) 
 
At the time of writing and interestingly enough, the _service broker API_ is about [to be standardized](https://www.openservicebrokerapi.org/).
See another example of a service broker [here](https://github.com/spring-cloud-samples/cloudfoundry-service-broker/tree/master/src/main/java/org/springframework/cloud/servicebroker/mongodb).

The service broker is a very powerful abstraction. It is certainly not limited to databases. For example, it could just as well manage test accounts in your payment providers' sandbox.

There's [a framework](https://github.com/spring-cloud/spring-cloud-cloudfoundry-service-broker) for implementing brokers
in Java with Spring. It greatly simplifies your efforts. What you will get is a Spring Boot app that you can deploy
to your CF. However, there are other ways to achieve the same goal, with [Ruby](https://github.com/cloudfoundry-samples/github-service-broker-ruby)
for instance and deploy anywhere.

There is a [deployment script](broker/deploy.sh) for the Simple Hive service broker.
However, installing a service broker on your CF is as simple as:
```bash
cf create-service-broker <brokername> <username> <password> <broker-url>
```
Now you can create instances of the service and bind apps to it.

## Cloud connector

When we bind an app to a service instance, CF injects information about the service instance into the app's environment.
Specifically, you will that information in the environment variable `VCAP_SERVICES`. In the case of Simple Hive it will
manifest as something like this:
```json
{
 "VCAP_SERVICES": {
  "simple-hive": [
   {
    "credentials": {
     "uri": "hive2://admin:secret-admin-password@simple-hive-service.local.pcfdev.io:80/your-db;transportMode=http;httpPath=simple-hive"
    }
   }
  ]
 }
}
```

Spring automatically creates `DataSource` beans if it has all necessary information. In order to help turn Hive's URI into a
`DataSource` bean we have implemented a `HiveDataSourceCreator` and registered it in [Spring Cloud Connectors](http://cloud.spring.io/spring-cloud-connectors/spring-cloud-connectors.html).
The connector is situated in its own module `hive-connector`.

Spring Cloud Connectors has some built-in service creators for the the most popular use-cases, e.g. Postgres, SMTP, etc.
If your service is not included you can build your own. Refer to the [documentation](http://cloud.spring.io/spring-cloud-connectors/spring-cloud-spring-service-connector.html#_connecting_to_generic_services) for guidance.

## A sample client

The sample client showcases all parties in action; Simple Hive, its broker and a client. 
Its purpose is to record ping pong wins for players.
It writes wins to Simple Hive and reads a count from it. (btw You don't need the sample client to run Simple Hive.)

Let's say you deployed everything(`./deploy.sh`) to your [PCF dev](https://pivotal.io/pcf-dev) and the sample client is
reachable at `sample-client.local.pcfdev.io`. Let's say Alfred has won a game of ping pong. We can record Alfred's win like so:
```bash
curl -XPOST http://sample-client.local.pcfdev.io/alfred
```

We can get a count of his wins by `GET`ting the same endpoint:
```bash
curl -XGET http://sample-client.local.pcfdev.io/alfred
```

Under the hood, the sample client is relying on the automatically provided `JdbcTemplate` bean. It itself has no knowledge
of where or what Simple Hive is. Cloud Foundry and Spring hide all those details.

## Usage & deployment

You can easily deploy the service, broker and sample client to any CF. Assuming your logged in to CF, run the deployment script:
```bash
./deploy.sh
```

You can also deploy each party individually:
```bash
./service/deploy.sh

./broker/deploy.sh  

./sample-client/deploy.sh
```

You can confirm your deployment with `./test.sh`. Or play with the sample client yourself:

```bash
$ curl -XPOST http://sample-client.local.pcfdev.io/alfred
$ curl -XPOST http://sample-client.local.pcfdev.io/alfred
$ curl -XGET http://sample-client.local.pcfdev.io/alfred
2
```

You can configure the admin password for the broker by setting `BROKER_ADMIN_PASSWORD`. Otherwise, it's a random
password which appears on stdout when deploying:
```bash
### Attention: BROKER_ADMIN_PASSWORD set to: 25184
```