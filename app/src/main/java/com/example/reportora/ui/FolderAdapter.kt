package com.example.reportora.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reportora.R


class FolderAdapter(
    private val folderList: List<Folder>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(folder: Folder)
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName: TextView = itemView.findViewById(R.id.textView2)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(folderList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_list, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folderList[position]
        holder.folderName.text = folder.name
    }

    override fun getItemCount(): Int {
        return folderList.size
    }
}

