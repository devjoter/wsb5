package com.flowoo.wsb5

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // nose click button action
        val noseClick: Button = findViewById(R.id.noseClick)
        noseClick.setOnClickListener{

            // toast for debug
            //Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show()

            val intent1 = Intent(this, WishPage::class.java)
            startActivity(intent1)
        }

        // home click button action
        val homeClick: ImageButton = findViewById(R.id.homeClick)
        homeClick.setOnClickListener {

            val intent1 = Intent(this, InfoPage::class.java)
            startActivity(intent1)
        }
    }
}