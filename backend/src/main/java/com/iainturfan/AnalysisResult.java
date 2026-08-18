package com.iainturfan;

import java.util.Set;

public record AnalysisResult(
        Set<String> notFollowingBack,
        Set<String> youDoNotFollowBack,
        int followersCount,
        int followingCount,
        int notFollowingBackCount,
        int youDoNotFollowBackCount
) {
}
