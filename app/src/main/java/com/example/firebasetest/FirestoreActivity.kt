package com.example.firebasetest

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreActivity : AppCompatActivity(), OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItems)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val itemsList = mutableListOf<Item>()

        val adapter = MyAdapter(itemsList, this)
        recyclerView.adapter = adapter

// Firestore에서 아이템 데이터 가져오기
        val db = Firebase.firestore
        val itemsCollection = db.collection("items")
        itemsCollection.get()
            .addOnSuccessListener { result1 ->
                for (document in result1) {
                    val title = document.getString("title")
                    val priceLong  = document.getLong("price(WON)") ?: 0L
                    val salesStatus = document.getBoolean("Sales status") ?: true
                    val itemID = document.id

                    val price = priceLong.toInt()

                    val item = Item(title.toString(), price, salesStatus, itemID)
                    itemsList.add(item)
                }
                adapter.notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알립니다.
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }



        // 메세지 함
        findViewById<Button>(R.id.msg_box).setOnClickListener {
            startActivity(
                Intent(this, MsgActivity::class.java)
            )
            finish()
        }

        // 필터 기능 구현
        val filter = findViewById<CheckBox>(R.id.salesFilter)
        filter.setOnCheckedChangeListener { _, isChecked ->
            itemsList.clear()
            if (isChecked){
                itemsCollection.whereEqualTo("Sales status", true).get()
                    .addOnSuccessListener { f ->
                        for (document in f) {
                            val title = document.getString("title")
                            val priceLong  = document.getLong("price(WON)") ?: 0L
                            val salesStatus = document.getBoolean("Sales status") ?: true
                            val itemID = document.id

                            val price = priceLong.toInt()

                            val item = Item(title.toString(), price, salesStatus, itemID)
                            itemsList.add(item)
                        }
                        adapter.notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알립니다.
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            }
            else if(!isChecked) {
                itemsCollection.get()
                    .addOnSuccessListener { result1 ->
                        for (document in result1) {
                            val title = document.getString("title")
                            val priceLong  = document.getLong("price(WON)") ?: 0L
                            val salesStatus = document.getBoolean("Sales status") ?: true
                            val itemID = document.id

                            val price = priceLong.toInt()

                            val item = Item(title.toString(), price, salesStatus, itemID)
                            itemsList.add(item)
                        }
                        adapter.notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알립니다.
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            }
        }


        val userUID = Firebase.auth.currentUser?.uid ?: "No User"
        findViewById<TextView>(R.id.userUid).text = userUID
        val docRef = db.collection("users").document(userUID)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                // 문서에서 'name' 필드의 값을 추출합니다.
                val name = document.getString("name")
                findViewById<TextView>(R.id.username).text = name
            } else {
                // 문서가 존재하지 않을 때의 처리를 합니다.
                Log.w(TAG, "Error getting documents.", document)
            }
        }.addOnFailureListener { exception ->
            // 에러 처리를 합니다.
            Log.w(TAG, "Error getting documents.", exception)
        }

        findViewById<Button>(R.id.Add_item).setOnClickListener {
            startActivity(
                Intent(this, UploadActivity::class.java)
            )
            finish()
        }

        findViewById<Button>(R.id.logout).setOnClickListener {
            Firebase.auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }
    override fun onItemClick(item: Item) {
        val intent = Intent(this, ShowitemActivity::class.java)
        intent.putExtra("ITEM_ID", item.itemID)
        // 필요한 경우, 다른 아이템 속성도 추가
        // 예: intent.putExtra("ITEM_TITLE", item.title)
        startActivity(intent)
        finish()
    }
}
