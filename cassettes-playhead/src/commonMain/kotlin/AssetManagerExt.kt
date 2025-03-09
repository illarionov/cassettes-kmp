/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import at.released.cassettes.base.AssetUrl
import kotlinx.io.RawSource
import kotlinx.io.buffered
import kotlinx.io.readByteArray

public fun <R : Any> AssetManager.readOrThrow(
    url: AssetUrl,
    transform: (RawSource, String) -> Result<R>,
): R {
    val candidates: List<AssetStorage.Factory> = getStorageCandidates(url)
    val failedPaths: MutableList<Pair<String, Throwable>> = mutableListOf()
    for (sourceFactory in candidates) {
        val assetStorage: AssetStorage = sourceFactory()
        val result: Result<R> = tryReadCandidate(assetStorage, transform)
        result.onSuccess {
            return it
        }.onFailure {
            failedPaths.add(assetStorage.path to it)
        }
    }
    if (failedPaths.isEmpty()) {
        throw PlayheadIoException("Could not determine the full path to `$url`")
    } else {
        val (firstPath, firstError) = failedPaths.first()
        throw PlayheadIoException(
            "Could not determine the full path to `$url`. " +
                    "Error when reading a file `$firstPath`: $firstError",
            failedPaths,
        )
    }
}

private fun <R : Any> tryReadCandidate(
    candidate: AssetStorage,
    transform: (RawSource, String) -> Result<R>,
): Result<R> {
    val source = try {
        candidate.open()
    } catch (@Suppress("TooGenericExceptionCaught") ex: Throwable) {
        return Result.failure(PlayheadException("Can not create source `${candidate.path}`", ex))
    }

    return source.use {
        transform(it, candidate.path)
    }
}

public fun AssetManager.readBytesOrThrow(url: AssetUrl): ByteArray = readOrThrow(url) { source, _ ->
    runCatching {
        source.buffered().readByteArray()
    }
}

public open class PlayheadException(
    message: String?,
    throwable: Throwable? = null,
) : RuntimeException(message, throwable)

public class PlayheadIoException(
    message: String?,
    paths: List<Pair<String, Throwable>> = emptyList(),
) : PlayheadException(
    message,
    paths.lastOrNull()?.second,
)
