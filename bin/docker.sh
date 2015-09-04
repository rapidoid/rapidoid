mvn clean install -DskipTests=true -Pfull
cp rapidoid.jar docker/
cd docker
ls -l rapidoid.jar
sudo docker build -t rapidoid/rapidoid .

