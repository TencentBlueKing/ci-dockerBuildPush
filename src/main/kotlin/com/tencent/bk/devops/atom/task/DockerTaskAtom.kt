package com.tencent.bk.devops.atom.task

import com.tencent.bk.devops.atom.AtomContext
import com.tencent.bk.devops.atom.pojo.StringData
import com.tencent.bk.devops.atom.spi.AtomService
import com.tencent.bk.devops.atom.spi.TaskAtom
import com.tencent.bk.devops.atom.task.executor.ThirdPartyExecutor
import com.tencent.bk.devops.atom.utils.json.JsonUtil
import org.slf4j.LoggerFactory

@AtomService(paramClass = DockerAtomParam::class)
class DockerTaskAtom : TaskAtom<DockerAtomParam> {

    companion object {
        private val logger = LoggerFactory.getLogger(DockerTaskAtom::class.java)
    }

    override fun execute(atomContext: AtomContext<DockerAtomParam>) {
        logger.info("context param: ${JsonUtil.toJson(atomContext.param)}")
        ThirdPartyExecutor(atomContext).execute()
        val result = atomContext.result
        result.data["BK_DOCKER_TARGE_IMAGE_NAME"] = StringData(atomContext.param.targetImage)
        result.data["BK_DOCKER_TARGE_IMAGE_TAG"] = StringData(atomContext.param.targetImageTag)
    }
}
