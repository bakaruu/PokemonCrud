package com.pokecrud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pokecrud.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var add: Button
    private lateinit var check: Button
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        add = findViewById(R.id.btn_add_pokemon)
        check = binding.btnListPokemon

        add.setOnClickListener {
            val activity = Intent(applicationContext, PokeAdd::class.java)
            startActivity(activity)
        }

        check.setOnClickListener {
            val activity = Intent(applicationContext, PokeCheck::class.java)
            startActivity(activity)
        }
    }
}



