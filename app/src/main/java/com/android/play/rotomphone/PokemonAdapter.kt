package com.android.play.rotomphone

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.android.play.rotomphone.databinding.ItemPokemonListBinding
import com.bumptech.glide.Glide

class PokemonAdapter: RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    override fun getItemCount() = 20

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.context,
            ItemPokemonListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(private val context: Context, private val binding: ItemPokemonListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
//            binding.image.setImageDrawable(Drawable.createFromStream(context.resources.assets.open("artwork/rotom.webp"), null))
            binding.name.text = "TEST"
            var url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/10034.png"
            Glide.with(context).load(url).into(binding.image);

        }
    }

}