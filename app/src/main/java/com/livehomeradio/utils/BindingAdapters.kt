package com.livehomeradio.utils

import android.text.TextWatcher
import android.util.Log
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.livehomeradio.R
import com.squareup.picasso.Picasso

/** Binding Adapters */
object BindingAdapters {

    @BindingAdapter(value = ["setRecyclerAdapter"], requireAll = false)
    @JvmStatic
    fun setRecyclerAdapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>
    ) {
        recyclerView.adapter = adapter
    }


    @BindingAdapter(value = ["addScrollListener"], requireAll = false)
    @JvmStatic
    fun addScrollListener(
        recyclerView: RecyclerView,
        listener: RecyclerView.OnScrollListener
    ) {
        recyclerView.addOnScrollListener(listener)
    }


    @BindingAdapter(value = ["onCheckChange"], requireAll = false)
    @JvmStatic
    fun onCheckChange(
        compoundButton: CompoundButton,
        listener: CompoundButton.OnCheckedChangeListener
    ) {
        compoundButton.setOnCheckedChangeListener(listener)
    }

    @BindingAdapter(value = ["setImageUrl"], requireAll = false)
    @JvmStatic
    fun setImageUrl(
        imageView: ImageView,
        url: String?
    ) {
        Log.d("ImageUrlIs", "+=======$url")
        when {
            url?.startsWith("/storage")!! -> Picasso.get().load(url).into(imageView)
            else -> Picasso.get().load(url).into(imageView)
        }

    }

    @BindingAdapter(value = ["addTextWatcher"], requireAll = false)
    @JvmStatic
    fun addTextWatcher(
        view: EditText,
        listener: TextWatcher
    ) {
        view.addTextChangedListener(listener)
    }

    @BindingAdapter(value = ["setTint"], requireAll = false)
    @JvmStatic
    fun ImageView.setTint(
        boolean: Boolean
    ) {
        if (boolean)
            this.setColorFilter(ContextCompat.getColor(this.context, R.color._7cb0e1))
        else
            this.setColorFilter(ContextCompat.getColor(this.context, R.color._b4b3b3))

    }
}