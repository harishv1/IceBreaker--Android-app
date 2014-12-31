package com.example.faceeventapp;

import java.util.Properties;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.Bucket;

public class Connection {
    public static AmazonSimpleDB awsSimpleDB;
    public static AmazonS3 awsS3;
    public static Properties properties;
    private static BasicAWSCredentials credentials =
            new BasicAWSCredentials("Acess_key", "Secret_key");


    // 1. Get Simple DB connection.
    public static AmazonSimpleDB getAwsSimpleDB() {
        if (awsSimpleDB == null) {
            //BasicAWSCredentials credentials= new BasicAWSCredentials(getProperties().getProperty("accessKey"), Connection.getProperties().getProperty("secreteKey"));
            awsSimpleDB = new AmazonSimpleDBClient(credentials);
        }
        return awsSimpleDB;
    }

    public static AmazonS3 getAwsS3()
    {
        if (awsS3 == null) {
            //BasicAWSCredentials credentials= new BasicAWSCredentials(getProperties().getProperty("accessKey"), Connection.getProperties().getProperty("secreteKey"));
            awsS3 = new AmazonS3Client(credentials);
        }
        return awsS3;

    }

    public static String getPictureBucket()
    {
        return "faceeventapp";
    }

    public static Properties getProperties()
    {
        if(properties==null)
        {
            properties=new Properties();
            try {
                properties.load(StoreToDB.class.getResourceAsStream("credentials.properties"));

            } catch (Exception e) {
                // TODO: handle exception
            }		}
        return properties;
    }
}