package org.zalando.baigan.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.util.function.Supplier;

final class S3ObjectSupplier implements Supplier<String> {
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String key;

    S3ObjectSupplier(final String bucketName, final String key) {
        this(AmazonS3ClientBuilder.defaultClient(), bucketName, key);
    }

    S3ObjectSupplier(final AmazonS3 s3Client, final String bucketName, final String key) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.key = key;
    }

    @Override
    public String get() {
        return s3Client.getObjectAsString(bucketName, key);
    }
}
