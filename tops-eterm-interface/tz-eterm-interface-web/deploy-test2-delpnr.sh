#/bin/bash

selfpath=$(cd "$(dirname "$0")"; pwd)
tops=$(cd "$(dirname "$selfpath/../../..")"; pwd)
bin=$tops/script/auto_deploy.py
$bin -t $tops -s /opt/tops-flight-eterm-delpnr/apache-tomcat-7.0.39 -w tz-eterm-interface-web -p tops-eterm-interface/tz-eterm-interface-web -r test2-delpnr --host 192.168.161.208 -u root -a Abc12345
