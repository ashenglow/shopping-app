#!/bin/bash
 CURRENT_PID=$(pgrep -f .jar)
 echo "$CURRENT_PID"
 if [ -z $CURRENT_PID ]; then
         echo "no process"
 else
         echo "kill $CURRENT_PID"
         kill -9 $CURRENT_PID
         sleep 3
 fi

 JAR_PATH="/home/ubuntu/cicd/*.jar"
 echo "jar path : $JAR_PATH"
 chmod +x $JAR_PATH
 nohup java -jar -Dspring.profiles.active=prod $JAR_PATH>> /home/ubuntu/cicd/deploy.log 2>> /home/ubuntu/cicd/deploy_err.log &
 echo "jar file deploy success"