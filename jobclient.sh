#!/usr/bin/env bash

JOB_CLIENT_HOME="${BASH_SOURCE-$0}"
JOB_CLIENT_HOME="$(dirname "${JOB_CLIENT_HOME}")"
JOB_CLIENT_HOME="$(cd "${JOB_CLIENT_HOME}"; pwd)"

if [ "$JAVA_HOME" != "" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

#把lib下的所有jar都加入到classpath中
for i in "$JOB_CLIENT_HOME"/../lib/*.jar
do
	CLASSPATH="$i:$CLASSPATH"
done

# echo $CLASSPATH

"$JAVA" -cp "$CLASSPATH" com.github.ltsopensource.jobclient.JobClientTest 
