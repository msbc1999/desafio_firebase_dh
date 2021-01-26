package me.mateus.desafiofirebasedh.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.mateus.desafiofirebasedh.databinding.ActivityGameDetailsBinding
import me.mateus.desafiofirebasedh.models.Game

class GameDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("game")) {
            val game = intent.getSerializableExtra("game") as Game

            binding.topAppBar.title = game.name
            binding.tvGameName.text = game.name
            binding.tvGameRelease.text = game.release
            binding.tvGameDescription.text = game.description
            Glide
                .with(this)
                .load(game.imageUrl)
//                    .placeholder(spinner)
//                    .error(R.drawable.ic_crop)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivGameImage)

        } else finish()
    }
}