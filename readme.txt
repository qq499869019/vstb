编译方法： mvn package -DskipTests
运行方法： 若.so库不在jar包中，则将jar包和.so库放同级目录
		   运行 nohup java -Djava.library.path=. -jar  PushSystem-0.0.1-SNAPSHOT.jar --spring.profiles.active=linux &