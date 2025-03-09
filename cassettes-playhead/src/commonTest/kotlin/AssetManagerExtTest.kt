/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import assertk.assertThat
import assertk.assertions.isEqualTo
import at.released.cassettes.base.AssetUrl
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.writeString
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AssetManagerExtTest {
    @Test
    fun readOrThrow_should_read_source() {
        val am = AssetManager { _: AssetUrl ->
            listOf(
                createFailureWasmBinarySource("/opt/path.txt"),
                createSuccessWasmBinarySource("/usr/data/path.txt"),
            )
        }

        val data = am.readBytesOrThrow(AssetUrl("path.txt")).decodeToString()
        assertThat(data).isEqualTo("Success")
    }

    @Test
    fun readOrThrow_should_throw_exception_on_failure() {
        val am = AssetManager { _: AssetUrl ->
            listOf(
                createFailureWasmBinarySource("/opt/path.txt"),
                createFailureWasmBinarySource("/usr/data/path.txt"),
            )
        }

        assertFailsWith<PlayheadIoException> {
            am.readBytesOrThrow(AssetUrl("path.txt"))
        }
    }

    @Test
    fun readOrThrow_should_throw_exception_if_source_list_is_empty() {
        val am = AssetManager { _: AssetUrl -> emptyList() }

        assertFailsWith<PlayheadIoException> {
            am.readBytesOrThrow(AssetUrl("path.txt"))
        }
    }

    @Test
    fun readOrThrow_should_not_throw_if_create_source_throws_exception() {
        val am = AssetManager { _: AssetUrl ->
            listOf(
                object : AssetStorage {
                    override val path: String = "nonexistent"
                    override fun open(): RawSource = throw FileNotFoundException("file not found")
                },
                createSuccessWasmBinarySource("/usr/data/path.txt"),
            )
        }

        val dataBytes = am.readBytesOrThrow(AssetUrl("path.txt"))
        val dataString = dataBytes.decodeToString()
        assertThat(dataString).isEqualTo("Success")
    }

    @Test
    fun readOrThrow_should_throw_if_transformer_throws_exception() {
        class UnexpectedException : RuntimeException()

        val am = AssetManager { _: AssetUrl ->
            listOf(createSuccessWasmBinarySource("/usr/data/path.txt"))
        }

        assertFailsWith<UnexpectedException> {
            am.readOrThrow(AssetUrl("path.txt")) { _: RawSource, _: String ->
                throw UnexpectedException()
            }
        }
    }

    companion object {
        fun createSuccessWasmBinarySource(
            path: String,
            content: Buffer = createSuccessContent(),
        ) = object : AssetStorage {
            override val path: String = path
            override fun open(): RawSource = content
        }

        private fun createSuccessContent(): Buffer = Buffer().apply {
            writeString("Success")
        }

        fun createFailureWasmBinarySource(
            path: String,
            createSource: () -> Nothing = { throw FileNotFoundException("$path not found") },
        ) = object : AssetStorage {
            override val path: String = path
            override fun open(): RawSource = createFailureRawSource(createSource)
        }

        private fun createFailureRawSource(
            failureFactory: () -> Nothing = { throw FileNotFoundException("file not found") },
        ): RawSource = object : RawSource {
            override fun close() = Unit
            override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
                failureFactory()
            }
        }
    }
}
