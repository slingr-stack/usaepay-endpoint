package io.slingr.endpoints.usaepay;

import io.slingr.endpoints.utils.Json;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Helps to match json format with soap format.
 * </p>
 * Created by smoyano on 06/12/17.
 */
public class JsonManipulator {

    private static Map<String, String> listsAttributes = new HashMap<>();

    static {
        listsAttributes.put("paymentMethods", "item");
        listsAttributes.put("customFields", "fieldValue");
        listsAttributes.put("search", "searchParam");
        listsAttributes.put("customers", "customer");
        listsAttributes.put("fields", "string");
        listsAttributes.put("lineItems", "item");
        listsAttributes.put("search", "searchParam");
        listsAttributes.put("transactions", "item");
    }

    public static <T> T convertToEpayObject(Json json, Class<T> type) {
        if (json == null) {
            return null;
        }
        Json jsonCopy = json.cloneJson();
        // manipulate json to match epay format
        jsonCopy.traverse(new Json.Visitor() {
            @Override
            public String convertKey(String key, String path) {
                return StringUtils.uncapitalize(key);
            }

            @Override
            public Object convertValue(String key, Object value, String path) {
                if (value instanceof Json && ((Json) value).isList()) {
                    String listAttr = findListAttribute(key);
                    if (listAttr != null) {
                        return Json.map().set(listAttr, value);
                    }
                }
                return value;
            }

        });
        return Json.stringToObject(jsonCopy.toString(), type);
    }

    public static Json convertToSlingrObject(Object epayObject) {
        if (epayObject == null) {
            return null;
        }
        Json json = Json.parse(Json.objectToString(epayObject));
        // manipulate json to match slingr format
        json.traverse(new Json.Visitor() {

            @Override
            public String convertKey(String key, String path) {
                return StringUtils.capitalize(key);
            }

            @Override
            public Object convertValue(String key, Object value, String path) {
                if (value instanceof Json) {
                    String listAttr = StringUtils.capitalize(findListAttribute(key));
                    if (listAttr != null && ((Json) value).isList(listAttr)) {
                        return ((Json) value).jsons(listAttr);
                    }
                }
                return value;
            }

        });
        return json;
    }

    private static String findListAttribute(String key) {
        for (String attribute : listsAttributes.keySet()) {
            if (attribute.equalsIgnoreCase(key)) {
                return listsAttributes.get(attribute);
            }
        }
        return null;
    }
}
