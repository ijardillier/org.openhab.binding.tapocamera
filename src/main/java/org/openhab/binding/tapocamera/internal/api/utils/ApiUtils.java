/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tapocamera.internal.api.utils;

import java.math.BigInteger;
import java.security.*;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.openhab.binding.tapocamera.internal.api.ApiMethodTypes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The type Api utils.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class ApiUtils {
    /**
     * Gets password hash.
     *
     * @param password the password
     * @return the password hash
     */
    public static String getPasswordHash(String password) {
        String hash = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(password.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hash = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return hash.toUpperCase();
    }

    public static String getPasswordHashSHA256(String password) {
        String hash = "";
        try {
            MessageDigest m = MessageDigest.getInstance("SHA256");
            m.reset();
            m.update(password.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hash = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            // while (hash.length() < 32) {
            // hash = "0" + hash;
            // }
            if (hash.length() % 2 == 1) {
                hash = "0" + hash;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return hash.toUpperCase();
    }

    public static String getPasswordHashSHA256(String password, Integer bytes) {
        String hash = getPasswordHashSHA256(password);
        if (hash.length() > bytes) {
            hash = hash.substring(0, bytes);
        }
        return hash.toUpperCase();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    /**
     * Create multiple command string.
     *
     * @param methods the methods
     * @return the string
     */
    public static String createMultipleCommand(List<ApiMethodTypes> methods) {
        JsonObject json = new JsonObject();
        json.addProperty("method", "multipleRequest");
        JsonObject params = new JsonObject();
        json.add("params", params);
        JsonArray requests = new JsonArray();
        params.add("requests", requests);

        methods.forEach(m -> {
            JsonObject method = new JsonObject();
            method.addProperty("method", m.getMethod());
            JsonObject param = new JsonObject();
            method.add("params", param);
            JsonObject section = new JsonObject();
            if (m.getClazz() != String.class) {
                section.addProperty("name", m.getSection());
            } else {
                section.addProperty(m.getSection(), "null");
            }
            param.add(m.getModule(), section);
            requests.add(method);
        });
        return json.toString();
    }

    /**
     * Create single command string.
     *
     * @param method the method
     * @param moduleName the module name
     * @param sectionsName the sections name
     * @return the string
     */
    public static String createSingleCommand(String method, String moduleName, List<String> sectionsName) {
        JsonObject json = new JsonObject();
        json.addProperty("method", method);
        JsonObject section = new JsonObject();
        json.add(moduleName, section);
        Gson gson = new Gson();
        section.add("name", gson.toJsonTree(sectionsName).getAsJsonArray());
        return json.toString();
    }

    /**
     * Create single command string.
     *
     * @param method the method
     * @param moduleName the module name
     * @param sectionName the section name
     * @param paramName the param name
     * @param value the value
     * @return the string
     */
    public static String createSingleCommand(String method, String moduleName, String sectionName, String paramName,
            Object value) {
        JsonObject json = new JsonObject();
        json.addProperty("method", method);
        JsonObject section = new JsonObject();
        JsonObject param = new JsonObject();
        json.add(moduleName, section);
        section.add(sectionName, param);

        if (value instanceof String) {
            param.addProperty(paramName, (String) value);
        } else if (value instanceof List<?>) {
            Gson gson = new Gson();
            param.add("paramName", gson.toJsonTree(value).getAsJsonArray());
        }
        return json.toString();
    }

    public static String createSingleCommandAsParam(String method, String moduleName, String sectionName,
            String paramName, Object value) {
        JsonObject json = new JsonObject();
        json.addProperty("method", method);
        JsonObject section = new JsonObject();
        JsonObject param = new JsonObject();
        JsonObject params = new JsonObject();
        json.add("params", params);
        params.add(moduleName, section);
        section.add(sectionName, param);

        if (value instanceof String) {
            param.addProperty(paramName, (String) value);
        } else if (value instanceof List<?>) {
            Gson gson = new Gson();
            param.add("paramName", gson.toJsonTree(value).getAsJsonArray());
        }
        return json.toString();
    }

    public static String singleToMulti(String command) {
        JsonObject json = new JsonObject();
        json.addProperty("method", "multipleRequest");
        JsonObject params = new JsonObject();
        json.add("params", params);
        JsonArray requests = new JsonArray();
        params.add("requests", requests);
        requests.add(JsonParser.parseString(command));
        return json.toString();
    }

    public static byte[] stringToByteArray(String s) {
        byte[] byteArray = new byte[s.length() / 2];
        String[] strBytes = new String[s.length() / 2];
        int k = 0;
        for (int i = 0; i < s.length(); i = i + 2) {
            int j = i + 2;
            strBytes[k] = s.substring(i, j);
            byteArray[k] = (byte) Integer.parseInt(strBytes[k], 16);
            k++;
        }
        return byteArray;
    }

    public static String base64encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String base64decode(String str) {
        return new String(Base64.getDecoder().decode(str));
    }

    public static String encryptRequest(String data, String lsk, String ivb) {
        try {
            IvParameterSpec iv = new IvParameterSpec(stringToByteArray(ivb));
            SecretKeySpec skeySpec = new SecretKeySpec(stringToByteArray(lsk), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(data.getBytes());

            return new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptResponse(String response, String lsk, String ivb) {
        try {
            IvParameterSpec iv = new IvParameterSpec(stringToByteArray(ivb));
            SecretKeySpec skeySpec = new SecretKeySpec(stringToByteArray(lsk), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(response));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
