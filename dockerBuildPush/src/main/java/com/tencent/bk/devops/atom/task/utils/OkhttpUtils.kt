
package com.tencent.bk.devops.atom.task.utils

import java.io.File
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory

object OkhttpUtils {

    private val logger = LoggerFactory.getLogger(OkhttpUtils::class.java)

    val jsonMediaType = MediaType.parse("application/json")

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(30L, TimeUnit.SECONDS)
        .build()

    // 下载会出现从 文件源--（耗时长）---->网关（网关全部收完才转发给用户，所以用户侧与网关存在读超时的可能)-->用户
    private val longHttpClient = OkHttpClient.Builder()
        .connectTimeout(5L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.MINUTES)
        .writeTimeout(30L, TimeUnit.MINUTES)
        .build()

    @Throws(UnsupportedEncodingException::class)
    fun joinParams(params: Map<String, String>): String {
        val paramItem = ArrayList<String>()
        for ((key, value) in params) {
            paramItem.add(key + "=" + URLEncoder.encode(value, "UTF-8"))
        }
        return paramItem.joinToString("&")
    }

    fun doGet(url: String, headers: Map<String, String> = mapOf()): Response {
        return doGet(okHttpClient, url, headers)
    }

    fun doHttp(request: Request): Response {
        return doHttp(okHttpClient, request)
    }

    fun doLongGet(url: String, headers: Map<String, String> = mapOf()): Response {
        return doGet(longHttpClient, url, headers)
    }

    fun doLongHttp(request: Request): Response {
        return doHttp(longHttpClient, request)
    }

    private fun doGet(okHttpClient: OkHttpClient, url: String, headers: Map<String, String> = mapOf()): Response {
        val requestBuilder = Request.Builder()
            .url(url)
            .get()
        if (headers.isNotEmpty()) {
            headers.forEach { key, value ->
                requestBuilder.addHeader(key, value)
            }
        }
        val request = requestBuilder.build()
        return okHttpClient.newCall(request).execute()
    }

    private fun doHttp(okHttpClient: OkHttpClient, request: Request): Response {
        return okHttpClient.newCall(request).execute()
    }

    fun downloadFile(url: String, destPath: File) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        longHttpClient.newCall(request).execute().use { response ->
            if (response.code() == 404) {
                logger.warn("The file $url is not exist")
                throw RuntimeException("文件不存在")
            }
            if (!response.isSuccessful) {
                logger.warn("fail to download the file from $url because of ${response.message()} and code ${response.code()}")
                throw RuntimeException("获取文件失败")
            }
            if (!destPath.parentFile.exists()) {
                destPath.parentFile.mkdirs()
                logger.info("mkdir file:${destPath.parentFile.name}")
            }
            val buf = ByteArray(4096)
            response.body()!!.byteStream().use { bs ->
                var len = bs.read(buf)
                FileOutputStream(destPath).use { fos ->
                    while (len != -1) {
                        fos.write(buf, 0, len)
                        len = bs.read(buf)
                    }
                }
            }
        }
    }

    fun downloadFile(response: Response, destPath: File) {
        if (response.code() == 304) {
            logger.info("file is newest, do not download to $destPath")
            return
        }
        if (!response.isSuccessful) {
            logger.warn("fail to download the file because of ${response.message()} and code ${response.code()}")
            throw RuntimeException("获取文件失败")
        }
        if (!destPath.parentFile.exists()) destPath.parentFile.mkdirs()
        val buf = ByteArray(4096)
        response.body()!!.byteStream().use { bs ->
            var len = bs.read(buf)
            FileOutputStream(destPath).use { fos ->
                while (len != -1) {
                    fos.write(buf, 0, len)
                    len = bs.read(buf)
                }
            }
        }
    }
}
