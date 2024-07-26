package com.example.reportora.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reportora.R

class FileAdapter(
    private val fileNames: List<String>,
    private val onButtonClickListener: OnButtonClickListener
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    interface OnButtonClickListener {
        fun onDownloadClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileName = fileNames[position]
        holder.textViewFileName.text = fileName

        holder.buttonDownload.setOnClickListener {
            onButtonClickListener.onDownloadClick(position)
        }

        holder.buttonDelete.setOnClickListener {
            onButtonClickListener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = fileNames.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewFileName: TextView = itemView.findViewById(R.id.filename)
        val buttonDownload: Button = itemView.findViewById(R.id.recycle_download)
        val buttonDelete: Button = itemView.findViewById(R.id.recycle_delete)
    }
}

