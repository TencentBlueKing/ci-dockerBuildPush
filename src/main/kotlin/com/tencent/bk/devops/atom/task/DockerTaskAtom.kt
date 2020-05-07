package com.tencent.bk.devops.atom.task

import com.tencent.bk.devops.atom.AtomContext
import com.tencent.bk.devops.atom.pojo.StringData
import com.tencent.bk.devops.atom.spi.AtomService
import com.tencent.bk.devops.atom.spi.TaskAtom
import com.tencent.bk.devops.atom.task.executor.ThirdPartyExecutor

@AtomService(paramClass = DockerAtomParam::class)
class DockerTaskAtom : TaskAtom<DockerAtomParam> {


    override fun execute(atomContext: AtomContext<DockerAtomParam>) {
        ThirdPartyExecutor(atomContext).execute()
        val result = atomContext.result
        result.data["BK_DOCKER_TARGE_IMAGE_NAME"] = StringData(atomContext.param.targetImage)
        result.data["BK_DOCKER_TARGE_IMAGE_TAG"] = StringData(atomContext.param.targetImageTag)
    }

}
