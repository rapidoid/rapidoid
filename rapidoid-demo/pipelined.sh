seq 1 10000 | xargs -Inone printf "GET / HTTP\n\n" | nc -w 2 localhost 8080 | grep World | wc

