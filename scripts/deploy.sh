#!/usr/bin/env bash
# указываем что это баш скрипт

# запускаем мавен
mvn clean package

# для копирывание файлов на сервер
echo 'Copy files...'

# для этого используем ssh ключ -- указываем что мы копируем -- куда кладем
scp -i ~/.ssh/id_rsa_drucoder \
    target/sweater-1.0-SNAPSHOT.jar \
    dru@192.168.0.107:/home/dru/

# перезапускаем сервер
echo 'Restart server...'

# останавливаем сервер запускаем приложение и убираем консоль из виду - все логи грубо скидываем в текстовый документ
ssh -i ~/.ssh/id_rsa_drucoder dru@192.168.0.107 << EOF
pgrep java | xargs kill -9
nohup java -jar sweater-1.0-SNAPSHOT.jar > log.txt &
EOF

# прощаемся с пользоваелем
echo 'Bye'