package com.mobisist.components.messaging

interface Message {
}

open class MessagingException : RuntimeException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
}

interface MessageSender<in T : Message, out R> {

    fun canHandle(msg: Message): Boolean

    @Throws(MessagingException::class)
    fun send(msg: T): R

}