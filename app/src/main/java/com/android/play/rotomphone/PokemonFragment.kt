package com.android.play.rotomphone

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.play.rotomphone.data.Pokemons
import com.android.play.rotomphone.databinding.FragmentSecondBinding
import com.bumptech.glide.Glide


class PokemonFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get pokemon data.
        var id: Int = requireArguments().getInt("pokemon")
        var pokemon = Pokemons().get(id, "${requireContext().filesDir}/pokemons.json")
        Log.d("Pokemon", "$pokemon")

        // 화면 꾸미기.
        pokemon?.let {
            binding.id.text = it.id.toString()
            var url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"
            Glide.with(requireContext()).load(url).into(binding.image);
            binding.name.text = it.name
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}