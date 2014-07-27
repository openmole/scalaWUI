# ScalaTraJSTagsWireRx #

The project aims at building a full client / server application using very powerfull scala tools to construct fully typed and reactive Web apllications.

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
