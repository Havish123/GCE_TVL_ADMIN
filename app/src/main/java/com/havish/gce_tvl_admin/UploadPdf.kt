package com.havish.gce_tvl_admin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class UploadPdf : AppCompatActivity() {
    private lateinit var addPdf: CardView
    private var pdfData:Uri?=null
    private lateinit var addPdfBtn: Button
    private lateinit var pdfTitle: EditText
    private lateinit var pdfText:TextView
    private lateinit var databasereference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var pdfName:String=""
    private var title:String=""
//    lateinit var  data:MutableMap<String,String>
    private lateinit var progressDialog: ProgressDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_pdf)

        databasereference= FirebaseDatabase.getInstance().reference
        storageReference= FirebaseStorage.getInstance().reference
        progressDialog= ProgressDialog(this)

        addPdf=findViewById(R.id.uploadPdf)
        pdfTitle=findViewById(R.id.pdfTitle)
        addPdfBtn=findViewById(R.id.uploadPdfBtn)
        pdfText=findViewById(R.id.pdfTextView)



        addPdf.setOnClickListener(object: View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(v: View?) {
                openPDFGallery();
            }
        })
        addPdfBtn.setOnClickListener{
            title=pdfTitle.text.toString()
            if(title.isEmpty()){
                pdfTitle.setError("Empty")
                pdfTitle.requestFocus()
            }else if(pdfData==null){
                Toast.makeText(this@UploadPdf, "Please Upload Pdf", Toast.LENGTH_SHORT)
                    .show()
            }else{
                uploadPdf()
            }
        }

    }

    private fun uploadPdf() {
        progressDialog.setMessage("Uploading.....")
        progressDialog.show();
        var reference:StorageReference=storageReference.child("pdf/"+pdfName+"-"+System.currentTimeMillis()+".pdf")
        reference.putFile(pdfData!!).addOnSuccessListener {
            var uriTask:Task<Uri> = it.storage.downloadUrl
            while (!uriTask.isComplete);
            var uri:Uri= uriTask.result!!;
            uploadData(uri.toString())
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(this@UploadPdf, "Something Went Wrong", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadData(downloadUrl: String) {
        var key:String=databasereference.child("pdf").push().key.toString()

        data class pdfDataClass(var pdfTitle:String,var url:String)

        var data=pdfDataClass(title,downloadUrl)

        databasereference.child("pdf").child(key).setValue(data).addOnCompleteListener{
            Toast.makeText(this@UploadPdf, "PDF upload Successfully", Toast.LENGTH_SHORT)
                .show()
            title=""
            pdfTitle.setText("")
            progressDialog.dismiss()
        }.addOnFailureListener{
            Toast.makeText(this@UploadPdf, "Failed to Upload PDF", Toast.LENGTH_SHORT)
                .show()
            progressDialog.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openPDFGallery() {
        var intent= Intent()
        intent.type="application/pdf"
        intent.action=Intent.ACTION_GET_CONTENT
        getResult.launch(Intent.createChooser(intent,"Select pdf file"))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            Log.d("PDF","PDF")
            if (it.resultCode == Activity.RESULT_OK) {
                pdfData= it.data?.data
                if(pdfData.toString().startsWith("content://")){
                    var cursor:Cursor?=null
                    try {
                        cursor=this.contentResolver.query(pdfData!!,null,null,null)
                        if(cursor!=null && cursor.moveToFirst()){
                            pdfName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } catch (e: Exception) {
                        Log.d("Error",e.toString())
                    }
                }else if(pdfData.toString().startsWith("file://")){
                    pdfName= File(pdfData.toString()).name
                }
                pdfText.text=pdfName
            }
        }
}