package org.zalando.baigan.s3;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import org.junit.jupiter.api.Test;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static java.nio.ByteBuffer.wrap;

class DecryptingS3ObjectSupplierTest {

    @Test
    void passesThroughUnencryptedValues() {
        final AWSKMS client = mock(AWSKMS.class);
        final DecryptingS3ObjectSupplier unit = new DecryptingS3ObjectSupplier(client, () -> "not-so-secret");
        assertEquals("not-so-secret", unit.get());
        verifyNoMoreInteractions(client);
    }

    @Test
    void decryptsEncodedCipher() {
        final String cipher = "secret";
        final String plaintext = "plaintext";

        final AWSKMS client = buildClient(plaintext, cipher);
        final String encrypted = "aws:kms:" + Base64.getEncoder().encodeToString(cipher.getBytes());

        final DecryptingS3ObjectSupplier unit = new DecryptingS3ObjectSupplier(client, () -> encrypted);
        assertEquals(plaintext, unit.get());
    }

    private AWSKMS buildClient(final String plaintext, final String cipher) {
        final AWSKMS client = mock(AWSKMS.class);
        final DecryptResult result = new DecryptResult();
        result.setPlaintext(wrap(plaintext.getBytes()));
        when(client.decrypt(argThat(argument ->
                argument.equals(new DecryptRequest().withCiphertextBlob(wrap(cipher.getBytes()))))))
                .thenReturn(result);
        return client;
    }
}