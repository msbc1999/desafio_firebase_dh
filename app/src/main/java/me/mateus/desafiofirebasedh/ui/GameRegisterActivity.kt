package me.mateus.desafiofirebasedh.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import me.mateus.desafiofirebasedh.GameViewModel
import me.mateus.desafiofirebasedh.databinding.ActivityGameRegisterBinding
import me.mateus.desafiofirebasedh.models.Game
import java.util.function.Supplier

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
                        showToast("É necessário preencher todos os campos.")
                    } else if (img == null) {
                        showToast("É necessário selecionar uma imagem.")
                    } else {
                        gameViewModel.registerGame(
                            game, { contentResolver.openFileDescriptor(img, "r")?.fileDescriptor!! }
                        ) { ex ->
                            if (ex == null) {
                                Log.i("GameRegister", "Salvo com sucesso!")
                                finish()
                            } else {
                                Log.e("GameRegister", "Erro ao salvar!", ex)
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
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}