docker run -d \
    --name revproxy \
    --net=host \
    rapidoid/rapidoid \
    '/foo->https://upstream1:8080,http://upstream2:8080' \
    '/bar->http://upstream3:9090' \
    '/->https://upstream4:8080' \
    on.port=80
