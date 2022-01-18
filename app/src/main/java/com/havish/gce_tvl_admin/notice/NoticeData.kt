package com.havish.gce_tvl_admin.notice

class NoticeData() {
    lateinit var title:String
    lateinit var image:String
    lateinit var date:String
    lateinit var time:String
    lateinit var key:String

    constructor(title: String, image: String, date: String, time: String, key: String): this() {
        this.title = title
        this.image = image
        this.date = date
        this.time = time
        this.key = key
    }


}