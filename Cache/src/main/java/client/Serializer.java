/**
 * 
 */
package client;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author ArunIyengar
 *
 */
public class Serializer {

    // Serialize a single object to a byte array
    public static <T> byte[] serializeToByteArray(T r) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(r);
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            System.out.println("Exception in Serializer.serializeToByteArray  " + ",  " + ex.getMessage() + " "
                    + ex.getStackTrace());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return bytes;
    }

    // deserialize a single object from a byte array
    public static <T> T deserializeFromByteArray(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        T r = null;
        try {
            in = new ObjectInputStream(bis);
            r = (T) in.readObject();
        } catch (IOException i) {
            System.out.println("Exception in Serializer.deserializeFromByteArray  " + ",  " + i.getMessage());
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out
                    .println("Serializer.deserializeFromByteArray: class not found");
            c.printStackTrace();
            return null;
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return r;
    }

    
}
