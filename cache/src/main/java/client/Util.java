/**
 *
 */
package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * @author ArunIyengar
 */
/*
 * Utility methods called by several other classes
 */
public class Util {

    /**
     * compress a serializable object using gzip
     *
     * @param object object which implements Serializable
     * @return byte array containing compressed objects
     */
    public static byte[] compress(Serializable object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOut = null;
            ObjectOutputStream objectOut = null;
            try {
                gzipOut = new GZIPOutputStream(baos);
                objectOut = new ObjectOutputStream(gzipOut);
                objectOut.writeObject(object);
            } catch (Exception e) {
                describeException(e, "Exception in Util.compress");
            } finally {
                objectOut.close();
                gzipOut.close();
            }
        } catch (Exception e) {
            describeException(e, "Exception in Util.compress");
        }
        return baos.toByteArray();
    }

    /**
     * Decompress a compressed object
     *
     * @param bytes byte array corresponding to compressed object
     * @return decompressed object
     */
    public static <T> T decompress(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        T object = null;
        try {
            GZIPInputStream gzipIn = null;
            ObjectInputStream objectIn = null;
            try {
                gzipIn = new GZIPInputStream(bais);
                objectIn = new ObjectInputStream(gzipIn);
                object = (T) objectIn.readObject();
            } catch (Exception e) {
                describeException(e, "Exception in Util.decompress");
            } finally {
                objectIn.close();
                gzipIn.close();
            }
        } catch (Exception e) {
            describeException(e, "Exception in Util.decompress");
        }
        return object;
    }


    /**
     * Output information about an exception
     *
     * @param e       The exception
     * @param message Message to output
     */
    public static void describeException(Exception e, String message) {
        System.out.println(message);
        System.out.println(e.getMessage());
        e.printStackTrace();
    }

    /**
     * Return the current time
     *
     * @return Milliseconds since January 1, 1970
     */
    public static long getTime() {
        return System.currentTimeMillis();
        // return (new Date()).getTime();
        //java.util.Date.getTime() method returns how many milliseconds have passed since 
        //January 1, 1970, 00:00:00 GMT
    }
}
