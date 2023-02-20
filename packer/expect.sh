#!/usr/bin/expect

set timeout 10
spawn sudo mysql_secure_installation

expect "Enter password for user root:"
send "\r"

expect "Set root password?"
send "Y\r"

expect "New password:"
send "11959791\r"

expect "Re-enter new password:"
send "11959791\r"

expect "Remove anonymous users?"
send "Y\r"

expect "Disallow root login remotely?"
send "Y\r"

expect "Remove test database and access to it?"
send "Y\r"

expect "Reload privilege tables now?"
send "Y\r"

expect eof
