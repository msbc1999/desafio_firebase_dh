package me.mateus.desafiofirebasedh.models

import com.google.firebase.firestore.DocumentId

data class Game(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var release: String = "",
    var description: String = "",
    var imageMD5: String = "",
    var imageUrl: String = ""
)

data class GameDocument(
    var gameList: List<Game> = listOf()
)