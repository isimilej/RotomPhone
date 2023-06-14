package com.android.play.rotomphone

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.play.rotomphone.data.Pokemon
import com.android.play.rotomphone.databinding.ItemPokemonListBinding
import com.bumptech.glide.Glide

class PokemonAdapter: RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    fun interface OnChoosePokemonListener {
        fun onChoosePokemon(pokemon: Pokemon)
    }
    private var onChoosePokemonListener: OnChoosePokemonListener? = null

    private var pokemonList: MutableList<Pokemon> = mutableListOf()

    override fun getItemCount() = pokemonList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.context,
            ItemPokemonListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pokemonList[position])
    }

    fun update(pokemons: MutableList<Pokemon>) {
        pokemonList = pokemons
        notifyDataSetChanged()
    }

    fun setOnChoosePokemonListener(listener: OnChoosePokemonListener) {
        onChoosePokemonListener = listener
    }

    inner class ViewHolder(private val context: Context, private val binding: ItemPokemonListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemon: Pokemon) {
            var url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"
            Glide.with(context).load(url).into(binding.image);

            binding.number.text = "${pokemon.id}"
            binding.name.text = "${pokemon.name}"
            //binding.koname.text = "${pokemon.koname}"

            binding.root.setOnClickListener {
                onChoosePokemonListener?.let {
                    it.onChoosePokemon(pokemon)
                }
            }

//            Glide.with(context)
//                .load(Uri.parse("file:///android_asset/artwork/ivysaur.png"))
//                .into(binding.image)

        }
    }

}