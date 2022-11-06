package com.wookhyun.photogallery

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wookhyun.photogallery.databinding.FragmentPhotoGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.lang.Exception
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "PhotoGalleryFragment"

@AndroidEntryPoint
class PhotoGalleryFragment : Fragment() {
    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "cannot access binding"
        }
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loadingObserver = Observer<Boolean> {
            when (it) {
                true -> {
                    Log.d(TAG, "onCreate: VISIBLE")
                    binding.requestLoadingScreen.visibility = View.VISIBLE
                }
                false -> {
                    Log.d(TAG, "onCreate: INVISIBLE")
                    binding.requestLoadingScreen.visibility = View.INVISIBLE
                }
            }
        }
        photoGalleryViewModel.loadingState.observe(this, loadingObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)
        binding.photoGrid.adapter = PhotoListAdapter(photoGalleryViewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoGalleryViewModel.uiState.collect { state ->
                    binding.photoGrid.adapter?.notifyDataSetChanged()
                    searchView?.setQuery(state.query, false)
                }
            }
        }
        setupMenu()
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_photo_gallery, menu)

                val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
                searchView = searchItem.actionView as? SearchView

                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        photoGalleryViewModel.setQuery(query ?: "")
                        // hide keyboard
                        (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                            searchView?.windowToken,
                            0
                        )
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_item_search -> {
                        photoGalleryViewModel.setQuery("")
                        false
                    }
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}