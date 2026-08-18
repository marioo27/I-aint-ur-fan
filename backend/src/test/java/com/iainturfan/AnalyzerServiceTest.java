package com.iainturfan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

class AnalyzerServiceTest {

    private AnalyzerService analyzerService;

    @BeforeEach
    void setUp() {
        analyzerService = new AnalyzerService(new JsonManager(new ObjectMapper()));
    }

    @Test
    void compareFollowersAndFollowing() {
        AnalysisResult result = analyzerService.compare(
                Set.of("alice", "bob"),
                Set.of("alice", "charlie")
        );

        assertThat(result.notFollowingBack()).containsExactly("charlie");
        assertThat(result.youDoNotFollowBack()).containsExactly("bob");
        assertThat(result.followersCount()).isEqualTo(2);
        assertThat(result.followingCount()).isEqualTo(2);
        assertThat(result.notFollowingBackCount()).isEqualTo(1);
        assertThat(result.youDoNotFollowBackCount()).isEqualTo(1);
    }

    @Test
    void compareRemovesDuplicatesThroughSets() {
        AnalysisResult result = analyzerService.compare(
                Set.of("alice", "bob"),
                Set.of("alice", "bob")
        );

        assertThat(result.notFollowingBack()).isEmpty();
        assertThat(result.youDoNotFollowBack()).isEmpty();
        assertThat(result.followersCount()).isEqualTo(2);
        assertThat(result.followingCount()).isEqualTo(2);
        assertThat(result.notFollowingBackCount()).isEqualTo(0);
        assertThat(result.youDoNotFollowBackCount()).isEqualTo(0);
    }

    @Test
    void compareCalculatesCounters() {
        AnalysisResult result = analyzerService.compare(
                Set.of("a", "b", "c"),
                Set.of("c", "d")
        );

        assertThat(result.followersCount()).isEqualTo(3);
        assertThat(result.followingCount()).isEqualTo(2);
        assertThat(result.notFollowingBackCount()).isEqualTo(1);
        assertThat(result.youDoNotFollowBackCount()).isEqualTo(2);
    }

    @Test
    void compareHandlesEmptyLists() {
        AnalysisResult result = analyzerService.compare(Collections.emptySet(), Collections.emptySet());

        assertThat(result.notFollowingBack()).isEmpty();
        assertThat(result.youDoNotFollowBack()).isEmpty();
        assertThat(result.followersCount()).isEqualTo(0);
        assertThat(result.followingCount()).isEqualTo(0);
        assertThat(result.notFollowingBackCount()).isEqualTo(0);
        assertThat(result.youDoNotFollowBackCount()).isEqualTo(0);
    }

    @Test
    void validateFileRejectsMissingFile() {
        assertThatThrownBy(() -> analyzerService.validateFile(null, "followers"))
                .isInstanceOf(AnalysisException.class)
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("MISSING_FILE");
    }

    @Test
    void validateFileRejectsEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "followers",
                "followers.json",
                "application/json",
                new byte[0]
        );

        assertThatThrownBy(() -> analyzerService.validateFile(emptyFile, "followers"))
                .isInstanceOf(AnalysisException.class)
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("EMPTY_FILE");
    }

    @Test
    void validateFileRejectsNonJsonFile() {
        MockMultipartFile textFile = new MockMultipartFile(
                "followers",
                "followers.txt",
                "text/plain",
                "{\"ok\":true}".getBytes()
        );

        assertThatThrownBy(() -> analyzerService.validateFile(textFile, "followers"))
                .isInstanceOf(AnalysisException.class)
                .extracting(exception -> ((AnalysisException) exception).getCode())
                .isEqualTo("NOT_JSON");
    }
}
