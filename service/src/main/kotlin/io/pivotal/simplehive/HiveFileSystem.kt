package io.pivotal.simplehive

import java.io.File

class HiveFileSystem {
    val baseDir = createBaseDir()

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
