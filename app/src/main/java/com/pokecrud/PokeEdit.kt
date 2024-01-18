package com.pokecrud

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pokecrud.databinding.ActivityPokeEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PokeEdit : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityPokeEditBinding
    private lateinit var name: EditText
    private lateinit var type: EditText
    private lateinit var number: EditText
    private lateinit var valueRating: RatingBar
    private lateinit var logo: ImageView
    private lateinit var EditPokemon: AppCompatButton
    private lateinit var backEvent: AppCompatButton


    private var url_logo: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private lateinit var poke_list: MutableList<Pokemon>
    private lateinit var pojo_pokemon: Pokemon
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokeEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val this_activity = this
        job = Job()

        pojo_pokemon = intent.getParcelableExtra<Pokemon>("pokemon")!!

        logo = binding.pokeEditLogo
        name = binding.pokeEditName
        type = binding.pokeEditType
        number = binding.pokeEditNumber
        valueRating = binding.pokeEditRating

        EditPokemon = binding.pokeEditEdit
        backEvent = binding.pokeEditBack

        name.setText(pojo_pokemon.name)
        type.setText(pojo_pokemon.type)
        number.setText(pojo_pokemon.number.toString())


        Glide.with(applicationContext)
            .load(pojo_pokemon.logo)
            .apply(Poke_Utilities.glideOptions(applicationContext))
            .transition(Poke_Utilities.transitions)
            .into(logo)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        poke_list = Poke_Utilities.getPokemonList(db_ref)

        EditPokemon.setOnClickListener {

            if (name.text.toString().trim().isEmpty() ||
                type.text.toString().trim().isEmpty() ||
                number.text.toString().trim().isEmpty() ||
                valueRating.rating < 0
            ) {
                Toast.makeText(
                    applicationContext, "Missing data in the form", Toast.LENGTH_SHORT
                ).show()
            }  else {

                //GlobalScope(Dispatchers.IO)
                var url_logo_firebase = String()
                launch {
                    if (url_logo == null) {
                        url_logo_firebase = pojo_pokemon.logo!!
                    } else {
                        url_logo_firebase =
                            Poke_Utilities.save_logo(st_ref, pojo_pokemon.id!!, url_logo!!)
                    }


                    Poke_Utilities.pokemon_add(
                        db_ref, pojo_pokemon.id!!,
                        name.text.toString().trim(),
                        number.text.toString().trim().toInt(),
                        type.text.toString().trim(),
                        valueRating.rating,
                        url_logo_firebase,
                        pojo_pokemon.date.toString().trim()
                    )
                    Poke_Utilities.courrutine_thing(
                        this_activity,
                        applicationContext,
                        "Pokemon edited successfuly "
                    )
                    val activity = Intent(applicationContext, PokeCheck::class.java)
                    startActivity(activity)
                }


            }


        }

        backEvent.setOnClickListener {
            val activity = Intent(applicationContext, PokeCheck::class.java)
            startActivity(activity)
        }

        logo.setOnClickListener {
            galleryAccess.launch("image/*")
        }



    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val galleryAccess = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri? ->
        if(uri!=null){
            url_logo = uri
            logo.setImageURI(uri)
        }


    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

}