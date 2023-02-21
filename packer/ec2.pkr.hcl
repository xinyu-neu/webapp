variable "aws_region" {
  type    = string
  default = "us-west-2"
}

variable "source_ami" {
  type    = string
  default = "ami-0f1a5f5ada0e7da53"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "subnet_id" {
  type    = string
  default = "subnet-01dffc76ab67b206d"
}

variable "aws_access_key_id" {
  type    = string
  default = "AKIAZFMPCB6SLLLTUPHC"
}

variable "aws_secret_access_key" {
  type    = string
  default = "hmOErBsGBqikPV5qZM+o9TaPfIBuP1dJSXSY41pk"
}

source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  ami_users       = ["630049607588", "619941072621"]
  access_key      = var.aws_access_key_id
  secret_key      = var.aws_secret_access_key


  instance_type = "t2.micro"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"

}

build {
  sources = ["source.amazon-ebs.my-ami"]

  provisioner "shell" {
    script = "setup.sh"
  }

  provisioner "shell" {
    script = "expect.sh"
  }

  provisioner "file" {
    source      = "../target/webapp-0.0.1-SNAPSHOT.jar"
    destination = "/opt/deployment/app.jar"
  }

  provisioner "shell" {
    script = "service.sh"
  }

  post-processor "manifest" {
    output     = "manifest.json"
    strip_path = true
  }
}

