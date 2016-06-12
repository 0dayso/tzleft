#/bin/bash

selfpath=$(cd "$(dirname "$0")"; pwd)
tops=$(cd "$(dirname "$selfpath/../../..")"; pwd)
bin=$tops/script/auto_deploy.py
$bin -t $tops -s /data/tomcat8081 -w tz-eterm-interface-web -p tops-eterm-interface/tz-eterm-interface-web -r test --host 192.168.160.183 -u root -a root
