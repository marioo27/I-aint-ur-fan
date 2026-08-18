package com.iainturfan;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AnalyzerController {

    private final AnalyzerService analyzerService;

    public AnalyzerController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping(value = "/analyzer/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalysisResult analyze(
            @RequestParam(value = "followers", required = false) MultipartFile followers,
            @RequestParam(value = "following", required = false) MultipartFile following
    ) throws IOException {
        return analyzerService.analyze(followers, following);
    }
}
