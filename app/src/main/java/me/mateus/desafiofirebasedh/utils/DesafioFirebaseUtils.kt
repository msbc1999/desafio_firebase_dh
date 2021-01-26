package me.mateus.desafiofirebasedh.utils

import java.io.FileDescriptor
import java.io.FileInputStream
import java.security.MessageDigest

class DesafioFirebaseUtils {

    companion object {

        fun fileMD5(file: FileDescriptor): String? {
            var md5: String? = null
            FileInputStream(file).also { fis ->
                try {
                    MessageDigest.getInstance("MD5").also { md ->
                        var buffer = ByteArray(2048)
                        var read = 0
                        do {
                            read = fis.read(buffer)
                            if (read > 0) md.update(buffer, 0, read)
                        } while (read > 0)
                        md5 = md.digest().joinToString("") { "%02x".format(it) }
                    }
                } catch (ex: Exception) {
                    md5 = null
                }
            }.close()
            return md5
        }




    }
}