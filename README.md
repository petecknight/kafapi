

install confluent kafka locally and run 
```
confluent local services start
```
create the topic before running the app (required for kstreams)

`
kafka-topics --zookeeper localhost:2181 --create --topic messages --partitions 3 --replication-factor 1
`

look up your ip address (you won't be able to "localhost" from within kubernetes!)

<hint> ifconfig on a mac will give a whole load of config but you're interested in **inet** value

```
en0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500
ether f0:18:98:2a:c7:af
inet6 fe80::8e3:7062:ec1d:a540%en0 prefixlen 64 secured scopeid 0xa
inet 192.168.0.38 netmask 0xffffff00 broadcast 192.168.0.255
nd6 options=201<PERFORMNUD,DAD>
media: autoselect
status: active	
```

use this value to update the advertised.listeners in [server.properties](https://github.com/petecknight/kafka-key-value-store/tree/master/images/server.proprties.advertised.listeners.png) in the confluent kafka installation

use this value to configure the host for the bootstrap servers in the kstreams and producer properties

the host for each replica will come from the kubernetes exposed as status.podIP

creating a record using the POST endpoint will use the producer to send a message to kafka

```http -j POST localhost:30010/messages key=$key value=$value```

simultaneously, that pod's topology consumes from the same topic adding the message to its local stateStore.

getting a record using the GET by key endpoint will try the local stateStore where the request was routed to. If it is
not found there, it uses the "streams metadata" to locate the pod that holds the stateStore where it is held and looks
it up from there.  

```localhost:9002/messages/3```

should you find a rogue process holding onto a port, you can identify and kill it by id

`lsof -ti:8010 | xargs kill`












