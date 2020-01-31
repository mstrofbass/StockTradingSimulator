#!/bin/bash

cd ../build/jar/
java -jar Simulator.jar simulate

cd /var/data/stock-data/logs
> option-trans.log

cat option-trans.log.0 >> option-trans.log && cat option-trans.log.1 >> option-trans.log

timestamp=`date '+%Y%m%d.%H%M%S'`

hdfs dfs -cp /user/blake/option-trans.log /user/blake/option-trans.$timestamp.log
hdfs dfs -rm /user/blake/option-trans.log
hdfs dfs -put /var/data/stock-data/logs/option-trans.log /user/blake/option-trans.log
