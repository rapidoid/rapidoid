echo Copying On into Admin

cp rapidoid-http-server/src/main/java/org/rapidoid/setup/On.java rapidoid-http-server/src/main/java/org/rapidoid/setup/Admin.java

perl -pi -e 's/\bON\b/ADMIN/' rapidoid-http-server/src/main/java/org/rapidoid/setup/Admin.java
perl -pi -e 's/\bOn\b/Admin/' rapidoid-http-server/src/main/java/org/rapidoid/setup/Admin.java
perl -pi -e 's/\Q.on()\E/.admin()/' rapidoid-http-server/src/main/java/org/rapidoid/setup/Admin.java
perl -pi -e 's/\b4\.3\.0\b/5.1.0/' rapidoid-http-server/src/main/java/org/rapidoid/setup/Admin.java
