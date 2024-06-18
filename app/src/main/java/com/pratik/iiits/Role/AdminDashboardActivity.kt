package com.pratik.iiits.Role

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pratik.iiits.R

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RoleRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        db = FirebaseFirestore.getInstance()

        val roleRequestsList = findViewById<RecyclerView>(R.id.role_requests_list)
        roleRequestsList.layoutManager = LinearLayoutManager(this)

        adapter = RoleRequestAdapter { requestId, userId, roleName, action ->
            handleRoleRequest(requestId, userId, roleName, action)
        }
        roleRequestsList.adapter = adapter

        val createGroupButton = findViewById<Button>(R.id.create_group_button)
        val createSubgroupButton = findViewById<Button>(R.id.create_subgroup_button)
        val createSubsubgroupButton = findViewById<Button>(R.id.create_subsubgroup_button)
        val groupNameEditText = findViewById<EditText>(R.id.group_name_edit_text)
        val subgroupNameEditText = findViewById<EditText>(R.id.subgroup_name_edit_text)
        val subsubgroupNameEditText = findViewById<EditText>(R.id.subsubgroup_name_edit_text)

        createGroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()
            if (groupName.isNotEmpty()) {
                createGroup(groupName)
            } else {
                Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        createSubgroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()
            val subgroupName = subgroupNameEditText.text.toString().trim()
            if (groupName.isNotEmpty() && subgroupName.isNotEmpty()) {
                createSubgroup(groupName, subgroupName)
            } else {
                Toast.makeText(this, "Group and subgroup names cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        createSubsubgroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()
            val subgroupName = subgroupNameEditText.text.toString().trim()
            val subsubgroupName = subsubgroupNameEditText.text.toString().trim()
            if (groupName.isNotEmpty() && subgroupName.isNotEmpty() && subsubgroupName.isNotEmpty()) {
                createSubsubgroup(groupName, subgroupName, subsubgroupName)
            } else {
                Toast.makeText(this, "Group, subgroup, and subsubgroup names cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch role requests
        fetchRoleRequests()
    }

    private fun fetchRoleRequests() {
        db.collection("roleRequests").whereEqualTo("status", "pending").get().addOnSuccessListener { result ->
            val requests = result.map { document ->
                document.toObject(RoleRequest::class.java).apply {
                    id = document.id
                }
            }
            adapter.submitList(requests)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch role requests", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createGroup(groupName: String) {
        val group = hashMapOf("name" to groupName, "level" to "group")
        db.collection("roles").document(groupName).set(group).addOnSuccessListener {
            Toast.makeText(this, "Group created", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createSubgroup(groupName: String, subgroupName: String) {
        val subgroup = hashMapOf("name" to subgroupName, "level" to "subgroup", "parent" to groupName)
        db.collection("roles").document(groupName).collection("subgroups").document(subgroupName).set(subgroup).addOnSuccessListener {
            Toast.makeText(this, "Subgroup created", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to create subgroup", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createSubsubgroup(groupName: String, subgroupName: String, subsubgroupName: String) {
        val subsubgroup = hashMapOf("name" to subsubgroupName, "level" to "subsubgroup", "parent" to subgroupName)
        db.collection("roles").document(groupName).collection("subgroups").document(subgroupName).collection("subsubgroups").document(subsubgroupName).set(subsubgroup).addOnSuccessListener {
            Toast.makeText(this, "Subsubgroup created", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to create subsubgroup", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRoleRequest(requestId: String, userId: String, roleName: String, action: String) {
        val status = if (action == "approve") "approved" else "rejected"
        db.collection("roleRequests").document(requestId).update("status", status).addOnSuccessListener {
            if (action == "approve") {
                db.collection("users").document(userId).update("roles", FieldValue.arrayUnion(roleName)).addOnSuccessListener {
                    Toast.makeText(this, "Role request approved", Toast.LENGTH_SHORT).show()
                    adapter.removeRequest(requestId)
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update user roles", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Role request rejected", Toast.LENGTH_SHORT).show()
                adapter.removeRequest(requestId)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update role request", Toast.LENGTH_SHORT).show()
        }
    }
}
