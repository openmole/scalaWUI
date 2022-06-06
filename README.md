# ScalaWUI (Scala Web UI)

[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.0.0.svg)](https://www.scala-js.org/)


The project aims at building a small but complete client / server application using very powerfull scala tools to construct fully typed and reactive Web applications. Among them:

- [scalajs](https://github.com/scala-js/scala-js)
- [endpoint4s](hhttps://endpoints4s.github.io)
- [scalatags](https://github.com/lihaoyi/scalatags)
- [scala.rx](https://github.com/lihaoyi/scala.rx)
- [akka-http](https://doc.akka.io/docs/akka-http)

as well as [scaladget](https://github.com/mathieuleclaire/scaladget) to draw some svg and display a [http://d3js.org/](D3.js)-like workflow.

It is an empty ready-to-work application, dealing with all the starting wiring. This prototype also exposes as example a small Graph editor inspired from [http://bl.ocks.org/cjrd/6863459](http://bl.ocks.org/cjrd/6863459) javascript example, but written witten in a reactive way thanks to the [scala.rx](https://github.com/lihaoyi/scala.rx) library.

## Build & Run##
First, build the javascript:
```sh
$ cd scalaWUI
$ sbt
> project server 
> run // Build the client JS files and move them to the right place and start the server
```

## Play with the graph ##

Open [http://localhost:8080/](http://localhost:8080/) in your browser.

The demo provides with a small graph based on d3.js library but with no D3 at all. It only relies on the previously cited libraries. Try to :
- drag the nodes to move them
- shift-click on graph to create a node
- shift-click on a node and then drag to another node to connect them with a directed edge
- click on node or edge and press delete to delete


![](http://public.iscpif.fr/~leclaire/graph.png)

Open [http://localhost:8080/documentation.json](http://localhost:8080/documentation.json) to get the REST API documentation.
