#!/bin/sh



chmod 755 $1
#设置 service app stop|start|restart 启动方式
#ln -s $1 /etc/init.d/$2
#chmod +x /etc/init.d/$2
#service $2 stop
#关闭服务
#ps -ef|grep 'java -jar'|grep $1|awk '{printf $2}'|xargs echo kill pid is
#ps -ef|grep 'java -jar'|grep $1|awk '{printf $2}'|xargs kill -9
cat carWebApp.pid | xargs echo kill pid is
cat carWebApp.pid | xargs kill

ulimit -l unlimited
ulimit -n 65536
#等待服务关闭
sleep 5
#重定向日志
exec >>carWebApp.stdout.log 2>>$LOG_DIR/carWebApp.stderr.log
#启动服务
nohup java -jar -Xms1024m -Xmx1024m -Xss256m $1 --spring.profile.active=default &
#导出进程ID
echo $! > carWebApp.pid



