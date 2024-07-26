package com.example.reportora.ui

import android.content.ContentValues
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reportora.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DownloadFragment : Fragment(), FileAdapter.OnButtonClickListener  {
    // TODO: Rename and change types of parameters
    private val args:DownloadFragmentArgs by navArgs()
    private val storage = Firebase.storage
    private lateinit var fileAdapter: FileAdapter
    private var fileNames = mutableListOf<String>()
    private lateinit var year:String
    private lateinit var username:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        year = args.year
        username = args.username
        Log.i("DownloadFragment", "Selected Year: $year")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_download, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileAdapter = FileAdapter(fileNames,this)

        // Set up RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.file_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = fileAdapter

        fetchFoldersFromStorage()


    }

    private fun fetchFoldersFromStorage() {
        val listRef = storage.reference.child("$username/$year")
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                for (prefix in listResult.prefixes) {
                    Log.i("DownloadFragment", "Prefix : ${prefix.name}")
                }
                for (item in listResult.items) {
                    // All the items under listRef.
                    fileNames.add(item.name)
                    Log.i("DownloadFragment", "Item : ${item.name}")
                }
                fileAdapter.notifyItemRangeInserted(fileNames.size - listResult.items.size, listResult.items.size)


            }
            .addOnFailureListener { exception ->
                Log.i("DownloadFragment", "Folder List failed", exception)
                // Uh-oh, an error occurred!
            }
    }


    private fun downloadFile(fileName: String) {
        val storageRef = storage.reference
        val fileRef = storageRef.child(fileName)
        Log.i("DownloadFragment", "Downloading Started...")
        Toast.makeText(requireContext(), "Downloading...", Toast.LENGTH_SHORT).show()

        fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            saveFileToMediaStore(fileName, bytes)
            Log.i("DownloadFragment", "Completed")
        }.addOnFailureListener { exception ->
            Log.i("DownloadFragment", "Download failed", exception)
            Toast.makeText(requireContext(), "Failed to download file: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFileToMediaStore(fileName: String, fileData: ByteArray) {
        val resolver = context?.contentResolver ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                outputStream?.write(fileData)
                Toast.makeText(context, "File saved successfully", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDownloadClick(position: Int) {
        val fileName = fileNames[position]
        downloadFile(fileName)
    }

    override fun onDeleteClick(position: Int) {
        val fileName = fileNames[position]
        val storageRef = storage.reference.child("$username/$year/$fileName")
        storageRef.delete().addOnSuccessListener {
            Log.i("DownloadFragment", "File deleted successfully")
            fileNames.removeAt(position)
            fileAdapter.notifyItemRemoved(position)
            Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Log.i("DownloadFragment", "Failed to delete file", exception)
            Toast.makeText(context, "Failed to delete file: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


}