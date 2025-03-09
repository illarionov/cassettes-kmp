/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.base

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

public interface AssetUrl {
    public val url: String

    private class DefaultAssetUrl(
        override val url: String,
    ) : AssetUrl {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || this::class != other::class) {
                return false
            }

            other as AssetUrl

            return url == other.url
        }

        override fun hashCode(): Int = url.hashCode()

        override fun toString(): String = "AssetUrl('$url')"
    }

    public companion object {
        @JvmStatic
        @JvmName("create")
        public operator fun invoke(url: String): AssetUrl = DefaultAssetUrl(url)
    }
}
