package com.havish.gce_tvl_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView

class UploadNotice : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_notice)

        val cardView=findViewById<CardView>(R.id.addImage)
        cardView.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v: View?) {

            }
        })
    }
}