package com.example.firebasetest

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UploadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_item)

        findViewById<Button>(R.id.send_msg).visibility = View.INVISIBLE
        findViewById<Button>(R.id.Upload_button).visibility = View.VISIBLE

        val salesStatus = findViewById<RadioButton>(R.id.radioButton)
        var isRadioButtonChecked = true
        salesStatus.setOnClickListener {
            if (isRadioButtonChecked) {
                salesStatus.isChecked = false
            }
            isRadioButtonChecked = salesStatus.isChecked
        }

        // 등록 버튼
        findViewById<Button>(R.id.Upload_button).setOnClickListener {

            val userid = Firebase.auth.currentUser?.uid ?: "No User"
            val title = findViewById<EditText>(R.id.item_title).text.toString()
            val detail = findViewById<EditText>(R.id.detail).text.toString()
            val priceString = findViewById<EditText>(R.id.item_price).text.toString()
            val status = findViewById<RadioButton>(R.id.radioButton)
            val salesStatus = status.isChecked

            if (title.isBlank()) {
                Log.w(TAG, "Title is empty. Please enter a title.")
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 이벤트 처리 중지
            }

            if (detail.isBlank()) {
                Log.w(TAG, "Detail is empty. Please enter a detail.")
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 이벤트 처리 중지
            }

            if (priceString.isBlank()) {
                Log.w(TAG, "Price is empty. Please enter a price.")
                Toast.makeText(this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 이벤트 처리 중지
            }

            val price = try {
                priceString.toLong() // 문자열을 숫자로 변환
            } catch (e: NumberFormatException) {
                0L // 유효하지 않은 입력의 경우, 가격을 0으로 처리
            }

            val db = Firebase.firestore
            val newItem = hashMapOf(
                "seller" to userid,
                "title" to title,
                "detail" to detail,
                "price(WON)" to price,
                "Sales status" to salesStatus
            )
            db.collection("items")
                .add(newItem)
                .addOnSuccessListener { document ->
                    // 성공적으로 문서가 추가되면 실행
                    Log.d(TAG, "DocumentSnapshot added with ID: ${document.id}")
                }
                .addOnFailureListener { er ->
                    // 문서 추가 실패 시 실행
                    Log.w(TAG, "Error adding document", er)
                }

            startActivity(
                Intent(this, FirestoreActivity::class.java)
            )
            finish()
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