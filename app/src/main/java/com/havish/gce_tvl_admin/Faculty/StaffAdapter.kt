package com.havish.gce_tvl_admin.Faculty

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.havish.gce_tvl_admin.R
import com.squareup.picasso.Picasso

class StaffAdapter (private val staffList: List<StaffData>,private val context:Context,private val category: String) : RecyclerView.Adapter<StaffAdapter.StaffViewAdapter>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewAdapter {
        var view:View=LayoutInflater.from(context).inflate(R.layout.faculty_data_layout,parent,false)
        return StaffViewAdapter(view)
    }

    override fun onBindViewHolder(holder: StaffViewAdapter, position: Int) {
        var item:StaffData=staffList.get(position)
        holder.name.text=item.name
        holder.email.text=item.email
        holder.post.text=item.post
        try {
            Picasso.get().load(item.image).into(holder.image)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.updateBtn.setOnClickListener{
            var intent:Intent= Intent(context,UpdateStaffActivity::class.java)
            intent.putExtra("name",item.name)
            intent.putExtra("email",item.email)
            intent.putExtra("post",item.post)
            intent.putExtra("image",item.image)
            intent.putExtra("key",item.key)
            intent.putExtra("category",category)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return staffList.size
    }
    class StaffViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name:TextView
        var email:TextView
        var post:TextView
        var updateBtn:Button
        var image:ImageView
        init {

            name=itemView.findViewById(R.id.staffName)
            email=itemView.findViewById(R.id.staffEmail)
            post=itemView.findViewById(R.id.staffPost)
            updateBtn=itemView.findViewById(R.id.staffUpdateBtn)
            image=itemView.findViewById(R.id.staffImage)
        }
    }
}