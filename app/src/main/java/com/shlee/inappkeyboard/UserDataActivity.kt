package com.shlee.inappkeyboard

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime

class FieldChecker {
    private val database = Firebase.database

    // 특정 필드 존재 여부 확인
    fun checkFieldExists(path: String, callback: (Boolean) -> Unit) {
        val ref = database.getReference(path)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                callback(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                println("확인 실패: ${error.message}")
                callback(false)
            }
        })
    }

    // 사용 예시
    fun checkUserMiles(userId: String) {
        checkFieldExists("Lank/$userId/mileage") { exists ->
            if (exists) {
                println("mileage 필드가 존재합니다")
            } else {
                println("mileage 필드가 존재하지 않습니다")
            }
        }
    }
}

class UserDataActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var button_ok: Button
    private lateinit var cancelButton: Button
    private lateinit var textView_GradeNum: TextView
    private lateinit var textView_PhoneNum: TextView

    val myeRf = Firebase.database.reference
    val database = Firebase.database
    val dateAndtime: LocalDateTime = LocalDateTime.now()
    var onlyDate: LocalDate = LocalDate.now()

    fun getUserMilesInRank(userId: String, callback: (String?) -> Unit) {
        val userRef = database.getReference("Rank/$userId/mileage")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMiles = snapshot.getValue(String::class.java)
                println("Rank 마일리지 읽기 성공: ${userMiles}")
                callback(userMiles)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Rank 마일리지 읽기 실패: ${error.message}")
                callback(null)
            }
        })
    }

    fun updateUserMilesInRank(userId: String, newMileage: String) {
        database.getReference("Rank/$userId/mileage")
            .setValue(newMileage)
            .addOnSuccessListener {
                println("Rank 업데이트 성공")
            }
            .addOnFailureListener { exception ->
                println("Rank 업데이트 실패")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_data)

        // Phone Number
        val strNumber = intent.getStringExtra("number")

        // Grade
        var nGrade = 0
        radioGroup = findViewById(R.id.radioGroup_Grade)

        var id = "000000"   // 6 digit (phone(4) + '_' + grade(1))

        // Button, TextView
        textView_PhoneNum = findViewById<TextView>(R.id.textView_PhoneNum)
        textView_GradeNum = findViewById<TextView>(R.id.textView_GradeNum)
        button_ok = findViewById(R.id.button_ok)
        cancelButton = findViewById(R.id.button_cancel)

        // Phone Number Display
        textView_PhoneNum.text = strNumber

        // Grad Display
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val selectedOption = selectedRadioButton.text.toString()
            textView_GradeNum.text = selectedOption
            nGrade = selectedOption.toInt()
        }

        // OK Button
        button_ok.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {

                //all_button_enable(false)

                onlyDate = LocalDate.now()
                val userData = UserData(onlyDate.toString(),"1")
                id = strNumber + '_'+ nGrade.toString()

                myeRf.child(this.onlyDate.toString()).child(id).setValue(userData).addOnSuccessListener {
                //myeRf.child("Rank").child(id).setValue(userData).addOnSuccessListener {

                    Toast.makeText(this, "Daily Record : Success", Toast.LENGTH_SHORT).show()

                    getUserMilesInRank(id) { miles ->
                        // Exist already
                        if (miles != null) {
                            Toast.makeText(this, "Total Mileages : ${miles.toInt()+1}", Toast.LENGTH_LONG).show()
                            println("누적 마일리지: ${miles+1}")
                            // Update
                            updateUserMilesInRank(id, (miles.toInt()+1).toString())
                        }
                        // Not Exist
                        else {
                            // Create new user
                            myeRf.child("Rank").child(id).setValue(userData).addOnSuccessListener {
                                Toast.makeText(this, "New user created in Rank : Success", Toast.LENGTH_SHORT).show()
                                println("신규 번호 Rank 등록 성공")
                            }.addOnFailureListener{
                                Toast.makeText(this, "New user created in Rank : Fail", Toast.LENGTH_SHORT).show()
                                println("신규 번호 Rank 등록 실패")
                            }
                        }
                    }
                }.addOnFailureListener{
                    Toast.makeText(this, "Daily Record : Fail", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }

        // Cancel Button
        cancelButton.setOnClickListener {
            finish()
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}