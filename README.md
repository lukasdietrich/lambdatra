# lambdatra
[![Build Status](https://drone.io/github.com/lukasdietrich/lambdatra/status.png)](https://drone.io/github.com/lukasdietrich/lambdatra/latest)
[![Maven](https://img.shields.io/github/release/lukasdietrich/lambdatra.svg?label=JitPack%20Maven&style=flat-square)](https://jitpack.io/#lukasdietrich/lambdatra)

Callback oriented http and websocket server based on netty.io

## How to use

```java
public static void main(String... args) {
    Lambdatra.create(80, server -> {
        server
            .on("/path/with/:parameters", (req, res) -> {
                res.write(String.format("Parameter was %s", req.getParam("parameters").get()));
            })
			
		    .onWebSocket("/sockets", MyWebSocket.class); // MyWebSocket has to extend WebSocket
	});
}
```

## Javadoc

Javadocs are available at `https://jitpack.io/com/github/lukasdietrich/lambdatra/${VERSION}/javadoc/`.  
eg. <https://jitpack.io/com/github/lukasdietrich/lambdatra/0.3.2/javadoc/>

## How it works (UML)

![UML](https://raw.githubusercontent.com/lukasdietrich/lambdatra/master/classes.png)

## License

```plain
The MIT License (MIT)

Copyright (c) 2015 Lukas Dietrich

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
