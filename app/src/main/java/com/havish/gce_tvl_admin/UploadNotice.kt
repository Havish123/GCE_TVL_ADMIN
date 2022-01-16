package com.havish.gce_tvl_admin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.cardview.widget.CardView
import java.net.URI
import android.os.Build
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class UploadNotice : AppCompatActivity() {

    private val REQ:Int=1
    private lateinit var cardView: CardView
    private var bitmap:Bitmap ?=null
    private lateinit var noticeImageView:ImageView
    private lateinit var upldBtn:Button
    private lateinit var noticeText:EditText
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var downloadUrl:String=""
    private lateinit var progressDialog:ProgressDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_notice)

        reference=FirebaseDatabase.getInstance().reference
        storageReference=FirebaseStorage.getInstance().reference
        progressDialog= ProgressDialog(this)

        cardView=findViewById(R.id.addImage)
        noticeImageView=findViewById(R.id.imagenotice)
        noticeText=findViewById(R.id.noticeTitle)
        upldBtn=findViewById(R.id.uploadNoticeBtn)


        cardView.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v: View?) {
                openGallery();
            }
        })

        upldBtn.setOnClickListener {
            if (noticeText.text.toString().isEmpty()) {
                noticeText.setError("Empty")
                noticeText.requestFocus()
            } else if (bitmap == null) {
                Log.d("Welcome","Welcome")
                uploadData()
            } else {
                Log.d("Hello","Hello")
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading.....")
        progressDialog.show();
        var baos:ByteArrayOutputStream=ByteArrayOutputStream();
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        var finalImg:ByteArray=baos!!.toByteArray();
        val filepath:StorageReference=storageReference.child("Notice").child(finalImg.toString()+"jpg")
        val uploadTask:UploadTask=filepath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(this@UploadNotice, object:OnCompleteListener<UploadTask.TaskSnapshot> {
            override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                if(p0.isSuccessful){
                    uploadTask.addOnSuccessListener(object: OnSuccessListener<UploadTask.TaskSnapshot>{
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            filepath.downloadUrl.addOnSuccessListener(object :OnSuccessListener<Uri>{
                                override fun onSuccess(p0: Uri?) {
                                    downloadUrl=p0.toString()
                                    uploadData()
                                }
                            })
                        }

                    })
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this@UploadNotice, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
    }

    private fun uploadData() {
        reference=reference.child("Notice")
        val uniqueKey:String=reference.push().key.toString()

        var title:String=noticeText.text.toString()

        var calDate:Calendar= Calendar.getInstance()
        var currentDate:SimpleDateFormat= SimpleDateFormat("dd-MM-yy")
        var date:String=currentDate.format(calDate.time)

        var calTime:Calendar= Calendar.getInstance()
        var currentTime:SimpleDateFormat= SimpleDateFormat("hh:mm a")
        var time:String=currentTime.format(calTime.time)

        var notice:NoticeData= NoticeData(title,downloadUrl,date,time,uniqueKey)

        reference.child(uniqueKey).setValue(notice).addOnSuccessListener(object:OnSuccessListener<Void>{
            override fun onSuccess(p0: Void?) {
                progressDialog.dismiss()
                Toast.makeText(this@UploadNotice, "Notice Uploaded", Toast.LENGTH_SHORT).show()
            }

        }).addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this@UploadNotice, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getResult.launch(intent)
    }
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            Log.d("Image","Image")
            if (it.resultCode == Activity.RESULT_OK) {
                var uri: Uri ?=it.data!!.data;
                try{
                    if (Build.VERSION.SDK_INT >= 29) {
                        Log.d("Image29","Image29")
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(applicationContext.contentResolver, uri!!)
                        bitmap = ImageDecoder.decodeBitmap(source)
                    } else {
                        Log.d("ImageJAVA","ImageJAVA")
                        bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            uri
                        )
                    }
                }catch (e:IOException){
                    e.printStackTrace()
                }

                noticeImageView!!.setImageBitmap(bitmap);
            }
        }

}