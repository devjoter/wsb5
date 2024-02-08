package com.flowoo.wsb5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class InfoPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_page)

        val homeClick: ImageButton = findViewById(R.id.homeClick)
        homeClick.setOnClickListener {

            val intent1 = Intent(this, MainPage::class.java)
            startActivity(intent1)
        }
    }
}