/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.rapidoid.cache.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.concurrent.atomic.AtomicLong;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class CacheStats extends RapidoidThing {

    private final com.github.benmanes.caffeine.cache.stats.CacheStats stats;

    private final AtomicLong bypassed;

    public CacheStats(com.github.benmanes.caffeine.cache.stats.CacheStats stats, AtomicLong bypassed) {
        this.stats = stats;
        this.bypassed = bypassed;
    }

    public long requests() {
        return stats.requestCount();
    }

    public long hits() {
        return stats.hitCount();
    }

    public double hitRate() {
        return stats.hitRate();
    }

    public long misses() {
        return stats.missCount();
    }

    public double missRate() {
        return stats.missRate();
    }

    public long loads() {
        return stats.loadCount();
    }

    public long errors() {
        return stats.loadFailureCount();
    }

    public double errorRate() {
        return stats.loadFailureRate();
    }

    public long totalLoadTime() {
        return stats.totalLoadTime();
    }

    public double averageLoadPenalty() {
        return stats.averageLoadPenalty();
    }

    public long evictions() {
        return stats.evictionCount();
    }

    public long bypassed() {
        return bypassed.get();
    }

    @Override
    public String toString() {
        return "CacheStats{" +
                "requests=" + requests() +
                ", hits=" + hits() +
                ", hitRate=" + hitRate() +
                ", misses=" + misses() +
                ", missRate=" + missRate() +
                ", loads=" + loads() +
                ", errors=" + errors() +
                ", errorRate=" + errorRate() +
                ", evictions=" + evictions() +
                ", bypassed=" + bypassed() +
                ", totalLoadTime=" + totalLoadTime() +
                ", averageLoadPenalty=" + averageLoadPenalty() +
                '}';
    }
}
