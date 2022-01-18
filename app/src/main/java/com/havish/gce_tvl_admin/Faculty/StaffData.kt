package com.havish.gce_tvl_admin.Faculty

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StaffData(){
    lateinit var name:String
    lateinit var email: String
    lateinit var post: String
    lateinit var image: String
    lateinit var key: String
    constructor(name:String,  email:String,  post:String,  image:String,  key:String) : this() {
        this.name=name
        this.email=email
        this.post=post
        this.image=image
        this.key=key
    }
}

