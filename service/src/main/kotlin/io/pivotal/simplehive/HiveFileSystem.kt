/*
 * Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under
 *
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

package io.pivotal.simplehive

import java.io.File

class HiveFileSystem {
    private val baseDir = createBaseDir()

    val warehouse: String = createDir("warehouse")
    val scratch: String = createDir("scratch")
    val localScratch: String = createDir("localScratch")
    val history: String = createDir("tmp")
    val tezInstallation: String = createDir("tezInstallation")

    private fun createBaseDir() = createDir(File.createTempFile("hive", ""))

    private fun createDir(name: String) = createDir(File(baseDir, name))

    private fun createDir(newDir: File): String {
        if (newDir.exists()) {
            newDir.delete()
        }

        if (!newDir.mkdir())
            throw RuntimeException("couldn't create $newDir")

        if (!newDir.setWritable(true, false))
            throw RuntimeException("couldn't set permissions on $newDir")

        newDir.deleteOnExit()
        return newDir.absolutePath
    }
}
