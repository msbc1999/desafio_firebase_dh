package me.mateus.desafiofirebasedh

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import me.mateus.desafiofirebasedh.models.Game
import me.mateus.desafiofirebasedh.utils.DesafioFirebaseUtils
import java.io.FileDescriptor
import java.io.FileInputStream
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.concurrent.thread

class GameViewModel : ViewModel() {

    val gameList = MutableLiveData<Array<Game>>().also { mld -> mld.value = arrayOf() }


    fun reloadGameList() {
        thread {
            gameListRef().get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.also { snapshot ->
                        gameList.value = snapshot.toObjects(Game::class.java).toTypedArray()
                    }
                }
            }
        }
    }

    fun registerGame(
        game: Game,
        uploadImage: Supplier<FileDescriptor>,
        callback: Consumer<Exception?> = Consumer { }
    ) {
        thread {
            try {
                DesafioFirebaseUtils.fileMD5(uploadImage.get()).also { md5 ->
                    if (md5 == null) {
                        callback.accept(IllegalArgumentException("Não foi possivel calcular o MD5 da imagem."))
                    } else {
                        game.imageMD5 = md5
                        gameImagesRef().child("game_images").child(md5).also { imgRef ->
                            imgRef.putStream(FileInputStream(uploadImage.get()))
                                .addOnCompleteListener { imgTask ->
                                    if (imgTask.isSuccessful) {
                                        imgRef.downloadUrl.addOnCompleteListener { urlTask ->
                                            if (urlTask.isSuccessful) {
                                                game.imageUrl = urlTask.result.toString()

                                                gameListRef().add(game)
                                                    .addOnCompleteListener { dbTask ->
                                                        if (dbTask.isSuccessful) {
                                                            callback.accept(null)
                                                            reloadGameList()
                                                        }
                                                    }
                                                    .addOnFailureListener { ex -> callback.accept(ex) }
                                            }
                                        }
                                    }
                                }.addOnFailureListener { ex -> callback.accept(ex) }
                        }
                    }
                }
            } catch (ex: Exception) {
                callback.accept(ex)
            }
        }
    }


    private fun firestore() = FirebaseFirestore.getInstance()
    private fun storage() = FirebaseStorage.getInstance()

    private fun gameListRef() = firestore().collection("games")
    private fun gameImagesRef() = storage().reference

}