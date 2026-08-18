package com.iainturfan;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyzerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthReturnsOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void analyzeReturnsComparison() throws Exception {
        mockMvc.perform(multipart("/api/analyzer/analyze")
                        .file(fixtureFile("followers", "followers-valid.json"))
                        .file(fixtureFile("following", "following-valid.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followersCount").value(2))
                .andExpect(jsonPath("$.followingCount").value(2))
                .andExpect(jsonPath("$.notFollowingBackCount").value(1))
                .andExpect(jsonPath("$.youDoNotFollowBackCount").value(1))
                .andExpect(jsonPath("$.notFollowingBack[0]").value("charlie"))
                .andExpect(jsonPath("$.youDoNotFollowBack[0]").value("bob"));
    }

    @Test
    void analyzeRejectsMissingFollowingFile() throws Exception {
        mockMvc.perform(multipart("/api/analyzer/analyze")
                        .file(fixtureFile("followers", "followers-valid.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_FILE"))
                .andExpect(jsonPath("$.message").value("Falta el archivo following"));
    }

    @Test
    void analyzeRejectsInvalidJson() throws Exception {
        mockMvc.perform(multipart("/api/analyzer/analyze")
                        .file(fixtureFile("followers", "followers-invalid.json"))
                        .file(fixtureFile("following", "following-valid.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_JSON"))
                .andExpect(jsonPath("$.message").value("El archivo followers no tiene un formato válido"));
    }

    @Test
    void analyzeRejectsFileWithoutUsers() throws Exception {
        mockMvc.perform(multipart("/api/analyzer/analyze")
                        .file(fixtureFile("followers", "followers-no-users.json"))
                        .file(fixtureFile("following", "following-valid.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NO_USERS"));
    }

    @Test
    void analyzeRejectsEmptyFile() throws Exception {
        MockMultipartFile emptyFollowers = new MockMultipartFile(
                "followers",
                "followers.json",
                "application/json",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/analyzer/analyze")
                        .file(emptyFollowers)
                        .file(fixtureFile("following", "following-valid.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("EMPTY_FILE"));
    }

    private MockMultipartFile fixtureFile(String fieldName, String fixtureName) throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/fixtures/" + fixtureName)) {
            byte[] content = input == null
                    ? "{}".getBytes(StandardCharsets.UTF_8)
                    : input.readAllBytes();
            return new MockMultipartFile(fieldName, fixtureName, "application/json", content);
        }
    }
}
