package org.zalando.baigan.s3;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class S3ObjectSupplierTest {

    @Test
    void downloadsObjectAsString() {
        final AmazonS3 client = mock(AmazonS3.class);
        when(client.getObjectAsString("bucket", "key")).thenReturn("42");
        final S3ObjectSupplier unit = new S3ObjectSupplier(client, "bucket", "key");
        assertEquals("42", unit.get());
    }
}