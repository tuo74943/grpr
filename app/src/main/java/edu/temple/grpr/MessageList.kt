package edu.temple.grpr

import java.io.Serializable

class MessageList : Serializable {
    private val messageList : MutableList<AudioMessage> by lazy {
        ArrayList()
    }

    val size : Int get() = messageList.size

    fun setMessages(memoList: MessageList){
        for(i in 0 until memoList.size){
            messageList.add(memoList.getMessage(i))
        }
    }

    fun addMessage(audioMessage: AudioMessage){
        messageList.add(audioMessage)
    }

    fun removeMessages(){
        for (i in 0 until messageList.size){
            messageList[i].deleteFile()
        }
        messageList.clear()
    }

    fun getMessage(index : Int) : AudioMessage{
        return messageList[index]
    }
}