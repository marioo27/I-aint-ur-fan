package com.iainturfan;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonManager {

    private final ObjectMapper mapper;

    public JsonManager(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Set<String> parseFollowers(InputStream input) {
        JsonNode root = readTree(input, "followers");

        if (!root.isArray()) {
            throw new AnalysisException(
                    "El archivo followers no tiene un formato válido",
                    "INVALID_STRUCTURE"
            );
        }

        Set<String> usernames = new HashSet<>();
        for (JsonNode node : root) {
            addUsernameFromStringList(node, usernames);
        }

        if (usernames.isEmpty()) {
            throw new AnalysisException(
                    "El archivo followers no contiene usuarios",
                    "NO_USERS"
            );
        }

        return usernames;
    }

    public Set<String> parseFollowing(InputStream input) {
        JsonNode root = readTree(input, "following");
        JsonNode relationships = root.get("relationships_following");

        if (relationships == null || !relationships.isArray()) {
            throw new AnalysisException(
                    "El archivo following no tiene un formato válido",
                    "INVALID_STRUCTURE"
            );
        }

        Set<String> usernames = new HashSet<>();
        for (JsonNode node : relationships) {
            JsonNode titleNode = node.get("title");
            if (titleNode != null && !titleNode.isNull()) {
                String username = titleNode.asText();
                if (username != null && !username.isBlank()) {
                    usernames.add(username.trim());
                    continue;
                }
            }
            addUsernameFromStringList(node, usernames);
        }

        if (usernames.isEmpty()) {
            throw new AnalysisException(
                    "El archivo following no contiene usuarios",
                    "NO_USERS"
            );
        }

        return usernames;
    }

    private void addUsernameFromStringList(JsonNode node, Set<String> usernames) {
        JsonNode stringList = node.get("string_list_data");
        if (stringList == null || !stringList.isArray() || stringList.isEmpty()) {
            return;
        }

        JsonNode valueNode = stringList.get(0).get("value");
        if (valueNode == null || valueNode.isNull()) {
            return;
        }

        String username = valueNode.asText();
        if (username != null && !username.isBlank()) {
            usernames.add(username.trim());
        }
    }

    private JsonNode readTree(InputStream input, String fieldName) {
        try {
            JsonNode root = mapper.readTree(input);
            if (root == null || root.isNull() || root.isMissingNode()) {
                throw new AnalysisException(
                        "El archivo " + fieldName + " no tiene un formato válido",
                        "INVALID_JSON"
                );
            }
            return root;
        } catch (JsonProcessingException e) {
            throw new AnalysisException(
                    "El archivo " + fieldName + " no tiene un formato válido",
                    "INVALID_JSON"
            );
        } catch (IOException e) {
            throw new AnalysisException(
                    "No se ha podido leer el archivo " + fieldName,
                    "INVALID_JSON"
            );
        }
    }
}
