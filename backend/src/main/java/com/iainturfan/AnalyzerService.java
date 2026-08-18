package com.iainturfan;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnalyzerService {

    static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;

    private final JsonManager jsonManager;

    public AnalyzerService(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    public AnalysisResult analyze(MultipartFile followers, MultipartFile following) throws IOException {
        validateFile(followers, "followers");
        validateFile(following, "following");

        Set<String> followerUsernames = jsonManager.parseFollowers(followers.getInputStream());
        Set<String> followingUsernames = jsonManager.parseFollowing(following.getInputStream());

        return compare(followerUsernames, followingUsernames);
    }

    AnalysisResult compare(Set<String> followers, Set<String> following) {
        Set<String> notFollowingBack = new TreeSet<>(following);
        notFollowingBack.removeAll(followers);

        Set<String> youDoNotFollowBack = new TreeSet<>(followers);
        youDoNotFollowBack.removeAll(following);

        Set<String> mutualFollowers = new TreeSet<>(followers);
        mutualFollowers.retainAll(following);

        return new AnalysisResult(
                notFollowingBack,
                youDoNotFollowBack,
                mutualFollowers,
                new TreeSet<>(followers),
                new TreeSet<>(following),
                followers.size(),
                following.size(),
                notFollowingBack.size(),
                youDoNotFollowBack.size(),
                mutualFollowers.size()
        );
    }

    void validateFile(MultipartFile file, String fieldName) {
        if (file == null || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new AnalysisException(
                    "Falta el archivo " + fieldName,
                    "MISSING_FILE"
            );
        }

        if (file.isEmpty()) {
            throw new AnalysisException(
                    "El archivo " + fieldName + " está vacío",
                    "EMPTY_FILE"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new AnalysisException(
                    "El archivo " + fieldName + " supera el tamaño máximo de 10 MB",
                    "FILE_TOO_LARGE"
            );
        }

        String filename = file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (!filename.endsWith(".json")) {
            throw new AnalysisException(
                    "El archivo " + fieldName + " debe ser un JSON",
                    "NOT_JSON"
            );
        }
    }
}
