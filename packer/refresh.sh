#!/bin/bash

ami_id=$(aws ec2 describe-images --owners 630049607588 --query 'reverse(sort_by(Images, &CreationDate))[0].ImageId' --output text)

lt_version=$(aws ec2 describe-launch-template-versions --launch-template-name csye6225-lt --query 'reverse(sort_by(LaunchTemplateVersions, &VersionNumber))[0].VersionNumber')

aws ec2 create-launch-template-version --launch-template-name csye6225-lt --source-version "$lt_version" --launch-template-data '{"ImageId":"'"$ami_id"'"}'

aws autoscaling start-instance-refresh --auto-scaling-group-name csye6225-asg