package com.fadedbytes.beo.stats;

import java.util.Map;

public record OrbStatResult(String orbName, Map<String, Integer> statMap) {}