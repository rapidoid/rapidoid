seq 1 10000 | xargs -Inone printf "GET / HTTP/1.1\r\n\r\n" | nc -w 2 localhost 8888 | grep World | wc
