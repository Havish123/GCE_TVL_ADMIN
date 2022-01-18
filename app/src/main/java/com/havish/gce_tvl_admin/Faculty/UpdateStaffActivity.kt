package com.havish.gce_tvl_admin.Faculty

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.havish.gce_tvl_admin.R
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException

class UpdateStaffActivity : AppCompatActivity() {
    private lateinit var updateStaffImage:ImageView
    private lateinit var updateStaffName:EditText
    private lateinit var updateStaffEmail:EditText
    private lateinit var updateStaffPost:EditText
    private lateinit var updateStaffBtn:Button
    private lateinit var deleteStaffBtn:Button
    private var bitmap: Bitmap?= null
    private var name:String=""
    private var email:String=""
    private var post:String=""
    private var image:String=""
    private var uniqueKey:String=""
    private var category:String=""
    private var downloadUrl:String=""

    private lateinit var progressDialog: ProgressDialog;
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_staff)

        name=intent.getStringExtra("name")!!
        email=intent.getStringExtra("email")!!
        post=intent.getStringExtra("post")!!
        image=intent.getStringExtra("image")!!
        uniqueKey=intent.getStringExtra("key")!!
        category=intent.getStringExtra("category")!!

        progressDialog= ProgressDialog(this)

        reference= FirebaseDatabase.getInstance().reference.child("staff")
        storageReference= FirebaseStorage.getInstance().reference

        updateStaffImage=findViewById(R.id.updateStaffImage)
        updateStaffName=findViewById(R.id.updateStaffName)
        updateStaffEmail=findViewById(R.id.updateStaffEmail)
        updateStaffPost=findViewById(R.id.updateStaffPost)
        updateStaffBtn=findViewById(R.id.updateStaffBtn)
        deleteStaffBtn=findViewById(R.id.deleteStaffBtn)

        try {
            Picasso.get().load(image).into(updateStaffImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateStaffName.setText(name)
        updateStaffEmail.setText(email)
        updateStaffPost.setText(post)

        updateStaffImage.setOnClickListener{
            openGallery()
        }

        deleteStaffBtn.setOnClickListener{
            deleteData()
        }
        updateStaffBtn.setOnClickListener{
            name=updateStaffName.getText().toString()
            email=updateStaffEmail.getText().toString()
            post=updateStaffPost.getText().toString()
            checkValidation()
        }
    }

    private fun deleteData() {
        reference.child(category).child(uniqueKey).removeValue().addOnCompleteListener{
            Toast.makeText(this@UpdateStaffActivity, "Staff Delete Successfully", Toast.LENGTH_SHORT).show()
            var intent:Intent=Intent(this@UpdateStaffActivity,UpdateFaculty::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }.addOnFailureListener{

        }
    }

    private fun checkValidation() {
        if(name.isEmpty()){
            updateStaffName.setError("Empty")
            updateStaffName.requestFocus()
        }else if(email.isEmpty()){
            updateStaffEmail.setError("Empty")
            updateStaffEmail.requestFocus()
        }else if(post.isEmpty()){
            updateStaffPost.setError("Empty")
            updateStaffPost.requestFocus()
        }else if(bitmap==null){
            progressDialog.setMessage("Uploading.....")
            progressDialog.show();
            updateData(image)
        }else{
            progressDialog.setMessage("Uploading.....")
            progressDialog.show();
            updateImage()
        }
    }

    private fun updateImage() {
        var baos: ByteArrayOutputStream = ByteArrayOutputStream();
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        var finalImg:ByteArray=baos!!.toByteArray();
        val filepath:StorageReference=storageReference.child("Staffs").child(finalImg.toString()+"jpg")
        val uploadTask: UploadTask =filepath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(this@UpdateStaffActivity, object:
            OnCompleteListener<UploadTask.TaskSnapshot> {
            override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                if(p0.isSuccessful){
                    uploadTask.addOnSuccessListener(object:
                        OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            filepath.downloadUrl.addOnSuccessListener(object :
                                OnSuccessListener<Uri> {
                                override fun onSuccess(p0: Uri?) {
                                    downloadUrl=p0.toString()
                                    updateData(downloadUrl)
                                }
                            })
                        }

                    })
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this@UpdateStaffActivity, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
    }

    private fun updateData(downloadUrl: String) {
        var map:HashMap<String,String> = HashMap<String,String>()
        map.put("name",name)
        map.put("email",email)
        map.put("post",post)
        map.put("image",downloadUrl)

        reference.child(category).child(uniqueKey).updateChildren(map as Map<String, Any>).addOnSuccessListener(object:OnSuccessListener<Void>{
            override fun onSuccess(p0: Void?) {
                progressDialog.dismiss()
                Toast.makeText(this@UpdateStaffActivity, "Staff Update Successfully", Toast.LENGTH_SHORT).show()
                var intent:Intent=Intent(this@UpdateStaffActivity,UpdateFaculty::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

        }).addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this@UpdateStaffActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openGallery() {
        val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getResult.launch(intent)
    }
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            Log.d("Image","Image")
            if (it.resultCode == Activity.RESULT_OK) {
                var uri: Uri?=it.data!!.data;
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
                }catch (e: IOException){
                    e.printStackTrace()
                }

                updateStaffImage!!.setImageBitmap(bitmap);
            }
        }
}