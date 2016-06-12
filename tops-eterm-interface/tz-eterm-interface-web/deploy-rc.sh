#/bin/bash

selfpath=$(cd "$(dirname "$0")"; pwd)
tops=$(cd "$(dirname "$selfpath/../../..")"; pwd)
bin=$tops/script/auto_deploy.py
$bin -t $tops -s /opt/tops-flight-eterm/apache-tomcat-7.0.39 -w tz-eterm-interface-web -p tops-eterm-interface/tz-eterm-interface-web -r rc --host 192.168.130.79 -u root -a NacVent5
