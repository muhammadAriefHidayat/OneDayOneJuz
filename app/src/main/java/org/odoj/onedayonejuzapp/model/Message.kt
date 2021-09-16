package org.odoj.onedayonejuzapp.model

class Message(val idMessage: String, val text: String, val toId:String, val fromId: String, val timeStamp:Long) {
    constructor() : this("","","","",-1)
}