package com.havish.gce_tvl_admin.notice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.havish.gce_tvl_admin.Faculty.StaffAdapter
import com.havish.gce_tvl_admin.Faculty.StaffData
import com.havish.gce_tvl_admin.R

class DeleteNoticeActivity : AppCompatActivity() {

    private lateinit var deleteRecyclerView: RecyclerView
    private lateinit var progressBar:ProgressBar
    private lateinit var noticeList:MutableList<NoticeData>

    private lateinit var reference: DatabaseReference

    private lateinit var adapter: NoticeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_notice)

        reference=FirebaseDatabase.getInstance().reference.child("Notice")

        deleteRecyclerView=findViewById(R.id.deleteNoticeRecyclerView)
        progressBar=findViewById(R.id.progressBar)

        deleteRecyclerView.layoutManager=LinearLayoutManager(this)
        deleteRecyclerView.setHasFixedSize(true)

        getNotice()

    }

    private fun getNotice() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noticeList= mutableListOf()
                if(!snapshot.exists()){

                }else{
                    for (snap: DataSnapshot in snapshot.children){
                        val data=snap.getValue(NoticeData::class.java)
                        noticeList.add(data!!)
                    }
                    adapter= NoticeAdapter(this@DeleteNoticeActivity,noticeList)
                    adapter.notifyDataSetChanged()
                    progressBar.visibility=View.GONE
                    deleteRecyclerView.adapter=adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility=View.GONE
                Toast.makeText(this@DeleteNoticeActivity,error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}