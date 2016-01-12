/*
 * Copyright (c) 2016 IBM Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.client.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author ArunIyengar
 */
public class Serializer {

    // Serialize a single object to a byte array
    public static <T> byte[] serializeToByteArray(T r) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(r);
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            new Exception("Exception in Serializer.serializeToByteArray", ex).printStackTrace();
        }
        return bytes;
    }

    // deserialize a single object from a byte array
    public static <T> T deserializeFromByteArray(byte[] bytes) {

        T r = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            r = (T) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            new Exception("Exception in Serializer.deserializeToByteArray", ex).printStackTrace();
        }
        return r;
    }


}
