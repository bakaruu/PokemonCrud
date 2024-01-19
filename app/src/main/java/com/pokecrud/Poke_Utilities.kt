package com.pokecrud

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Poke_Utilities {
    companion object {

        fun poke_Exist(pokemons: List<Pokemon>, name: String): Boolean {
            return pokemons.any { it.name!!.lowercase() == name.lowercase() }

        }

        fun getPokemonList(db_ref: DatabaseReference): MutableList<Pokemon> {
            var pokelist = mutableListOf<Pokemon>()

            db_ref.child("Pokemon")
                .child("Pokemons")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { hijo: DataSnapshot ->
                            val pojo_pokemon = hijo.getValue(Pokemon::class.java)
                            pokelist.add(pojo_pokemon!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return pokelist
        }

        fun pokemon_add(
            db_ref: DatabaseReference,
            id: String,
            name: String,
            number: Int,
            type: String,
            valueRating: Float,
            url_firebase: String,
            dateFormat: String
        ) =
            db_ref.child("Pokemon").child("Pokemons").child(id).setValue(
                Pokemon(
                    id,
                    name,
                    number,
                    type,
                    valueRating,
                    url_firebase,
                    dateFormat
                )
            )




        suspend fun save_logo(sto_ref: StorageReference, id: String, logo: Uri): String {
            lateinit var url_logo_firebase: Uri

            url_logo_firebase = sto_ref.child("Pokemon").child("logos").child(id)
                .putFile(logo).await().storage.downloadUrl.await()

            return url_logo_firebase.toString()
        }

        fun courrutine_thing(activity: AppCompatActivity, context: Context, text: String) {
            activity.runOnUiThread {
                Toast.makeText(
                    context,
                    text,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun loading_animation(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }


        val transitions = DrawableTransitionOptions.withCrossFade(500)

        fun glideOptions(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(loading_animation(context))
                .fallback(R.drawable.pokeball)
                .error(R.drawable.error404)
            return options
        }

        fun isNumber(value: String): Boolean {
            return value.toIntOrNull() != null
        }
    }

}