package com.tencent.bk.devops.atom.task

import com.tencent.bk.devops.atom.pojo.AtomBaseParam
import lombok.Data
import lombok.EqualsAndHashCode

@Data
@EqualsAndHashCode(callSuper = true)
class DockerAtomParam : AtomBaseParam() {

    val sourceMirrorTicketPair: String? = null
    val targetImage: String = ""
    val targetImageTag: String = ""
    val targetTicketId: String? = null
    val dockerBuildDir: String? = null
    val dockerFilePath: String? = null
    var dockerBuildArgs: String? = null
    val dockerBuildHosts: String? = null

    override fun toString(): String {
        return "sourceMirrorTicketPair: $sourceMirrorTicketPair, \n" +
            "targetImage: $targetImage, \n" +
            "targetImageTag: $targetImageTag, \n" +
            "targetTicketId: $targetTicketId, \n" +
            "dockerBuildDir: $dockerBuildDir, \n" +
            "dockerFilePath: $dockerFilePath, \n" +
            "dockerBuildArgs: $dockerBuildArgs, \n" +
            "dockerBuildHosts: $dockerBuildHosts, \n"
    }
}
