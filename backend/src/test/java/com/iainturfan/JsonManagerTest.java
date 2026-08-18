package com.iainturfan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class JsonManagerTest {

    private JsonManager jsonManager;

    @BeforeEach
    void setUp() {
        jsonManager = new JsonManager(new ObjectMapper());
    }

    @Test
    void parseFollowersReadsValidJson() throws Exception {
        Set<String> usernames = jsonManager.parseFollowers(fixture("followers-valid.json"));

        assertThat(usernames).containsExactlyInAnyOrder("alice", "bob");
    }

    @Test
    void parseFollowingReadsValidJson() throws Exception {
        Set<String> usernames = jsonManager.parseFollowing(fixture("following-valid.json"));

        assertThat(usernames).containsExactlyInAnyOrder("alice", "charlie");
    }

    @Test
    void parseFollowersRejectsInvalidJson() {
        assertThatThrownBy(() -> jsonManager.parseFollowers(fixture("followers-invalid.json")))
                .isInstanceOf(AnalysisException.class)
                .hasMessage("El archivo followers no tiene un formato válido")
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("INVALID_JSON");
    }

    @Test
    void parseFollowersDetectsMissingUsers() {
        assertThatThrownBy(() -> jsonManager.parseFollowers(fixture("followers-no-users.json")))
                .isInstanceOf(AnalysisException.class)
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("NO_USERS");
    }

    @Test
    void parseFollowingDetectsMissingUsers() {
        assertThatThrownBy(() -> jsonManager.parseFollowing(fixture("following-no-users.json")))
                .isInstanceOf(AnalysisException.class)
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("NO_USERS");
    }

    @Test
    void parseFollowersRejectsIncompatibleStructure() {
        assertThatThrownBy(() -> jsonManager.parseFollowers(fixture("followers-wrong-structure.json")))
                .isInstanceOf(AnalysisException.class)
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("INVALID_STRUCTURE");
    }

    @Test
    void parseFollowingRejectsIncompatibleStructure() {
        assertThatThrownBy(() -> jsonManager.parseFollowing(fixture("following-wrong-structure.json")))
                .isInstanceOf(AnalysisException.class)
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("INVALID_STRUCTURE");
    }

    @Test
    void parseFollowersRemovesDuplicates() throws Exception {
        Set<String> usernames = jsonManager.parseFollowers(fixture("followers-duplicates.json"));

        assertThat(usernames).containsExactlyInAnyOrder("alice", "bob");
        assertThat(usernames).hasSize(2);
    }

    @Test
    void parseFollowingRemovesDuplicates() throws Exception {
        Set<String> usernames = jsonManager.parseFollowing(fixture("following-duplicates.json"));

        assertThat(usernames).containsExactlyInAnyOrder("alice", "charlie");
        assertThat(usernames).hasSize(2);
    }

    private InputStream fixture(String name) {
        InputStream input = getClass().getResourceAsStream("/fixtures/" + name);
        assertThat(input).as("fixture %s must exist", name).isNotNull();
        return input;
    }
}
