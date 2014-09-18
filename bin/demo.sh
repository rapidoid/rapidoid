mvn clean install -DskipTests=true
cd rapidoid-demo
mvn exec:java -Dexec.mainClass="org.rapidoid.demo.http.Main" -Dexec.args="$@"
