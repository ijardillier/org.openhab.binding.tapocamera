package org.openhab.binding.tapocamera.internal.api.v2.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.openhab.binding.tapocamera.internal.api.v2.TypeMethodResponse;

public class ApiUtils {
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

    public static String createMultipleCommand(List<TypeMethodResponse> methods) {
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

    public static String createSingleCommand(String method, String moduleName, List<String> sectionsName ) {
        JsonObject json = new JsonObject();
        json.addProperty("method", method);
        JsonObject section = new JsonObject();
        json.add(moduleName, section);
        Gson gson = new Gson();
        section.add("name", gson.toJsonTree(sectionsName).getAsJsonArray());
        return json.toString();
    }
}
