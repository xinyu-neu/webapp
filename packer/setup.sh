#!/bin/bash

sudo yum update
sudo yum upgrade -y

sudo yum install -y tcl
sudo yum install -y expect

sudo mkdir /opt/deployment
sudo mkdir /var/log/app
sudo mkdir /var/logs

sudo chown -R $USER:$USER /opt/deployment
sudo chown -R $USER:$USER /var/log/app
sudo chown -R $USER:$USER /var/logs
sudo chown -R $USER:$USER /opt/aws/amazon-cloudwatch-agent/etc

sudo wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
sudo rpm -ivh jdk-17_linux-x64_bin.rpm

#sudo yum install -y mariadb-server
#sudo systemctl start mariadb
#sudo systemctl enable mariadb

sudo wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
sudo rpm -U ./amazon-cloudwatch-agent.rpm

sudo tee /opt/aws/amazon-cloudwatch-agent/etc/cloudwatch-config.json > /dev/null <<EOT
{
  "agent": {
      "metrics_collection_interval": 10,
      "logfile": "/var/logs/amazon-cloudwatch-agent.log"
  },
  "logs": {
      "logs_collected": {
          "files": {
              "collect_list": [
                  {
                      "file_path": "/opt/deployment/csye6225.log",
                      "log_group_name": "csye6225",
                      "log_stream_name": "webapp"
                  }
              ]
          }
      },
      "log_stream_name": "cloudwatch_log_stream"
  },
  "metrics":{
    "metrics_collected":{
       "statsd":{
          "service_address":":8125",
          "metrics_collection_interval":15,
          "metrics_aggregation_interval":300
       }
    }
 }
}
EOT