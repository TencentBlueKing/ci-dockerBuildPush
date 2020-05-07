package com.tencent.bk.devops.atom.task.utils

import com.tencent.bk.devops.atom.task.DockerAtomParam
import com.tencent.bk.devops.atom.task.executor.ThirdPartyExecutor
import com.tencent.bk.devops.atom.task.utils.TicketUtils.getTicketUserAndPass
import com.tencent.bk.devops.atom.task.utils.shell.CommonShellUtils
import java.io.File
import org.slf4j.LoggerFactory

object DockerUtils {

    private val logger = LoggerFactory.getLogger(ThirdPartyExecutor::class.java)

    fun dockerLogin(loginIp: String, ticketId: String, workspace: File) {
        val pair = getTicketUserAndPass(ticketId)
        val username = pair.first
        val password = pair.second
        // WARNING! Using --password via the CLI is insecure. Use --password-stdin.
        val commandStr = "docker login $loginIp --username $username --password $password"
        logger.info("[execute script]: " + String.format("docker login %s --username %s  --password ***", loginIp, username))
        CommonShellUtils.execute(commandStr, workspace)
    }

    fun dockerLogout(loginIp: String, workspace: File) {
        val commandStr = "docker logout $loginIp"
        logger.info("[execute script]: $commandStr")
        CommonShellUtils.execute(commandStr, workspace)
    }

    fun dockerPush(dockerAtomParam: DockerAtomParam, workspace: File) {
        with(dockerAtomParam) {
            val fullImageName = targetImage.removePrefix("/").removeSuffix("/") + ":" + targetImageTag
            val commandStr = "docker push $fullImageName"
            logger.info("[execute script]: $commandStr")
            CommonShellUtils.execute(commandStr, workspace)
            println(">>>>>>>>>>push successfully.....")
        }
    }

    fun dockerBuild(dockerAtomParam: DockerAtomParam, workspace: File) {
        with(dockerAtomParam) {
            val buildScript = StringBuilder()
            val fullImageName = targetImage.removePrefix("/").removeSuffix("/") + ":" + targetImageTag
            buildScript.append("docker build --network=host --pull -f $dockerFilePath -t $fullImageName $dockerBuildDir")
            dockerBuildArgs?.split("\n")?.filter { !it.isBlank() }?.forEach {
                buildScript.append(" --build-arg ").append(it.trim())
            }
            dockerBuildHosts?.split("\n")?.filter { !it.isBlank() }?.forEach {
                buildScript.append(" --add-host ").append(it.trim())
            }
            logger.info("[execute script]: $buildScript")
            CommonShellUtils.execute(buildScript.toString(), workspace)
        }
    }
}
