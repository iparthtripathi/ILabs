package com.pratik.iiits

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pratik.iiits.Models.Poll
import com.pratik.iiits.notes.Adapters.PollAdapter

class PollsFragment : Fragment(R.layout.fragment_polls) {

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var polls: MutableList<Poll>
    private lateinit var pollAdapter: PollAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        polls = mutableListOf()
        pollAdapter = PollAdapter(requireContext(), polls)

        val rvPolls = view.findViewById<RecyclerView>(R.id.rvPolls)
        rvPolls.adapter = pollAdapter
        rvPolls.layoutManager = LinearLayoutManager(requireContext())

        firestoreDb = FirebaseFirestore.getInstance()

        fetchPolls()
    }

    private fun fetchPolls() {
        val pollsReference = firestoreDb.collection("polls")

        pollsReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("PollsFragment", "Error fetching polls: ${e.message}")
                Toast.makeText(requireContext(), "Error fetching polls: ${e.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot == null || snapshot.isEmpty) {
                Log.e("PollsFragment", "No polls found")
                Toast.makeText(requireContext(), "No polls found", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val pollList = snapshot.toObjects(Poll::class.java)
            polls.clear()
            polls.addAll(pollList)
            Log.d("PollsFragment", "Fetched polls: ${polls.size}")
            pollAdapter.notifyDataSetChanged()
        }
    }
}