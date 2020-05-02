
package com.tencent.bk.devops.atom.task.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tencent.bk.devops.atom.api.SdkEnv
import com.tencent.bk.devops.atom.task.pojo.FastExecuteScriptRequest
import com.tencent.bk.devops.atom.task.utils.OkhttpUtils
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.LoggerFactory

class JobResourceApi {

    companion object {
        private val objectMapper = ObjectMapper().registerModule(KotlinModule())
        private val logger = LoggerFactory.getLogger(JobResourceApi::class.java)
        private val jobUrl = SdkEnv.getVmSeqId()
    }

    fun fastExecuteScript(executeScriptRequest: FastExecuteScriptRequest, jobHost: String): Long {
        val url = "$jobHost/api/c/compapi/v2/job/fast_execute_script/"
        val requestBody = objectMapper.writeValueAsString(executeScriptRequest)
        val taskInstanceId = sendTaskRequest(requestBody, url)
        if (taskInstanceId <= 0) {
            // 失败处理
            logger.error("start execute script failed")
            throw RuntimeException("start execute script failed")
        }
        return taskInstanceId
    }

    fun getTaskResult(
        appId: String,
        appSecret: String,
        bizId: String,
        taskInstanceId: Long,
        operator: String,
        jobHost: String
    ): TaskResult {
        try {
            val url =
                "$jobHost/api/c/compapi/v2/job/get_job_instance_status/?bk_app_code=$appId&bk_app_secret=$appSecret&bk_username=$operator&bk_biz_id=$bizId&job_instance_id=$taskInstanceId"
//            logger.info("Get request url: $url")
            OkhttpUtils.doGet(url).use { resp ->
                val responseStr = resp.body()!!.string()
//            val responseStr = HttpUtils.get(url)
//                logger.info("responseBody: $responseStr")
                val response: Map<String, Any> = jacksonObjectMapper().readValue(responseStr)
                if (response["code"] == 0) {
                    val responseData = response["data"] as Map<String, Any>
                    val instanceData = responseData["job_instance"] as Map<String, Any>
                    val status = instanceData["status"] as Int
                    return when (status) {
                        3 -> {
                            logger.info("Job execute task finished and success")
                            TaskResult(true, true, "Success")
                        }
                        4 -> {
                            logger.error("Job execute task failed")
                            TaskResult(true, false, "Job failed")
                        }
                        else -> {
                            logger.info("Job execute task running")
                            TaskResult(false, false, "Job Running")
                        }
                    }
                } else {
                    val msg = response["message"] as String
                    logger.error("job execute failed, msg: $msg")
                    throw RuntimeException("job execute failed, msg: $msg")
                }
            }
        } catch (e: Exception) {
            logger.error("execute job error", e)
            throw RuntimeException("execute job error: ${e.message}")
        }
    }

    private fun sendTaskRequest(requestBody: String, url: String): Long {
        val httpReq = Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
            .build()
        OkhttpUtils.doHttp(httpReq).use { resp ->
            val responseStr = resp.body()!!.string()
            if (resp.code() != 200) {
                logger.info("Response code error!|{}", resp.code())
                throw RuntimeException("start job failed, resp code error!")
            }
            val response: Map<String, Any> = jacksonObjectMapper().readValue(responseStr)
            if (response["code"] == 0) {
                val responseData = response["data"] as Map<String, Any>
                val taskInstanceId = responseData["job_instance_id"].toString().toLong()
                logger.info("start job success, taskInstanceId: $taskInstanceId")
                return taskInstanceId
            } else {
                val msg = response["message"] as String
                logger.error("start job failed, msg: $msg")
                throw RuntimeException("start job failed, msg: $msg")
            }
        }
    }

    data class TaskResult(val isFinish: Boolean, val success: Boolean, val msg: String)
}
