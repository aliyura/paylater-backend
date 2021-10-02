package com.syrol.paylater.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3BucketStorageService {

    private Logger logger = LoggerFactory.getLogger(S3BucketStorageService.class);

    @Autowired
    private AmazonS3 amazonS3Client;
    @Autowired
    private App app;
    @Autowired
    private Response response;
    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    public APIResponse uploadFile(String fileName, MultipartFile file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucketName, "pictures/"+fileName, file.getInputStream(), metadata);
            app.print(fileName+"File uploaded successfully");
            return response.success(fileName);
        } catch (IOException ioe) {
            app.print("IOException: " + ioe.getMessage());
            return response.failure(ioe.getMessage());
        } catch (AmazonServiceException serviceException) {
            app.print("AmazonServiceException: "+ serviceException.getMessage());
            return response.failure(serviceException.getMessage());
        } catch (AmazonClientException clientException) {
            app.print("AmazonClientException Message: " + clientException.getMessage());
            return response.failure(clientException.getMessage());
        }
    }

    public String deleteFile(final String fileName) {
        amazonS3Client.deleteObject(bucketName, fileName);
        return "Deleted File: " + fileName;
    }


    public ByteArrayOutputStream downloadFile(String keyName) {
        try {
            S3Object s3object = amazonS3Client.getObject(new GetObjectRequest(bucketName, keyName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream;
        } catch (IOException ioException) {
            logger.error("IOException: " + ioException.getMessage());
        } catch (AmazonServiceException serviceException) {
            logger.info("AmazonServiceException Message:    " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            logger.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }

        return null;
    }

    public List<String> listFiles() {

        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(bucketName);

        List<String> keys = new ArrayList<>();

        ObjectListing objects = amazonS3Client.listObjects(listObjectsRequest);

        while (true) {
            List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
            if (objectSummaries.size() < 1) {
                break;
            }

            for (S3ObjectSummary item : objectSummaries) {
                if (!item.getKey().endsWith("/"))
                    keys.add(item.getKey());
            }

            objects = amazonS3Client.listNextBatchOfObjects(objects);
        }

        return keys;
    }

}
