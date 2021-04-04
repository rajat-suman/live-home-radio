package com.livehomeradio.views.makecall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.livehomeradio.databinding.CallItemBinding
import com.livehomeradio.roomdb.Contacts

class ContactsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = ArrayList<Contacts>()
    private var onItemClick: OnClickContact? = null
    fun initClick(listener: OnClickContact) {
        onItemClick = listener
    }

    fun addItems(data: List<Contacts>) {
        list.clear()
        list.addAll(data)
        list.sortBy { it.name }
        notifyDataSetChanged()
    }

    fun getAllItems(): ArrayList<Contacts> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CallItemBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VH) {
            holder.setData(position)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class VH(private val binding: CallItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(position: Int) {
            binding.model = list[position]
            binding.ivCall.setOnClickListener {
                onItemClick?.onClick(position, it)
            }
            binding.executePendingBindings()
        }

    }

    fun interface OnClickContact {
        fun onClick(position: Int, view: View)
    }
}