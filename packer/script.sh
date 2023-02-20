#!/bin/bash

sudo yum update
sudo yum upgrade -y

sudo yum install -y tcl
sudo yum install -y expect


sudo mkdir /opt/deployment
sudo mkdir /var/log/app

sudo chown -R $USER:$USER /opt/deployment
sudo chown -R $USER:$USER /var/log/appy

sudo wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
sudo rpm -ivh jdk-17_linux-x64_bin.rpm


sudo yum install -y mariadb-server 
sudo systemctl start mariadb
sudo systemctl enable mariadb