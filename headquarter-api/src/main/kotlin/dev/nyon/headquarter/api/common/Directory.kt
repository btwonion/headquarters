package dev.nyon.headquarter.api.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class Directory(val path: String) {

    val file = File(path)

    fun getSubDirectory(file: String, force: Boolean = false): Directory? {
        return if (force) subDirectory(file)
        else {
            if (!File("$path/$file/").exists()) return null
            Directory("$path/$file/")
        }
    }

    fun createFile(name: String): File {
        val file = File("$path/$name")
        if (!file.exists()) file.createNewFile()
        return file
    }

    fun subDirectory(file: String): Directory {
        File("$path/$file/").mkdirs()
        return Directory("$path/$file")
    }

    fun files(): Flow<File> {
        return flow {
            for (file in File(path).listFiles()!!) emit(file)
        }
    }

    fun file(name: String, force: Boolean = false): File? {
        return if (force) createFile(name)
        else {
            val file = File("$path/$name")
            if (file.exists()) return file else return null
        }
    }

}