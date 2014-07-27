# ScalaTraJSTagsWireRx #

## Build & Run ##

```sh
$ cd scalaTraJSTagsWireRx
$ sbt
> project ui
> toJS // Build the client JS files
> project server
> container:restart
```

Then open [http://localhost:8080/](http://localhost:8080/) in your browser.
