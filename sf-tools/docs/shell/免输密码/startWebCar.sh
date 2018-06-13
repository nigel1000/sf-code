#!/bin/sh

passwd="*******"

shPath="/Users/nijianfeng/projects/car-tv/car-parent/docs/soft/"
jarPath="/Users/nijianfeng/projects/car-tv/car-parent/web-car/target/"
jarName="web-car-1.0-SNAPSHOT.jar"
remoteJarPath="/root/${jarName}"
echo jarPath is: ${jarPath}${jarName}
chmod 755 ${shPath}startWebCarUpdateJar.exp
chmod 755 ${shPath}startWebCarRunJar.sh
# 免输密码进行文件传输
${shPath}startWebCarUpdateJar.exp ${jarPath}${jarName} ${remoteJarPath} ${passwd}
${shPath}startWebCarUpdateJar.exp ${shPath}startWebCarRunJar.sh /root/startWebCarRunJar.sh ${passwd}

# 免输密码进行远程脚本执行
expect -c "
set timeout -1;
spawn -noecho ssh -o StrictHostKeyChecking=no -p 22 root@114.55.72.6 \"sh /root/startWebCarRunJar.sh ${remoteJarPath}\";
expect \"*password:\";
send \"${passwd}\r\";
expect eof;
"





# chmod 755 /Users/nijianfeng/projects/car-tv/car-parent/docs/soft/startWebCar.sh
# sh /Users/nijianfeng/projects/car-tv/car-parent/docs/soft/startWebCar.sh

