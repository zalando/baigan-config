package org.zalando.baigan.s3;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import java.util.Base64;
import java.util.function.Supplier;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;

final class DecryptingS3ObjectSupplier implements Supplier<String> {

    private static final String KMS_START_TAG = "aws:kms:";
    private static final Base64.Decoder BASE64 = Base64.getDecoder();

    private final AWSKMS kmsClient;
    private final Supplier<String> delegate;

    DecryptingS3ObjectSupplier(final Supplier<String> delegate) {
        this(AWSKMSClientBuilder.defaultClient(), delegate);
    }

    DecryptingS3ObjectSupplier(final AWSKMS kmsClient, final Supplier<String> delegate) {
        this.kmsClient = kmsClient;
        this.delegate = delegate;
    }

    @Override
    public String get() {
        final String delegateValue = delegate.get();
        if (delegateValue.startsWith(KMS_START_TAG)) {
            return decrypt(delegateValue.substring(KMS_START_TAG.length()));
        }
        return delegateValue;
    }

    private String decrypt(final String cipherText) {
        final byte[] cipherBytes = BASE64.decode(cipherText);
        final byte[] plainBytes = decrypt(cipherBytes);
        return new String(plainBytes, UTF_8);
    }

    // TODO: apparently this one is flaky and requires retries
    private byte[] decrypt(final byte[] cipherBytes) {
        final DecryptRequest request = new DecryptRequest().withCiphertextBlob(wrap(cipherBytes));
        final DecryptResult result = kmsClient.decrypt(request);
        return result.getPlaintext().array();
    }

}
