package me.mateus.desafiofirebasedh.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.mateus.desafiofirebasedh.GameViewModel
import me.mateus.desafiofirebasedh.databinding.ActivityGameRegisterBinding
import me.mateus.desafiofirebasedh.models.Game

class GameRegisterActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    private val REQUEST_GAME_IMAGE = 1

    private lateinit var binding: ActivityGameRegisterBinding

    private var selectedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegisterGame.setOnClickListener {
            getDataFromScreen().also { game ->
                selectedImage.also { img ->
                    if (game == null) {
                        showMessage(
                            title = "Aviso",
                            message = "É necessário preencher todos os campos."
                        )
                    } else if (img == null) {
                        showMessage(
                            title = "Aviso",
                            message = "É necessário selecionar uma imagem."
                        )
                    } else {
                        LoadingDialog(this, "Salvando...").also { dialog ->
                            dialog.showDialog()
                            gameViewModel.registerGame(
                                game,
                                { contentResolver.openFileDescriptor(img, "r")?.fileDescriptor!! }
                            ) { ex ->
                                runOnUiThread {
                                    dialog.dismissDialog()
                                }
                                if (ex == null) {
                                    showToast("Salvo com sucesso!")
                                    finish()
                                } else {
                                    Log.e("GameRegister", "Erro ao salvar!", ex)
                                    showToast("Erro ao salvar!")
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.ivSelectImage.setOnClickListener { requestImage() }
    }

    private fun getDataFromScreen(): Game? {
        Game().also { game ->
            game.name = binding.etName.text.toString()
            game.description = binding.etDescription.text.toString()
            game.release = binding.etReleaseDate.text.toString()

            return if (game.name.isBlank() || game.description.isBlank() || game.release.isBlank()) {
                null
            } else {
                game
            }
        }
    }

    private fun requestImage() {
        Intent().also { intent ->
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_GAME_IMAGE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_GAME_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.also { uri -> selectedImage = uri }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun showMessage(
        title: String,
        message: String,
        buttonText: String = "OK",
        clickListener: DialogInterface.OnClickListener? = null
    ) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonText, clickListener)
                .show()
        }
    }


}