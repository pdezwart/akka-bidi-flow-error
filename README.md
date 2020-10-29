Sample repository to exhibit integration issue between akka-http-metrics and akka-http.

To reproduce, run the server locally, and run the following `curl` request:

```
curl -v "http://localhost:8080/"
```

Immediately send Ctrl^C once initiated, which will cancel the request. The following logs will be outputted in the server console:

```
[ERROR] [10/29/2020 13:14:34.608] [test-akka.actor.default-dispatcher-12] [akka.actor.ActorSystemImpl(test)] Internal server error, sending 500 response
akka.http.impl.util.One2OneBidiFlow$OutputTruncationException: Inner flow was completed without producing result elements for 1 outstanding elements
	at akka.http.impl.util.One2OneBidiFlow$OutputTruncationException$.apply(One2OneBidiFlow.scala:22)
	at akka.http.impl.util.One2OneBidiFlow$OutputTruncationException$.apply(One2OneBidiFlow.scala:22)
	at akka.http.impl.util.One2OneBidiFlow$One2OneBidi$$anon$1$$anon$4.onUpstreamFinish(One2OneBidiFlow.scala:97)
	at akka.stream.impl.fusing.GraphInterpreter.processEvent(GraphInterpreter.scala:523)
	at akka.stream.impl.fusing.GraphInterpreter.execute(GraphInterpreter.scala:390)
	at akka.stream.impl.fusing.GraphInterpreterShell.runBatch(ActorGraphInterpreter.scala:625)
	at akka.stream.impl.fusing.GraphInterpreterShell$AsyncInput.execute(ActorGraphInterpreter.scala:502)
	at akka.stream.impl.fusing.GraphInterpreterShell.processEvent(ActorGraphInterpreter.scala:600)
	at akka.stream.impl.fusing.ActorGraphInterpreter.akka$stream$impl$fusing$ActorGraphInterpreter$$processEvent(ActorGraphInterpreter.scala:769)
	at akka.stream.impl.fusing.ActorGraphInterpreter$$anonfun$receive$1.applyOrElse(ActorGraphInterpreter.scala:784)
	at akka.actor.Actor.aroundReceive(Actor.scala:537)
	at akka.actor.Actor.aroundReceive$(Actor.scala:535)
	at akka.stream.impl.fusing.ActorGraphInterpreter.aroundReceive(ActorGraphInterpreter.scala:691)
	at akka.actor.ActorCell.receiveMessage(ActorCell.scala:577)
	at akka.actor.ActorCell.invoke(ActorCell.scala:547)
	at akka.dispatch.Mailbox.processMailbox(Mailbox.scala:270)
	at akka.dispatch.Mailbox.run(Mailbox.scala:231)
	at akka.dispatch.Mailbox.exec(Mailbox.scala:243)
	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)

13:14:35.404 [scala-execution-context-global-35] INFO  c.g.p.akka.bidi.flow.error.Main$ - Response sent

```

This can avoided by removing the `newMeteredServerAt` call with a standard `newServerAt`, suggesting that the `akka-http-metrics` implementation is not properly handling a user-initiated request cancellation.

