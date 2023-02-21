#!/bin/bash

sudo chown -R jvmapps:appmgr /opt/deployment
sudo systemctl daemon-reload
sudo systemctl start myapp.service