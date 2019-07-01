package org.zalando.baigan.s3;

import java.util.function.Supplier;

public final class S3Stores {

    private S3Stores() {
        // utility class
    }

    public static Supplier<String> s3(final String bucketName, final String key) {
        return new DecryptingS3ObjectSupplier(new S3ObjectSupplier(bucketName, key));
    }
}
