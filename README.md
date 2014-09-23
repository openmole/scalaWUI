# ScalaTraJSTagsWireRx #

The project aims at building a small but complete client / server application using very powerfull scala tools to construct fully typed and reactive Web applications. Among them:

- [scalatra](http://scalatra.org/)
- [scalajs](https://github.com/scala-js/scala-js)
- [scalatags](https://github.com/lihaoyi/scalatags)
- [scala.rx](https://github.com/lihaoyi/scala.rx)
- [autowire](https://github.com/lihaoyi/autowire)

This prototype exposes a Graph editor inspired from [http://bl.ocks.org/cjrd/6863459](http://bl.ocks.org/cjrd/6863459) javascript example.

## Build & Run ##

```sh
$ cd scalaTraJSTagsWireRx
$ sbt
> toJS // Build the client JS files
> container:restart // Start the server
```

Then open [http://localhost:8080/](http://localhost:8080/) in your browser.

## Play with the graph ##

- drag to translate the graph
- shift-click on graph to create a node
- shift-click on a node and then drag to another node to connect them with a directed edge
- click on node or edge and press delete to delete


![](http://public.iscpif.fr/~leclaire/graph.png)