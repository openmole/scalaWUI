# ScalaWUI #

The project aims at building a small but complete client / server application using very powerfull scala tools to construct fully typed and reactive Web applications. Among them:

- [scalatra](http://scalatra.org/)
- [scalajs](https://github.com/scala-js/scala-js)
- [scalatags](https://github.com/lihaoyi/scalatags)
- [scala.rx](https://github.com/lihaoyi/scala.rx)
- [autowire](https://github.com/lihaoyi/autowire)

as well as [scaladget](https://github.com/mathieuleclaire/scaladget) to take adavantage of the mapping of the [http://d3js.org/](D3.js) library.

This prototype exposes a Graph editor inspired from [http://bl.ocks.org/cjrd/6863459](http://bl.ocks.org/cjrd/6863459) javascript example.

## Build & Run##
First, build the javascript:
```sh
$ cd scalaWUI
$ sbt
> go // Build the client JS files and move them to the right place
```

Then, start the server (in your previous sbt session):
```sh
> container:restart // Start the server
```

## Play with the graph ##

Open [http://localhost:8080/](http://localhost:8080/) in your browser.

The demo provides with a small graph based on d3.js library. Try to :
- drag the nodes to move them
- shift-click on graph to create a node
- shift-click on a node and then drag to another node to connect them with a directed edge
- click on node or edge and press delete to delete


![](http://public.iscpif.fr/~leclaire/graph.png)
