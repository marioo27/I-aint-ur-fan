package com.iainturfan;

import java.util.Set;

public record AnalysisResult(
        Set<String> notFollowingBack,
        Set<String> youDoNotFollowBack,
        Set<String> mutualFollowers,
        Set<String> followers,
        Set<String> following,
        int followersCount,
        int followingCount,
        int notFollowingBackCount,
        int youDoNotFollowBackCount,
        int mutualFollowersCount
) {
}
