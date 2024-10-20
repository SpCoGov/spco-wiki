package top.spco.spcobot.wiki.core.util;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.function.Supplier;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class TOTPUtil {
    private static final Base32 base32 = new Base32();

    public static Supplier<String> getOtpSupplier(String key) {
        return () -> getOTP(key);
    }

    public static String getOTP(String key) {
        byte[] bytes = base32.decode(key);
        String hexKey = Hex.encodeHexString(bytes);
        return getOTP(getStep(), hexKey);
    }

    private static long getStep() {
        // 30 seconds StepSize (ID TOTP)
        return System.currentTimeMillis() / 30000;
    }

    private static String getOTP(final long step, final String key) {
        StringBuilder steps = new StringBuilder(Long.toHexString(step).toUpperCase());
        while (steps.length() < 16) {
            steps.insert(0, "0");
        }

        // Get the HEX in a Byte[]
        final byte[] msg = hexStr2Bytes(steps.toString());
        final byte[] k = hexStr2Bytes(key);

        final byte[] hash = hmac_sha1(k, msg);

        // put selected bytes into result int
        final int offset = hash[hash.length - 1] & 0xf;
        final int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        final int otp = binary % 1000000;

        StringBuilder result = new StringBuilder(Integer.toString(otp));
        while (result.length() < 6) {
            result.insert(0, "0");
        }
        return result.toString();
    }


    private static byte[] hexStr2Bytes(final String hex) {
        final byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        final byte[] ret = new byte[bArray.length - 1];

        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private static byte[] hmac_sha1(final byte[] keyBytes, final byte[] text) {
        try {
            final Mac hmac = Mac.getInstance("HmacSHA1");
            final SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (final GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }
}
