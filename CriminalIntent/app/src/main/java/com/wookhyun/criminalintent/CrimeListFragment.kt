package com.wookhyun.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wookhyun.criminalintent.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.log

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    private val crimeListViewModel: CrimeListViewModel by viewModels()
    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Can't access null binding"
        }

    inner class CrimeListFragmentToolBar : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.fagment_crime_list, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.new_crime -> {
                    showNewCrime()
                    true
                }
                else -> true
            }
        }

        private fun showNewCrime() {
            viewLifecycleOwner.lifecycleScope.launch {
                val newCrime = Crime(
                    id = UUID.randomUUID(),
                    title = "",
                    date = Date(),
                    isSolved = false
                )
                crimeListViewModel.addCrime(newCrime)
                findNavController().navigate(
                    CrimeListFragmentDirections.showCrimeDetail(newCrime.id)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            CrimeListFragmentToolBar(),
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            }
        }
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeListViewModel.crimes.collect { crimes ->
                    if (crimes.isEmpty()) {
                        binding.invalid.visibility = View.VISIBLE
                        binding.crimeRecyclerView.visibility = View.INVISIBLE
                    } else {
                        binding.invalid.visibility = View.INVISIBLE
                        binding.crimeRecyclerView.visibility = View.VISIBLE
                        binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes, { id ->
                            findNavController().navigate(
                                CrimeListFragmentDirections.showCrimeDetail(id)
                            )
                        }, {
                            CoroutineScope(Dispatchers.IO).launch {
                                crimeListViewModel.deleteCrime(it)
                            }
                        })
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}