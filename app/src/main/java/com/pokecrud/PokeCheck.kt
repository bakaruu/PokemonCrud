package com.pokecrud

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pokecrud.databinding.ActivityCheckPokemonsBinding

class PokeCheck : AppCompatActivity() {
    private lateinit var binding: ActivityCheckPokemonsBinding

    private lateinit var back: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: MutableList<Pokemon>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adapter: PokemonAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckPokemonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        back = binding.mainBack

        list = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("Pokemon").child("Pokemons")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach { child: DataSnapshot? ->
                        val pojo_pokemon = child?.getValue(Pokemon::class.java)
                        list.add(pojo_pokemon!!)
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        recyclerView = binding.checkPokemonsList
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)

        adapter = PokemonAdapter(list)
        recyclerView.adapter = adapter


        // Configurar el ItemTouchHelper después de inicializar el adaptador y el RecyclerView
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Permitir deslizar a la izquierda o derecha
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // No necesitas implementar el movimiento, devolver false
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Llama al método onItemDismiss del adaptador cuando se desliza un elemento
                (recyclerView.adapter as PokemonAdapter).onItemDismiss(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        back.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pokemon_menu,menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter((newText))
                return true
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                adapter.filter.filter("")
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }




    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Permitir deslizar a la izquierda o derecha
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // No necesitas implementar el movimiento, devolver false
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Llama al método onItemDismiss del adaptador cuando se desliza un elemento
            (recyclerView.adapter as PokemonAdapter).onItemDismiss(viewHolder.adapterPosition)
        }
    }











}