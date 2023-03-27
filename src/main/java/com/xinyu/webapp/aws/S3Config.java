package com.xinyu.webapp.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
//    private final String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
//    private final String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
//
//    public AWSCredentials credentials() {
//        AWSCredentials credentials = new BasicAWSCredentials(
//                accessKey,
//                secretKey
//        );
//        return credentials;
//    }

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .withRegion(Regions.US_WEST_2)
                .build();
        return s3Client;
    }

}
