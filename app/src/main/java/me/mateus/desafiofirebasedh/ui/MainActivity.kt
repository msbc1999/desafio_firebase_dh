package me.mateus.desafiofirebasedh.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.mateus.desafiofirebasedh.GameViewModel
import me.mateus.desafiofirebasedh.databinding.ActivityMainBinding
import me.mateus.desafiofirebasedh.databinding.LayoutGameCardBinding
import me.mateus.desafiofirebasedh.models.Game

class MainActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.cvPesquisarContainer.setOnClickListener {
            binding.etPesquisar.requestFocus()
        }

        binding.fabRegisterGame.setOnClickListener {
            startActivity(Intent(this, GameRegisterActivity::class.java))
        }

        GameAdapter().also { gameAdapter ->
            binding.rvGameList.adapter = gameAdapter

            gameViewModel.gameList.observe(this) { list ->
                gameAdapter.gameList = list
                gameAdapter.notifyDataSetChanged()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        gameViewModel.reloadGameList()
    }
}


class GameAdapter : RecyclerView.Adapter<GameViewHolder>() {

    var gameList: Array<Game> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GameViewHolder(
        LayoutGameCardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        parent.context
    )

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.game = gameList[position]
        holder.bindData()
    }

    override fun getItemCount() = gameList.size

}

class GameViewHolder(
    private val binding: LayoutGameCardBinding,
    private val context: Context
) :
    RecyclerView.ViewHolder(binding.root) {

    var game: Game? = null

    fun bindData() {
        game?.also { g ->
            binding.tvGameName.text = g.name
            binding.tvGameRelease.text = g.release
            Glide
                .with(context)
                .load(g.imageUrl)
//                    .placeholder(spinner)
//                    .error(R.drawable.ic_crop)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivGameImage)
        }
    }
}
