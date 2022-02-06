package com.havish.gce_tvl_admin.notice


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.havish.gce_tvl_admin.R
import com.squareup.picasso.Picasso

class NoticeAdapter() : RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter>() {
    private lateinit var context:Context
    private lateinit var noticeList: List<NoticeData>

    constructor(context: Context,list:List<NoticeData>) : this() {
        this.context=context
        this.noticeList=list
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoticeAdapter.NoticeViewAdapter {
        var view:View=
            LayoutInflater.from(context).inflate(R.layout.newsfeed_item_layout,parent,false)
        return NoticeViewAdapter(view)
    }

    override fun onBindViewHolder(holder: NoticeViewAdapter, @SuppressLint("RecyclerView") position: Int) {
        var item:NoticeData=noticeList.get(position)
        holder.deleteNoticeTitle.text=item.title
        var display: DisplayMetrics =context.resources.displayMetrics
        var width:Int=display.widthPixels
        try {
            if(item.image!=null)
                Glide.with(context).load(item.image).override(width,width).optionalCenterCrop().into(holder.deleteNoticeImage)
                //Picasso.get().load(item.image).into(holder.deleteNoticeImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.deleteNoticeBtn.setOnClickListener{

            var builder: AlertDialog.Builder=AlertDialog.Builder(context)
            builder.setMessage("Are you sure want to delete?")
            builder.setCancelable(true)
            builder.setPositiveButton("OK",object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    var reference:DatabaseReference=FirebaseDatabase.getInstance().reference.child("Notice")
                    reference.child(item.key).removeValue().addOnCompleteListener{
                        Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show()
                    }
                    notifyItemRemoved(position)
                }

            })
            builder.setNegativeButton("Cancel",object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.cancel()
                }

            })

            var dialog:AlertDialog ?=null
            try{
                dialog=builder.create()
            }catch (err: Exception){
                err.printStackTrace()
            }
            if(dialog!=null){
                dialog.show()
            }
        }

    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    class NoticeViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var deleteNoticeBtn:Button
        var deleteNoticeTitle:TextView
        var deleteNoticeImage:ImageView
        init {
            deleteNoticeBtn=itemView.findViewById(R.id.deleteNoticeBtn)
            deleteNoticeTitle=itemView.findViewById(R.id.deleteNoticeTitle)
            deleteNoticeImage=itemView.findViewById(R.id.deleteNoticeImage)
        }
    }
}