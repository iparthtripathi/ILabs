package com.pratik.iiits


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var yourGroupsRecyclerView: RecyclerView
    private lateinit var availableGroupsRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var yourGroupsAdapter: GroupsAdapter
    private lateinit var availableGroupsAdapter: GroupsAdapter
    private val yourGroupsList = ArrayList<Group>()
    private val availableGroupsList = ArrayList<Group>()
    private var groupsListener: ListenerRegistration? = null
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups_list)

        createGroupButton = findViewById(R.id.create_group_button)
        yourGroupsRecyclerView = findViewById(R.id.your_groups_recycler_view)
        availableGroupsRecyclerView = findViewById(R.id.available_groups_recycler_view)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        yourGroupsAdapter = GroupsAdapter(yourGroupsList, ::openGroupChat)
        availableGroupsAdapter = GroupsAdapter(availableGroupsList, ::requestToJoinGroup)
        yourGroupsRecyclerView.layoutManager = LinearLayoutManager(this)
        availableGroupsRecyclerView.layoutManager = LinearLayoutManager(this)
        yourGroupsRecyclerView.adapter = yourGroupsAdapter
        availableGroupsRecyclerView.adapter = availableGroupsAdapter

        category = intent.getStringExtra("category")
        Log.d(TAG, "Category received: $category")

        createGroupButton.setOnClickListener {
            val intent = Intent(this, GroupCreateActivity::class.java)
            intent.putExtra("CATEGORY", category)
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
                .whereEqualTo("category", category)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e(TAG, "Listen failed: $e")
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        yourGroupsList.clear()
                        availableGroupsList.clear()

                        for (document in snapshots.documents) {
                            val group = document.toObject(Group::class.java)
                            if (group != null) {
                                if (group.members.contains(currentUser.uid)) {
                                    yourGroupsList.add(group)
                                } else {
                                    availableGroupsList.add(group)
                                }
                            }
                        }

                        Log.d(TAG, "Your groups: ${yourGroupsList.size}")
                        Log.d(TAG, "Available groups: ${availableGroupsList.size}")

                        yourGroupsAdapter.notifyDataSetChanged()
                        availableGroupsAdapter.notifyDataSetChanged()
                    } else {
                        Log.d(TAG, "No groups found")
                    }
                }
        }
    }


    private fun openGroupChat(group: Group) {
        val intent = Intent(this, GroupChat::class.java)
        intent.putExtra("groupId", group.id)
        startActivity(intent)
    }

    private fun requestToJoinGroup(group: Group) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userName = currentUser.displayName ?: "Unknown" // Assuming user has a display name

            val groupRequest = hashMapOf(
                "userId" to userId,
                "userName" to userName,
                "groupId" to group.id,
                "groupName" to group.name,
                "status" to "pending"
            )

            firestore.collection("groupRequests")
                .add(groupRequest)
                .addOnSuccessListener {
                    // Notify user that request has been sent
                }
        }
    }

}
