package org.odoj.onedayonejuzapp.Model

class Message(val idMessage: String, val text: String, val toId:String, val fromId: String, val timeStamp:Long) {
    constructor() : this("","","","",-1)
}