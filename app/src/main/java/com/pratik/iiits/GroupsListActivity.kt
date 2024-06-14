package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.pratik.iiits.Adapters.GroupsAdapter
import com.pratik.iiits.Models.Group

class GroupsListActivity : AppCompatActivity() {
    private lateinit var createGroupButton: Button
    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupsList = ArrayList<Group>()
    private var groupsListener: ListenerRegistration? = null
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups_list)

        createGroupButton = findViewById(R.id.create_group_button)
        groupsRecyclerView = findViewById(R.id.groups_recycler_view)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        groupsAdapter = GroupsAdapter(groupsList, ::openGroupChat)
        groupsRecyclerView.layoutManager = LinearLayoutManager(this)
        groupsRecyclerView.adapter = groupsAdapter

        category = intent.getStringExtra("category")

        createGroupButton.setOnClickListener {
            val intent = Intent(this, GroupCreateActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        }

        listenForGroupChanges()
    }

    override fun onDestroy() {
        super.onDestroy()
        groupsListener?.remove()
    }

    private fun listenForGroupChanges() {
        val currentUser = auth.currentUser
        if (currentUser != null && category != null) {
            groupsListener = firestore.collection("groups")
                .whereArrayContains("members", currentUser.uid)
                .whereEqualTo("category", category)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        groupsList.clear()
                        for (document in snapshots.documents) {
                            val group = document.toObject(Group::class.java)
                            if (group != null) {
                                groupsList.add(group)
                            }
                        }
                        groupsAdapter.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun openGroupChat(group: Group) {
        val intent = Intent(this, GroupChat::class.java)
        intent.putExtra("groupId", group.id)
        startActivity(intent)
    }
}
