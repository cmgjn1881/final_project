package com.example.firebasetest

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShowitemActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_item)

        val itemId = intent.getStringExtra("ITEM_ID").toString()

        val userId = Firebase.auth.currentUser?.uid ?: "No User"

        val db = Firebase.firestore

        val itemsCollection = db.collection("items")
        itemsCollection.document(itemId).get()
            .addOnSuccessListener { document ->
                if(document != null){
                    val itemTitle = document.getString("title") ?: ""
                    val titleEditText = findViewById<EditText>(R.id.item_title)
                    titleEditText.setText(itemTitle)

                    val itemDetail = document.getString("detail") ?: ""
                    val detail = findViewById<EditText>(R.id.detail)
                    detail.setText(itemDetail)

                    val itemprice = document.getLong("price(WON)") ?: 0
                    val price = findViewById<EditText>(R.id.item_price)
                    price.setText(itemprice.toString())

                    val itemstatus = document.getBoolean("Sales status")
                    val salesStatus = findViewById<RadioButton>(R.id.radioButton)
                    if (itemstatus == true)
                        salesStatus.isChecked = true
                    else if (itemstatus == false)
                        salesStatus.isChecked = false

                    titleEditText.isEnabled = false
                    detail.isEnabled = false
                    price.isEnabled = false
                    salesStatus.isClickable = false

                    val seller = document.getString("seller") ?: ""
                    if (seller == userId){
                        findViewById<Button>(R.id.edit_button).visibility = View.VISIBLE
                        findViewById<Button>(R.id.send_msg).visibility = View.INVISIBLE
                    }
                    val userCollection = db.collection("users")
                    userCollection.document(seller).get()
                        .addOnSuccessListener { doc ->
                            if(doc != null){
                                val username = doc.getString("name")
                                val uname = findViewById<TextView>(R.id.item_seller_name)
                                uname.setText(username)
                            }
                        }
                }
                else {
                    Log.w(TAG, "Error getting documents.", document)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        // 수정 버튼
        findViewById<Button>(R.id.edit_button).setOnClickListener {
            val titleEditText = findViewById<EditText>(R.id.item_title)
            val detail = findViewById<EditText>(R.id.detail)
            val price = findViewById<EditText>(R.id.item_price)
            val salesStatus = findViewById<RadioButton>(R.id.radioButton)
            var isRadioButtonChecked = true
            salesStatus.setOnClickListener {
                if (isRadioButtonChecked) {
                    salesStatus.isChecked = false
                }
                isRadioButtonChecked = salesStatus.isChecked
            }
            findViewById<Button>(R.id.finishEdit).visibility = View.VISIBLE
            findViewById<Button>(R.id.edit_button).visibility = View.INVISIBLE
            titleEditText.isEnabled = true
            detail.isEnabled = true
            price.isEnabled = true
            salesStatus.isClickable = true

        }

        // 수정 완료 버튼
        findViewById<Button>(R.id.finishEdit).setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("수정 완료")
            builder.setMessage("수정을 완료하시겠습니까?")

            builder.setPositiveButton("수정 완료") { dialog, which ->
                findViewById<Button>(R.id.edit_button).visibility = View.VISIBLE
                findViewById<Button>(R.id.finishEdit).visibility = View.INVISIBLE
                val titleEditText = findViewById<EditText>(R.id.item_title).text.toString()
                val detail = findViewById<EditText>(R.id.detail).text.toString()
                val priceString = findViewById<EditText>(R.id.item_price).text.toString()
                val status = findViewById<RadioButton>(R.id.radioButton)
                val salesStatus = status.isChecked
                itemsCollection.document(itemId).update("title", titleEditText)
                itemsCollection.document(itemId).update("detail", detail)
                itemsCollection.document(itemId).update("price(WON)", priceString.toLong())
                itemsCollection.document(itemId).update("Sales status", salesStatus)

                finish()
                startActivity(intent)
            }

            builder.setNegativeButton("수정 계속") { dialog, which ->
                dialog.cancel()
            }
            builder.create().show()
        }

        val msgdata = db.collection("msgDB")

        // 문자 보내기
        findViewById<Button>(R.id.send_msg).setOnClickListener {
            // 여러 줄을 입력받을 수 있는 EditText 생성
            val input = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                hint = "여기에 입력하세요"
                minLines = 1 // 기본적으로 표시할 줄 수
                isVerticalScrollBarEnabled = true // 스크롤 바 활성화
                maxLines = 10 // 최대 줄 수
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("메세지 보내기")

            builder.setView(input)

            // 확인 버튼 설정
            builder.setPositiveButton("확인") { dialog, which ->
                // 사용자 입력 처리
                val userInput = input.text.toString()
                val caller = userId

                itemsCollection.document(itemId).get().addOnSuccessListener { doc ->
                    val receiver = doc.getString("seller") ?: ""

                    val newItem = hashMapOf(
                        "caller" to caller,
                        "detail" to userInput,
                        "receiver" to receiver
                    )
                    msgdata.add(newItem)
                        .addOnSuccessListener {
                            Log.d(TAG, "메세지 작성 완료")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "메세지 작성 실패", e)
                        }
                }
                //Toast.makeText(applicationContext, "입력된 값: $userInput", Toast.LENGTH_SHORT).show()
            }

            // 취소 버튼 설정
            builder.setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }

            // AlertDialog 생성 및 표시
            builder.create().show()
        }

        // 나가기 버튼
        findViewById<Button>(R.id.exitButton).setOnClickListener {
            startActivity(
                Intent(this, FirestoreActivity::class.java)
            )
            finish()
        }
    }
}