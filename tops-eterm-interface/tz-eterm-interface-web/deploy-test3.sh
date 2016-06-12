#/bin/bash

selfpath=$(cd "$(dirname "$0")"; pwd)
tops=$(cd "$(dirname "$selfpath/../../..")"; pwd)
bin=$tops/script/auto_deploy.py
$bin -t $tops -s /data/tz-eterm-interface-web -w tz-eterm-interface-web -p tops-eterm-interface/tz-eterm-interface-web -r test3 --host 192.168.161.87 -u root -a op3tomcat
