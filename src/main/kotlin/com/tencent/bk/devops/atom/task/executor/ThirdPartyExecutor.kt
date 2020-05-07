package com.tencent.bk.devops.atom.task.executor

import com.tencent.bk.devops.atom.AtomContext
import com.tencent.bk.devops.atom.task.DockerAtomParam
import com.tencent.bk.devops.atom.task.pojo.DockerRepoTicketItem
import com.tencent.bk.devops.atom.task.utils.DockerUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ThirdPartyExecutor(private val atomContext: AtomContext<DockerAtomParam>) {

    companion object {
        private val logger = LoggerFactory.getLogger(ThirdPartyExecutor::class.java)
    }

    fun execute() {
        executeThird(atomContext)
    }

    private fun executeThird(atomContext: AtomContext<DockerAtomParam>) {
        with(atomContext.param) {
            val workspace = File(atomContext.param.bkWorkspace)

            val loginIps = mutableSetOf<String>()
            try {
                loginIps.addAll(doSourceLogin(this, workspace))

                DockerUtils.dockerBuild(this, workspace)
            } finally {
                loginIps.forEach {
                    DockerUtils.dockerLogout(it, workspace)
                }
            }

            loginIps.clear()
            try {
                loginIps.addAll(doTargetLogin(this, workspace))

                DockerUtils.dockerPush(this, workspace)
            } finally {
                loginIps.forEach {
                    DockerUtils.dockerLogout(it, workspace)
                }
            }

        }
    }

    private fun doSourceLogin(dockerAtomParam: DockerAtomParam, workspace: File): Set<String> {
        val loginIps = mutableSetOf<String>()
        with(dockerAtomParam) {
            if (sourceMirrorTicketPair.isNullOrBlank()) return setOf()
            DockerRepoTicketItem.fromJson(sourceMirrorTicketPair).forEach {
                val loginIp = it.url
                val ticketId = it.ticketId
                DockerUtils.dockerLogin(loginIp, ticketId, workspace)
                loginIps.add(loginIp)
            }

            return loginIps
        }
    }

    private fun doTargetLogin(dockerAtomParam: DockerAtomParam, workspace: File): Set<String> {
        val loginIps = mutableSetOf<String>()
        with(dockerAtomParam) {

            // solve target ticket id
            if (!targetTicketId.isNullOrBlank()) {
                val loginIp = targetImage.removePrefix("/").removeSuffix("/").split("/").first()
                logger.info("login for target host: $loginIp")
                DockerUtils.dockerLogin(loginIp, targetTicketId, workspace)
                logger.info("login successfully for host: $loginIp")
                loginIps.add(loginIp)
            }
            return loginIps
        }
    }

}