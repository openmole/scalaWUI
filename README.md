# ScalaTraJSTagsWireRx #

The project aims at building a small but complete client / server application using very powerfull scala tools to construct fully typed and reactive Web applications. Among them:

- [scalatra](http://scalatra.org/)
- [scalajs](https://github.com/scala-js/scala-js)
- [scalatags](https://github.com/lihaoyi/scalatags)
- [scala.rx](https://github.com/lihaoyi/scala.rx)
- [autowire](https://github.com/lihaoyi/autowire)


## Build & Run ##

```sh
$ cd scalaTraJSTagsWireRx
$ sbt
> project ui
> toJS // Build the client JS files
> project server
> container:restart // Start the server
```

Then open [http://localhost:8080/](http://localhost:8080/) in your browser.
