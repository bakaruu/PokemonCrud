package com.pokecrud

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.utilities.Utilities
import com.google.firebase.storage.FirebaseStorage
import com.pokecrud.databinding.ItemPokemonBinding


class PokemonAdapter(private val pokemon_list: MutableList<Pokemon>) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>(), Filterable, ItemTouchHelperAdapter {


    private lateinit var context: Context
    private var filtered_list = pokemon_list

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonAdapter.PokemonViewHolder {
        val item_view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        context = parent.context
        return PokemonViewHolder(item_view)
    }

    override fun onBindViewHolder(holder: PokemonAdapter.PokemonViewHolder, position: Int) {
        val current_object = filtered_list[position]
        holder.number.text = current_object.number.toString()
        holder.name.text = current_object.name
        holder.type.text = current_object.type
        holder.date.text = current_object.date.toString()
        holder.rating.rating = current_object.valueRating!!

        //holder para el longclick
        holder.constraint_row.setOnLongClickListener {
            val intent = Intent(context, PokeEdit::class.java)
            intent.putExtra("pokemon", current_object) // Pasa el objeto Pokémon como un extra
            context.startActivity(intent)
            true // Indica que se ha gestionado el evento de long click
        }


        val URL: String? = when (current_object.logo) {
            "" -> null
            else -> current_object.logo
        }

        Glide.with(context)
            .load(URL)
            .apply(Poke_Utilities.glideOptions(context))
            .transition(Poke_Utilities.transitions)
            .into(holder.logo)
    }

    override fun getItemCount(): Int = filtered_list.size
    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val logo: ImageView = itemView.findViewById(R.id.iv_item_pokemon_logo)
        val number: TextView = itemView.findViewById(R.id.tv_item_pokemon_number_data)
        val name: TextView = itemView.findViewById(R.id.tv_item_pokemon_name_data)
        val type: TextView = itemView.findViewById(R.id.tv_item_pokemon_name_type_data)
        val date: TextView = itemView.findViewById(R.id.tv_item_pokemon_name_date_data)
        val rating: RatingBar = itemView.findViewById(R.id.rb_item_pokemon_ratingbar)
        val constraint_row: ConstraintLayout = itemView.findViewById(R.id.constraint_row)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()) {
                    filtered_list = pokemon_list
                } else {
                    filtered_list = (pokemon_list.filter {
                        it.name.toString().lowercase().contains(busqueda)
                    }) as MutableList<Pokemon>
                }

                val filterResults = FilterResults()
                filterResults.values = filtered_list
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }

    override fun onItemDismiss(position: Int) {
        if (position >= 0 && position < filtered_list.size) {
            // Obtener referencia a Firebase
            val dbRef = FirebaseDatabase.getInstance().getReference()
            val stoRef = FirebaseStorage.getInstance().getReference()
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

            // Obtener el Pokémon en la posición
            val pokemon = filtered_list[position]

            // Actualizar en Firebase y almacenamiento
            dbRef.child("Pokemon").child("Pokemons").child(pokemon.id!!)
                .child("user_notification").setValue(androidId)

            stoRef.child("Pokemon").child("logos").child(pokemon.id!!).delete()

            dbRef.child("Pokemon").child("Pokemons").child(pokemon.id!!).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Eliminar localmente solo si Firebase se actualiza correctamente
                        filtered_list.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "Pokemon deleted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Manejar el caso en que la eliminación en Firebase falla
                        Toast.makeText(context, "Error deleting Pokemon", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }



}