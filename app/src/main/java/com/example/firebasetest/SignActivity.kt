package com.example.firebasetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        findViewById<Button>(R.id.sign_up_button).setOnClickListener{
            val userEmail = findViewById<EditText>(R.id.userEmail)?.text.toString()
            val password = findViewById<EditText>(R.id.userPassword)?.text.toString()
            val name = findViewById<EditText>(R.id.name)?.text.toString()
            val birthDate = findViewById<EditText>(R.id.birthDate)?.text.toString()
            dosignup(userEmail, password, name, birthDate)
        }

        findViewById<Button>(R.id.Homebutton).setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }

    private fun dosignup(userEmail: String, password: String, name: String, birthDate: String) {
        if (userEmail.isBlank() || password.isBlank()) {
            Toast.makeText(this, "이메일 또는 비밀번호가 입력되지 않았습니다", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isBlank()) {
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (birthDate.length != 8) {
            Toast.makeText(this, "정확한 날짜를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task->
                if (task.isSuccessful) {

                    val uid = task.result?.user?.uid
                    uid?.let {
                        // Firestore에 사용자 데이터 저장
                        saveUserData(it, name, birthDate)
                    }
                    startActivity(
                        Intent(this, FirestoreActivity::class.java)
                    )
                    finish()
                } else {
                    Log.w("SignActivity", "signUpWithEmail", task.exception)
                    Toast.makeText(this, "Sign-Up failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun saveUserData(uid: String, name: String, birthDate: String) {
        // Firestore 인스턴스 가져오기
        val db = Firebase.firestore

        // 사용자 정보를 포함하는 Map 생성
        val userInfo = hashMapOf(
            "uid" to uid,
            "name" to name,
            "birthDate" to birthDate
        )

        // 'users' 컬렉션 내에 uid를 문서 ID로 사용하여 userInfo 문서를 저장
        db.collection("users").document(uid).set(userInfo)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing document", e)
            }
    }

}