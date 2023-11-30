package com.example.firebasetest

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MsgActivity : AppCompatActivity(), OnMsgClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.msgdata)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItems)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val itemsList = mutableListOf<Msgitem>()
        val adapter = MsgAdapter(itemsList, this)
        recyclerView.adapter = adapter

        val userid = Firebase.auth.currentUser?.uid ?: "No User"
        val db = Firebase.firestore
        val msgcollection = db.collection("msgDB")
        val usercollection = db.collection("users")

        val textView = findViewById<TextView>(R.id.msgstatus)
        val receivebutton = findViewById<Button>(R.id.receivebutton)
        val sendbutton = findViewById<Button>(R.id.sendbutton)

        // 받은 메세지 보기
        receivebutton.setOnClickListener {
            receivebutton.visibility = View.INVISIBLE
            sendbutton.visibility = View.VISIBLE
            textView.text = "받은 메세지"
            itemsList.clear()

            msgcollection.whereEqualTo("receiver", userid).get()
                .addOnSuccessListener { result ->
                    val expectedResultsCount = result.size()
                    var resultsProcessed = 0

                    if (expectedResultsCount == 0) {
                        // 데이터가 없으면 여기서 어댑터 업데이트
                        adapter.notifyDataSetChanged()
                    }

                    for (document in result) {
                        val caller = document.getString("caller").toString()
                        val detail = document.getString("detail").toString()

                        usercollection.document(caller).get()
                            .addOnSuccessListener { userdocument ->
                                val callername = userdocument.getString("name").toString()
                                val msgitem = Msgitem("보낸 사람 : ", callername, detail)
                                itemsList.add(msgitem)
                                resultsProcessed++
                                if (resultsProcessed == expectedResultsCount) {
                                    // 마지막 쿼리가 완료되었을 때 여기서 어댑터 업데이트
                                    adapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
        }

        // 보낸 메세지 보기
        sendbutton.setOnClickListener {
            receivebutton.visibility = View.VISIBLE
            sendbutton.visibility = View.INVISIBLE
            textView.text = "보낸 메세지"
            itemsList.clear()

            msgcollection.whereEqualTo("caller", userid).get()
                .addOnSuccessListener { result ->
                    val expectedResultsCount = result.size()
                    var resultsProcessed = 0

                    if (expectedResultsCount == 0) {
                        // 데이터가 없으면 여기서 어댑터 업데이트
                        adapter.notifyDataSetChanged()
                    }

                    for (document in result) {
                        val receiver = document.getString("receiver").toString()
                        val detail = document.getString("detail").toString()

                        usercollection.document(receiver).get()
                            .addOnSuccessListener { userdocument ->
                                val receivername = userdocument.getString("name").toString()
                                val msgitem = Msgitem("받은 사람 : ", receivername, detail)
                                itemsList.add(msgitem)
                                resultsProcessed++
                                if (resultsProcessed == expectedResultsCount) {
                                    // 마지막 쿼리가 완료되었을 때 여기서 어댑터 업데이트
                                    adapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
        }


        // 메세지 함 나가기
        findViewById<Button>(R.id.goHome).setOnClickListener{
            startActivity(
                Intent(this, FirestoreActivity::class.java)
            )
            finish()
        }
    }
    override fun onMsgClick(item: Msgitem) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("메세지")
        builder.setMessage(item.detail)
        builder.setPositiveButton("확인") {dialog, which ->
            dialog.cancel()
        }
        builder.create().show()
    }
}
