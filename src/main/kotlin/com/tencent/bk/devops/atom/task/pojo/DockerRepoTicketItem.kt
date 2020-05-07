package com.tencent.bk.devops.atom.task.pojo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class DockerRepoTicketItem(
    val url: String = "",
    val ticketId: String = ""
) {
    companion object {
        fun fromJson(json: String?): List<DockerRepoTicketItem> {
            if (json.isNullOrBlank()) return listOf()

            return jacksonObjectMapper().readValue<List<Map<String, String>>>(json!!).map {
                val url = it["key"] ?: throw RuntimeException("source url is null")
                val ticketId = it["value"] ?: throw RuntimeException("ticketId is null")
                DockerRepoTicketItem(url, ticketId)
            }
        }
    }
}
