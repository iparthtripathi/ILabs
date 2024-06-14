package com.pratik.iiits

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pratik.iiits.Adapters.MessagesAdapter
import com.pratik.iiits.Adapters.UsersAdapter
import com.pratik.iiits.Models.Group
import com.pratik.iiits.Models.Message
import com.pratik.iiits.Models.UserModel

class GroupChat : AppCompatActivity() {
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var groupNameTextView: TextView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var sendImageButton: ImageButton
    private lateinit var imagePreview: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var groupId: String

    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = ArrayList<Message>()

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_chat)

        groupNameTextView= findViewById(R.id.group_name_text_view)
        messagesRecyclerView = findViewById(R.id.messages_recycler_view)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)
        sendImageButton = findViewById(R.id.send_image_button)
        imagePreview = findViewById(R.id.image_preview)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        groupId = intent.getStringExtra("groupId")!!

        messagesAdapter = MessagesAdapter(messagesList)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messagesAdapter

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            } else if (imageUri != null) {
                sendImage(imageUri!!)
            }
        }

        groupNameTextView.setOnClickListener {
            showGroupMembersDialog()
        }

        sendImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        loadGroupDetails()
        loadMessages()
    }

    private fun loadGroupDetails() {
        firestore.collection("groups").document(groupId)
            .get()
            .addOnSuccessListener { document ->
                val group = document.toObject(Group::class.java)
                if (group != null) {
                    groupNameTextView.text = group.name
                }
            }
    }

    private fun loadMessages() {
        firestore.collection("groups").document(groupId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                messagesList.clear()
                for (doc in snapshots!!) {
                    val message = doc.toObject(Message::class.java)
                    messagesList.add(message)
                }
                messagesAdapter.notifyDataSetChanged()
                messagesRecyclerView.scrollToPosition(messagesList.size - 1)
            }
    }

    private fun sendMessage(messageText: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val message = Message(
                senderId = currentUser.uid,
                message = messageText,
                timestamp = System.currentTimeMillis()
            )

            firestore.collection("groups").document(groupId).collection("messages").add(message)
                .addOnSuccessListener {
                    messageInput.text.clear()
                    imageUri = null
                    imagePreview.visibility = ImageView.GONE
                }
        }
    }

    private fun sendImage(imageUri: Uri) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val storageRef = storage.reference.child("group_images/${System.currentTimeMillis()}.jpg")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val message = Message(
                            senderId = currentUser.uid,
                            imageUrl = imageUrl,
                            timestamp = System.currentTimeMillis()
                        )

                        firestore.collection("groups").document(groupId).collection("messages").add(message)
                            .addOnSuccessListener {
                                imagePreview.visibility = ImageView.GONE
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            imagePreview.setImageURI(imageUri)
            imagePreview.visibility = ImageView.VISIBLE
        }
    }

    private fun showGroupMembersDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_group_members, null)
        val membersRecyclerView: RecyclerView = dialogView.findViewById(R.id.members_recycler_view)
        val membersAdapter = UsersAdapter(ArrayList(), null)
        membersRecyclerView.layoutManager = LinearLayoutManager(this)
        membersRecyclerView.adapter = membersAdapter

        firestore.collection("groups").document(groupId)
            .get()
            .addOnSuccessListener { document ->
                val group = document.toObject(com.pratik.iiits.Models.Group::class.java)
                if (group != null) {
                    firestore.collection("users")
                        .whereIn("uid", group.members)
                        .get()
                        .addOnSuccessListener { userDocuments ->
                            val membersList = userDocuments.toObjects(UserModel::class.java)
                            membersAdapter.updateUsers(membersList)
                        }
                }
            }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK", null)
            .show()
    }
}