package com.havish.gce_tvl_admin

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
import androidx.cardview.widget.CardView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.IOException

class UploadImage : AppCompatActivity() {
    private lateinit var imageCategory: Spinner
    private lateinit var addImageBtn:Button
    private lateinit var selectImage:CardView
    private lateinit var gallery:ImageView
    private var category:String="";
    private var bitmap:Bitmap ?= null
    private lateinit var progressDialog: ProgressDialog;
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var downloadUrl:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        imageCategory=findViewById(R.id.imageCategory)
        addImageBtn=findViewById(R.id.uploadGalleryBtn)
        selectImage=findViewById(R.id.addGallaryImage)
        gallery=findViewById(R.id.galleryImageView)
        progressDialog= ProgressDialog(this)

        reference= FirebaseDatabase.getInstance().reference.child("gallery")
        storageReference= FirebaseStorage.getInstance().reference.child("gallery")

        var list= arrayListOf<String>("Select Category","Independance Day","Graduation Day","Other Events")
        imageCategory.adapter=ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list)

        imageCategory.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category=imageCategory.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        selectImage.setOnClickListener(
            object:View.OnClickListener {
                override fun onClick(v: View?) {
                    openGallery();
                }
            }
        )
        addImageBtn.setOnClickListener {
            if (bitmap == null) {
                Toast.makeText(this@UploadImage, "Please Upload Image", Toast.LENGTH_SHORT)
                    .show()
            } else if (category.equals("Select Category")) {
                Toast.makeText(this@UploadImage, "Please Select the Category", Toast.LENGTH_SHORT)
                    .show()
            }else{
                progressDialog.setMessage("Uploading.....")
                progressDialog.show();
                uploadImage()
            }
        }

    }

    private fun uploadImage() {
        var baos: ByteArrayOutputStream = ByteArrayOutputStream();
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        var finalImg:ByteArray=baos!!.toByteArray();
        val filepath: StorageReference =storageReference.child(finalImg.toString()+"jpg")
        val uploadTask: UploadTask =filepath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(this@UploadImage, object:
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
                                    uploadData()
                                }
                            })
                        }

                    })
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this@UploadImage, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
    }

    private fun uploadData() {
        var reference1=reference.child(category)
        var key:String=reference1.push().key.toString()
        reference1.child(key).setValue(downloadUrl)
            .addOnSuccessListener{
                progressDialog.dismiss()
            Toast.makeText(this@UploadImage, "Uploaded Successfully", Toast.LENGTH_SHORT)
                .show()
            }
            .addOnFailureListener{
                progressDialog.dismiss()
            Toast.makeText(this@UploadImage, "Something Went Wrong", Toast.LENGTH_SHORT)
                .show()
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

                gallery!!.setImageBitmap(bitmap);
            }
        }
}


