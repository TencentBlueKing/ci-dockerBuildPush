
package com.tencent.bk.devops.atom.task.pojo

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 快速执行脚本请求
 */
data class FastExecuteScriptRequest(
    @JsonProperty("bk_app_code")
    val appCode: String,

    @JsonProperty("bk_app_secret")
    val appSecret: String,

    @JsonProperty("bk_username")
    val username: String,

    @JsonProperty("bk_biz_id")
    val bizId: Long,

    @JsonProperty("script_content")
    val scriptContent: String,

    @JsonProperty("script_param")
    val scriptParam: String,

    @JsonProperty("script_timeout")
    val scriptTimeout: Long,

    @JsonProperty("account")
    val account: String,

    @JsonProperty("script_type")
    val scriptType: Int,

    @JsonProperty("ip_list")
    val ipList: List<IpDTO>
)
