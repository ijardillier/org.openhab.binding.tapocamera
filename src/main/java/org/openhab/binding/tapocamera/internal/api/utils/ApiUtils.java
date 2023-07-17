/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *  SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openhab.binding.tapocamera.internal.api.ApiMethodTypes;

/**
 * The type Api utils.
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
     * @param method       the method
     * @param moduleName   the module name
     * @param sectionsName the sections name
     * @return the string
     */
    public static String createSingleCommand(String method, String moduleName, List<String> sectionsName ) {
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
     * @param method      the method
     * @param moduleName  the module name
     * @param sectionName the section name
     * @param paramName   the param name
     * @param value       the value
     * @return the string
     */
    public static String createSingleCommand(String method, String moduleName, String sectionName, String paramName, Object value ) {
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

    public static String createSingleCommandAsParam(String method, String moduleName, String sectionName, String paramName, Object value ) {
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
}
