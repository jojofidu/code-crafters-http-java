[![progress-banner](https://backend.codecrafters.io/progress/http-server/07b1ad82-3da9-4700-a8bc-4dc344d4fa0b)](https://app.codecrafters.io/users/codecrafters-bot?r=2qF)

This is a starting point for Java solutions to the
["Build Your Own HTTP server" Challenge](https://app.codecrafters.io/courses/http-server/overview).

[HTTP](https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol) is the
protocol that powers the web. In this challenge, you'll build a HTTP/1.1 server
that is capable of serving multiple clients.

Along the way you'll learn about TCP servers,
[HTTP request syntax](https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html),
and more.

**Note**: If you're viewing this repo on GitHub, head over to
[codecrafters.io](https://codecrafters.io) to try the challenge.

# Run in codecrafters

## Set remote git URL

```shell
git remote set-url origin https://git.codecrafters.io/744192a36faeb098
```
will need to reset for github afterwards

Is it possible to have two? Maybe, and call one origin and other originX, this could be better!

## Lombok issue

When running in code-crafters lombok is not working.
So either "Delombok" the existing code or use the /no-lombok code

# Test

Server alive
```shell
curl -v http://localhost:4421
```

Echo value back
```shell
curl -v http://localhost:4421/echo/<value>
```

Return User-Agent header
```shell
curl -v http://localhost:4421/user-agent
curl -v --header "User-Agent: My-Agent" http://localhost:4421/user-agent
```

Concurrent connections, need to use command in 2 terminals
```shell
nc localhost 4221
```

**To use /files API we need to start program with `--directory` flag**

Get file contents
```shell
curl -v http://localhost:4421/files/<file-path>
```

Store file
```shell
curl -v http://localhost:4421/files/<file-path> -d "My file data\nIshere"
```

**Note for Encodings:** 

Request 1:
```
GET /echo/foo HTTP/1.1
...
Accept-Encoding: unsupported-encoding-1, gzip
```

Response 1:
```
HTTP/1.1 200 OK
Content-Encoding: gzip
Content-Type: text/plain
Content-Length: 3

foo
```

Request 2:
```
GET /echo/foo HTTP/1.1
...
Accept-Encoding: unsupported-encoding-1
```

Response 2:
```
HTTP/1.1 200 OK
Content-Type: text/plain
Content-Length: 3

bar
```

Receive gzip encoding of echo
```shell
curl -v --header "Accept-Encoding: unsupported-1, gzip" -o <path-for-new-gzip-file> http://localhost:4221/echo/<value>
```
