package com.example.reportora.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reportora.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class HomeFragment : Fragment(), FolderAdapter.OnItemClickListener {

    private val storage = Firebase.storage
    private lateinit var folderList: MutableList<Folder>
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userName:String
    private val args:HomeFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null) {
            if(currentUser.uid == "gacmBE4JgqgRvPgGlu5783pUm7k1") {
                userName = args.username
                Log.i("DownloadFragment", "Selected Year: $userName")
            }
            else userName = currentUser.email.toString().replace("@gmail.com", "")

        }


        return  view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderList = mutableListOf()
        folderAdapter = FolderAdapter(folderList,this)

        // Set up RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.folder_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = folderAdapter

        fetchFoldersFromStorage()

    }



    private fun fetchFoldersFromStorage() {
        val listRef = storage.reference.child(userName)
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                for (prefix in listResult.prefixes) {
                    Log.i("HomeFragment", "Prefix : ${prefix.name}")
                    folderList.add(Folder(prefix.name, prefix.toString()))
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                }
                folderAdapter.notifyItemRangeInserted(folderList.size - listResult.prefixes.size, listResult.prefixes.size)
                for (item in listResult.items) {
                    // All the items under listRef.
                    Log.i("HomeFragment", "Item : ${item.name}")
                }
            }
            .addOnFailureListener { exception ->
                Log.i("HomeFragment", "Folder List failed", exception)
                // Uh-oh, an error occurred!
            }
    }

    override fun onItemClick(folder: Folder) {
        val action = HomeFragmentDirections.actionHomeFragmentToDownloadFragment(folder.name,userName)
        findNavController().navigate(action)
    }



}