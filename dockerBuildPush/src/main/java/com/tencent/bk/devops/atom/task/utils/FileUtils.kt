
package com.tencent.bk.devops.atom.task.utils

import java.io.File

object FileUtils {

    // docker 指向docket母机挂载的软链
    fun getDockerWorkspraceFilePath(pipelineId: String, vmseqId: String): String {
        return "/data/landun/workspace/$pipelineId/$vmseqId"
    }

    /**
     *  容器挂载路径
     *  容器挂载默认路径： /data/landun/workspace 指向 母机路径：  /data/landun/workspace/${pipeline}/${vmseqId}
     *  往容器挂宅路径下操作文件，母机路径将指向母机挂宅路径。
     *  如向容器 /data/landun/workspace/$buildId 添加文件。则母机路径 /data/landun/workspace/${pipeline}/${vmseqId}/${buildId} 下将有对应文件。
     *  容器内的路径，容器销毁后即不存在。母机的若不删除，一直存在。
     */
    fun getContainerLink(buildId: String): String {
        return "/data/landun/workspace${getContainerTmpLink(buildId)}"
    }

    fun getContainerTmpLink(buildId: String): String {
        return "/$buildId/jobPush"
    }

    /**
     *  拼接母机存放路径
     *  规则 /母机挂宅路径/$buildId/$fileName
     */
    fun getDockerSavePath(pipelineId: String, vmseqId: String, buildId: String, filePath: String): String {
        val fileName: String
        if (!filePath.startsWith("/")) {
            fileName = "/$filePath"
        } else {
            fileName = filePath
        }
        val dockerWorkSpace = getDockerWorkspraceFilePath(pipelineId, vmseqId)
        val containerLink = getContainerTmpLink(buildId)
        return dockerWorkSpace + containerLink + fileName
    }

    /**
     * 按指定路径，在容器内创建文件
     */
    fun createDockerContainerFile(buildId: String, fileName: String): File {
        return File(getContainerLink(buildId), fileName)
    }
}
