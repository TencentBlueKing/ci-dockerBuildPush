package com.tencent.bk.devops.atom.task.api

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.bk.devops.atom.api.BaseApi
import com.tencent.bk.devops.atom.pojo.Result
import com.tencent.bk.devops.atom.utils.json.JsonUtil

class CredentialApi : BaseApi() {

    fun getCredential(credentialId: String): Result<Map<String, String>> {
        val request = super.buildGet("/ticket/api/build/credentials/$credentialId/detail")
        val responseContent = super.request(request, "failed to obtain credential information")
        return JsonUtil.fromJson(responseContent, object : TypeReference<Result<Map<String, String>>>() {})
    }
}
