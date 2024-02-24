package com.example.quizfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbox.QuizModel
import com.example.quizfinal.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var quizModelList: MutableList<QuizModel>
    private lateinit var adapter: QuizListAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val realtimeDatabaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        setupRecyclerView()

        // Set click listener for the "Save Name" button
        binding.buttonSaveName.setOnClickListener {
            saveNameToFirestore()
        }

        // Fetch quiz data from Firebase Realtime Database
        fetchDataFromFirebase()
    }

    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchDataFromFirebase() {
        realtimeDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            quizModelList.add(quizModel)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Data fetching cancelled: ${databaseError.message}")
            }
        })
    }

    // Function to save the name to Firestore
    private fun saveNameToFirestore() {
        val name = binding.editTextName.text.toString()  // Get the name entered by the user

        // Document ID for the name document in Firestore (replace with appropriate ID)
        val documentId = "default"

        // Data to be saved in Firestore
        val nameData = hashMapOf("name" to name)

        // Save the name to Firestore
        firestore.collection("names").document(documentId)
            .set(nameData)
            .addOnSuccessListener {
                println("Name successfully saved to Firestore.")
            }
            .addOnFailureListener { e ->
                println("Error saving name to Firestore: $e")
            }
    }
}
