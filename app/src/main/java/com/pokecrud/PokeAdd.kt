package com.pokecrud

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.utilities.Utilities
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pokecrud.databinding.ActivityMainBinding
import com.pokecrud.databinding.ActivityPokeAddBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import kotlin.coroutines.CoroutineContext


class PokeAdd : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityPokeAddBinding
    private lateinit var name: EditText
    private lateinit var type: EditText
    private lateinit var number: EditText
    private lateinit var valueRating: RatingBar
    private lateinit var logo: ImageView
    private lateinit var addPokemon: AppCompatButton
    private lateinit var backEvent: AppCompatButton


    private var url_logo: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private lateinit var poke_list: MutableList<Pokemon>

    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokeAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val this_activity = this
        job = Job()

        logo = binding.pokeAddLogo
        name = binding.pokeAddName
        type = binding.pokeAddType
        number = binding.pokeAddNumber
        valueRating = binding.pokeAddRating

        addPokemon = binding.pokeAddAdd
        backEvent = binding.pokeAddBack

        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance().format(calendar)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()
        poke_list = Poke_Utilities.getPokemonList(db_ref)

        addPokemon.setOnClickListener {

            if (name.text.toString().trim().isEmpty() ||
                type.text.toString().trim().isEmpty() ||
                number.text.toString().trim().isEmpty() ||
                valueRating.rating < 0
            ) {
                Toast.makeText(
                    applicationContext, "Missing data in the form", Toast.LENGTH_SHORT
                ).show()
            } else if (!Poke_Utilities.isNumber(number.text.toString().trim())) {
                Toast.makeText(
                    applicationContext,
                    "Please enter a valid number for 'Number'",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (url_logo == null) {
                Toast.makeText(
                    applicationContext, "Missing logo", Toast.LENGTH_SHORT
                ).show()
            } else if (Poke_Utilities.poke_Exist(poke_list, name.text.toString().trim())) {
                Toast.makeText(
                    applicationContext,
                    "This Pokemon already exist. Try a new one",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                var id_gen: String? = db_ref.child("Pokemon").child("Pokemons").push().key

                launch {
                    val url_logo_firebase = Poke_Utilities.save_logo(st_ref, id_gen!!, url_logo!!)

                    val androidId =
                        Settings.Secure.getString(
                            applicationContext.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                    Poke_Utilities.pokemon_add(
                        db_ref, id_gen!!,
                        name.text.toString().trim(),
                        number.text.toString().toInt(),
                        type.text.toString().trim(),
                        valueRating.rating,
                        url_logo_firebase,
                        dateFormat.toString().trim(),
                        Status.CREATED,
                        androidId

                    )


                    Poke_Utilities.courrutine_thing(
                        this_activity,
                        applicationContext,
                        "Pokemon successfully created"
                    )

                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)


                }

            }


        }

        backEvent.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }


        logo.setOnClickListener {
            accesoGaleria.launch("image/*")

        }


    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }


    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            url_logo = uri
            logo.setImageURI(uri)
        }
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

}