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
import android.view.View
import android.widget.*
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class AddStaff : AppCompatActivity() {
    private lateinit var staffImage:ImageView
    private lateinit var staffName:EditText
    private lateinit var staffEmail:EditText
    private lateinit var staffPost:EditText
    private lateinit var addStaffBtn:Button
    private lateinit var staffCategory: Spinner

    private var category:String="";
    private var bitmap: Bitmap?= null
    private lateinit var progressDialog: ProgressDialog;
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dbRef:DatabaseReference

    private var downloadUrl:String=""
    private var name:String=""
    private var email:String=""
    private var post:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_staff)
        staffImage=findViewById(R.id.addStaffImage)
        staffName=findViewById(R.id.addstaffName)
        staffEmail=findViewById(R.id.addstaffEmail)
        staffPost=findViewById(R.id.addstaffPost)
        addStaffBtn=findViewById(R.id.addStaffBtn)
        staffCategory=findViewById(R.id.staffCategory)

        progressDialog= ProgressDialog(this)

        reference= FirebaseDatabase.getInstance().reference.child("staff")
        storageReference= FirebaseStorage.getInstance().reference

        var list= arrayListOf<String>("Select Category","CSE","MECH","CIVIL","ECE","EEE")
        staffCategory.adapter=
            ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list)

        staffCategory.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category=staffCategory.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        staffImage.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    openGallery();
                }
            }
        )

        addStaffBtn.setOnClickListener{
            checkValidation()
        }
    }
    //Hello
    private fun checkValidation() {
        name=staffName.text.toString()
        email=staffEmail.text.toString()
        post=staffPost.text.toString()
        if(name.isEmpty()){
            staffName.setError("Empty")
            staffName.requestFocus()
        }else if(email.isEmpty()){
            staffEmail.setError("Empty")
            staffEmail.requestFocus()
        }else if(post.isEmpty()){
            staffPost.setError("Empty")
            staffPost.requestFocus()
        }else if(category.equals("Select Category")){
            Toast.makeText(this@AddStaff,"Please select Staff Category",Toast.LENGTH_LONG).show()
        }else if(bitmap==null){
            progressDialog.setMessage("Uploading.....")
            progressDialog.show();
            insertData()
        }else{
            progressDialog.setMessage("Uploading.....")
            progressDialog.show();
            insertImage()
        }
    }

    private fun insertData() {
        dbRef=reference.child(category)
        val uniqueKey:String=dbRef.push().key.toString()

       var data=StaffData(name,email,post,downloadUrl,uniqueKey)

        dbRef.child(uniqueKey).setValue(data).addOnSuccessListener(object:OnSuccessListener<Void>{
            override fun onSuccess(p0: Void?) {
                progressDialog.dismiss()
                Toast.makeText(this@AddStaff, "Staff Added Successfully", Toast.LENGTH_SHORT).show()
            }

        }).addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this@AddStaff, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }

    }

    private fun insertImage() {
        var baos: ByteArrayOutputStream = ByteArrayOutputStream();
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        var finalImg:ByteArray=baos!!.toByteArray();
        val filepath:StorageReference=storageReference.child("Staffs").child(finalImg.toString()+"jpg")
        val uploadTask: UploadTask =filepath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(this@AddStaff, object:
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
                                    insertData()
                                }
                            })
                        }

                    })
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this@AddStaff, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
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

                staffImage!!.setImageBitmap(bitmap);
            }
        }
}