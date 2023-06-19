package com.android.play.rotomphone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.play.rotomphone.data.Pokemons
import com.android.play.rotomphone.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val pokemons = Pokemons().getList(requireContext())

        var adapter = PokemonAdapter()
        binding.pokemons.adapter = adapter
        //adapter.setO
        adapter.update(pokemons)

        adapter.setOnChoosePokemonListener { pokemon ->
            val bundle = bundleOf("pokemon" to pokemon.id)
            findNavController().navigate(R.id.action_MainFragment_to_PokemonFragment, bundle)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_PokemonFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}