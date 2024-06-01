package com.pratik.iiits

import PollAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pratik.iiits.Models.Poll
import com.pratik.iiits.Models.UserModel

class PollsFragment : Fragment() {
    private lateinit var pollsRecyclerView: RecyclerView
    private lateinit var pollAdapter: PollAdapter
    private lateinit var firestoreDb: FirebaseFirestore
    private var currentUser: UserModel? = null
    private val pollIds = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_polls, container, false)

        // Initialize Firestore and RecyclerView
        firestoreDb = FirebaseFirestore.getInstance()
        pollsRecyclerView = view.findViewById(R.id.rvPolls)
        pollsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch current user information
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            firestoreDb.collection("users")
                .document(currentUserUid)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    currentUser = userSnapshot.toObject(UserModel::class.java)
                    Log.d("PollsFragment", "Current user: $currentUser")
                    // Initialize adapter and set it to RecyclerView
                    pollAdapter = PollAdapter(requireContext(),pollsRecyclerView,listOf(), currentUser!!)
                    pollsRecyclerView.adapter = pollAdapter
                    // Fetch polls from Firestore
                    fetchPollsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("PollsFragment", "Error fetching current user", exception)
                }
        }

        return view
    }

    private fun fetchPollsFromFirestore() {
        firestoreDb.collection("polls")
            .get()
            .addOnSuccessListener { result ->
                val polls = result.map { document ->
                    pollIds.add(document.id)  // Store document ID
                    document.toObject(Poll::class.java).also {
                        Log.d("PollsFragment", "Fetched poll: ${it.question}, options: ${it.options}")
                    }
                }
                pollAdapter.updatePolls(polls, pollIds)
            }
            .addOnFailureListener { exception ->
                Log.e("PollsFragment", "Error fetching polls", exception)
            }
    }
}
