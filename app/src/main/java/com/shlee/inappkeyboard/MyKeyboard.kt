package com.shlee.inappkeyboard

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime

class MyKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    // keyboard keys (buttons)
    private var mButton1: Button? = null
    private var mButton2: Button? = null
    private var mButton3: Button? = null
    private var mButton4: Button? = null
    private var mButton5: Button? = null
    private var mButton6: Button? = null
    private var mButton7: Button? = null
    private var mButton8: Button? = null
    private var mButton9: Button? = null
    private var mButton0: Button? = null
    private var mButtonDelete: Button? = null
    private var mButtonEnter: Button? = null

    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    private var keyValues: SparseArray<String> = SparseArray()

    // Our communication link to the EditText
    private var inputConnection1: InputConnection? = null

    val myeRf = Firebase.database.reference
    val dateAndtime: LocalDateTime = LocalDateTime.now()
    var onlyDate: LocalDate = LocalDate.now()

    // constructors
    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // initialize buttons

        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)
        mButton1 = findViewById(R.id.button_1)
        mButton2 = findViewById(R.id.button_2)
        mButton3 = findViewById(R.id.button_3)
        mButton4 = findViewById(R.id.button_4)
        mButton5 = findViewById(R.id.button_5)
        mButton6 = findViewById(R.id.button_6)
        mButton7 = findViewById(R.id.button_7)
        mButton8 = findViewById(R.id.button_8)
        mButton9 = findViewById(R.id.button_9)
        mButton0 = findViewById(R.id.button_0)
        mButtonDelete = findViewById(R.id.button_delete)
        mButtonEnter = findViewById(R.id.button_enter)

        // set button click listeners
        mButton1!!.setOnClickListener(this)
        mButton2!!.setOnClickListener(this)
        mButton3!!.setOnClickListener(this)
        mButton4!!.setOnClickListener(this)
        mButton5!!.setOnClickListener(this)
        mButton6!!.setOnClickListener(this)
        mButton7!!.setOnClickListener(this)
        mButton8!!.setOnClickListener(this)
        mButton9!!.setOnClickListener(this)
        mButton0!!.setOnClickListener(this)
        mButtonDelete!!.setOnClickListener(this)
        mButtonEnter!!.setOnClickListener(this)

        // map buttons IDs to input strings
        keyValues.put(R.id.button_1, "1")
        keyValues.put(R.id.button_2, "2")
        keyValues.put(R.id.button_3, "3")
        keyValues.put(R.id.button_4, "4")
        keyValues.put(R.id.button_5, "5")
        keyValues.put(R.id.button_6, "6")
        keyValues.put(R.id.button_7, "7")
        keyValues.put(R.id.button_8, "8")
        keyValues.put(R.id.button_9, "9")
        keyValues.put(R.id.button_0, "0")
        keyValues.put(R.id.button_enter, "\n")
    }

    private fun all_button_enable(boolean: Boolean) {
        mButton1!!.setClickable(boolean)
        mButton2!!.setClickable(boolean)
        mButton3!!.setClickable(boolean)
        mButton4!!.setClickable(boolean)
        mButton5!!.setClickable(boolean)
        mButton6!!.setClickable(boolean)
        mButton7!!.setClickable(boolean)
        mButton8!!.setClickable(boolean)
        mButton9!!.setClickable(boolean)
        mButton0!!.setClickable(boolean)
        mButtonDelete!!.setClickable(boolean)
        mButtonEnter!!.setClickable(boolean)
    }

    private fun all_button_visible(boolean: Boolean) {
        mButton1!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton2!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton3!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton4!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton5!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton6!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton7!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton8!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton9!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButton0!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButtonDelete!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
        mButtonEnter!!.visibility = (if (boolean) View.VISIBLE else View.INVISIBLE)
    }

    override fun onClick(v: View) {
        // do nothing if the InputConnection has not been set yet

        if (inputConnection1 == null) return

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.id == R.id.button_delete) {
            val selectedText = inputConnection1!!.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection1!!.deleteSurroundingText(1, 0)
            } else {
                // delete the selection
                inputConnection1!!.commitText("", 1)
            }
        } else if (v.id == R.id.button_enter) {

            val text = inputConnection1!!.getTextBeforeCursor(4, 0) as String?
            if (text!!.length == 4) {

                val intent = Intent(v.context, UserDataActivity::class.java)
                intent.putExtra("number", text)
                v.context.startActivity(intent)

                all_button_enable(false)
                //all_button_visible(false)

                val userData = UserData("NoData","1")
                onlyDate = LocalDate.now()

                myeRf.child(this.onlyDate.toString()).child(text.toString()).setValue(userData).addOnSuccessListener {
                    Toast.makeText(this.getContext(), "New user save OK", Toast.LENGTH_SHORT).show()
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    all_button_enable(true)
                }.addOnFailureListener{
                    Toast.makeText(this.getContext(), "New user save Fail !", Toast.LENGTH_SHORT).show()
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    inputConnection1!!.deleteSurroundingText(1, 0)
                    all_button_enable(true)
                }
            }
            else {
                Toast.makeText(this.getContext(), "Please enter 4 digits !", Toast.LENGTH_SHORT).show()
            }
        } else {
            val value = keyValues[v.id]
            inputConnection1!!.commitText(value, 1)
        }
    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    fun setInputConnection(ic: InputConnection?) {
        this.inputConnection1 = ic
    }
}
