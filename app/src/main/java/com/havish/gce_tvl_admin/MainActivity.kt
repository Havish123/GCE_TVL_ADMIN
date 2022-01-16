package com.havish.gce_tvl_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity(),View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val uploadNotice=findViewById<CardView>(R.id.addNotice)
        var uploadFaculty:CardView=findViewById(R.id.addFaculty)
        var uploadGallery=findViewById(R.id.addGallary) as CardView
        var uploadEbook=findViewById(R.id.addPdf) as CardView

//        btn.setOnClickListener(object :View.OnClickListener{
//            override fun onClick(v: View?) {
//                print("Welcome")
//                Log.d("Welcome","Welcome")
//                Toast.makeText(this@MainActivity, "HI", Toast.LENGTH_SHORT).show()
//            }
//        })
        uploadNotice.setOnClickListener(this)
        uploadFaculty.setOnClickListener(this)
        uploadGallery.setOnClickListener(this)
        uploadEbook.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.addNotice->{
                Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show()
                var intent= Intent(this@MainActivity,UploadNotice::class.java)
                startActivity(intent)
                finish()
            }
            R.id.addGallary->{
                Toast.makeText(this,"Welcome Image",Toast.LENGTH_SHORT).show()
                var intent= Intent(this@MainActivity,UploadImage::class.java)
                startActivity(intent)
                finish()
            }
            R.id.addPdf->{
                Toast.makeText(this,"Welcome Image",Toast.LENGTH_SHORT).show()
                var intent= Intent(this@MainActivity,UploadPdf::class.java)
                startActivity(intent)
                finish()
            }
            R.id.addFaculty->{
                Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show()
            }
        }
    }
}