package ru.te3ka.homework18

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.te3ka.homework18.adapter.AttractionListAdapter
import ru.te3ka.homework18.databinding.FragmentListBinding
import ru.te3ka.homework18.viewmodel.AttractionViewModel

class ListFragment : Fragment() {
    private lateinit var attractionViewModel: AttractionViewModel
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )

        val adapter = AttractionListAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 3)

        attractionViewModel = ViewModelProvider(this).get(AttractionViewModel::class.java)
        attractionViewModel.allAttractions.observe(viewLifecycleOwner, { attractions ->
            attractions?.let { adapter.submitList(it) }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTakePhoto.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_listFragment_to_createFragment)
        }
    }
}

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    imageUrl?.let {
        Glide.with(view.context)
            .load(imageUrl)
            .into(view)
    }
}