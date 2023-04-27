package com.android.play.rotomphone

import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.play.rotomphone.data.Pokemons
import com.android.play.rotomphone.databinding.FragmentFirstBinding
import java.io.BufferedReader
import java.io.FileReader

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

//        binding.image.setImageDrawable(Drawable.createFromStream(resources.assets.open("artwork/ball.png"), null))
//        binding.pokemon.setImageDrawable(Drawable.createFromStream(resources.assets.open("artwork/venusaur.png"), null))

        val pokemons = Pokemons().getList("${requireContext().filesDir}/pokemon.json")

        var adapter = PokemonAdapter()
        binding.pokemons.adapter = adapter
        adapter.update(pokemons)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}