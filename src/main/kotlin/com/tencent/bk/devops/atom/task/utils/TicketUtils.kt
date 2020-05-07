package com.tencent.bk.devops.atom.task.utils

import com.tencent.bk.devops.atom.task.api.CredentialApi

object TicketUtils {

    private val credentialApi = CredentialApi()

    fun getTicketUserAndPass(ticketId: String?): Pair<String, String> {
        if (ticketId.isNullOrBlank()) {
            println("ticket id is null or blank: $ticketId")
            return Pair("", "")
        }

        val ticket: Map<String, String>?
        try {
            ticket = credentialApi.getCredential(ticketId).data
        } catch (e: Exception) {
            throw RuntimeException("获取凭证 $ticketId 失败", e)
        }

        val username = ticket?.get("username")
        val password = ticket?.get("password")

        if (ticket.isNullOrEmpty()) throw RuntimeException("the ticketId is error, please check your input......")
        if (username.isNullOrEmpty()) throw RuntimeException("the username is error, please check your input......")
        if (password.isNullOrEmpty()) throw RuntimeException("the password is error, please check your input......")

        return Pair(username, password)
    }
}