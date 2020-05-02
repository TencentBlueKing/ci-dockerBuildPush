
package com.tencent.bk.devops.atom.task.utils

import com.tencent.bk.devops.atom.task.pojo.FastExecuteScriptRequest
import com.tencent.bk.devops.atom.task.service.JobResourceApi

object JobUtils {

    private val jobResourceApi = JobResourceApi()

    fun fastExecuteScript(executeScriptRequest: FastExecuteScriptRequest, jobHost: String): Long {
        return jobResourceApi.fastExecuteScript(executeScriptRequest, jobHost)
    }

    fun getTaskResult(
        appId: String,
        appSecret: String,
        bizId: String,
        taskInstanceId: Long,
        operator: String,
        jobHost: String
    ): JobResourceApi.TaskResult {
        return jobResourceApi.getTaskResult(appId, appSecret, bizId, taskInstanceId, operator, jobHost)
    }

    fun getDetailUrl(appId: String, taskInstanceId: Long, jobHost: String): String {
        return "<a target='_blank' href='$jobHost/?taskInstanceList&appId=$appId#taskInstanceId=$taskInstanceId'>查看详情</a>"
    }
}
