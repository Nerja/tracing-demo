FROM golang:1.22-alpine as builder

RUN go install go.k6.io/xk6/cmd/xk6@latest
RUN  xk6 build --output "/tmp/k6" --with github.com/Nerja/xk6-oteltracing --with github.com/Nerja/xk6-smpp

FROM alpine:3.20.3
COPY --from=builder /tmp/k6 /usr/bin/k6
COPY ./test.js /home/k6/test.js
ENTRYPOINT [ "/usr/bin/k6" ]
CMD [ "run", "/home/k6/test.js" ]
