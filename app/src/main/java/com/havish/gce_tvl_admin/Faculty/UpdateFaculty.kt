package com.havish.gce_tvl_admin.Faculty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.havish.gce_tvl_admin.R

class UpdateFaculty : AppCompatActivity() {
    private lateinit var fabButton:FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_faculty)

        fabButton=findViewById(R.id.fab)
        fabButton.setOnClickListener{
            var intent= Intent(this@UpdateFaculty,AddStaff::class.java)
            startActivity(intent)
        }
    }
}