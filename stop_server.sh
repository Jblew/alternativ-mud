(
echo open 127.0.0.1 9999
sleep 1
echo "login remoteadmin:rmadminpass"
echo "stop"
) | telnet
