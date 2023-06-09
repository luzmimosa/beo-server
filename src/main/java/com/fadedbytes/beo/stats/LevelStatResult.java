package com.fadedbytes.beo.stats;

import com.fadedbytes.beo.util.key.NamespacedKey;

public record LevelStatResult(NamespacedKey levelKey, OrbStatResult[] orbStats, long time) {}