package com.android.play.rotomphone

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.play.rotomphone.data.Constants
import com.android.play.rotomphone.data.Pokemons
import com.android.play.rotomphone.databinding.FragmentPokemonBinding
import com.bumptech.glide.Glide


class PokemonFragment : Fragment() {

    private var _binding: FragmentPokemonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPokemonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get pokemon data.
        var id: Int = requireArguments().getInt("pokemon")
        var pokemon = Pokemons().get(id, requireContext())
        Log.d("Pokemon", "$pokemon")

        // 화면 꾸미기.
        pokemon?.let {
            binding.id.text = it.id.toString()
            var url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"
            Glide.with(requireContext()).load(url).into(binding.image);
            binding.name.text = it.name

            binding.height.text = it.height.toString()
            binding.weight.text = it.weight.toString()

            binding.color.text = it.color

            binding.genus.text = it.genus

            binding.generation.text = it.generation

            // 타입출력
            binding.type.text = it.types.joinToString(separator = ",") { type ->
                if (Constants.TYPES.containsKey(type)) {
                    Constants.TYPES[type]!!
                } else {
                    ""
                }
            }

            binding.hp.text = it.stats["hp"].toString()
            binding.attack.text = it.stats["attack"].toString()
            binding.defense.text = it.stats["defense"].toString()
            binding.speed.text = it.stats["speed"].toString()
            binding.specialAttack.text = it.stats["special-attack"].toString()
            binding.specialDefense.text = it.stats["special-defense"].toString()

            // get first element.
            for ((key, value) in it.flavors) {
                binding.flavors.text = value
                break;
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}