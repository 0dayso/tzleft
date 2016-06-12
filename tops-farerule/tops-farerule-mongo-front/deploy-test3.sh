#/bin/bash

selfpath=$(cd "$(dirname "$0")"; pwd)
tops=$(cd "$(dirname "$selfpath/../../..")"; pwd)
bin=$tops/script/auto_deploy.py

$bin -t $tops -s /usr/local/tops-farerule-mongo-tomcat -w tops-farerule-mongo-front -p tops-farerule/tops-farerule-mongo-front -r test3 --host 192.168.161.189 -u root -a Abc12345
