package com.tencent.bk.devops.atom.task.utils.shell

import com.tencent.bk.devops.atom.task.pojo.AgentEnv
import com.tencent.bk.devops.atom.task.pojo.OSType
import java.io.File

object CommonShellUtils {

    fun execute(
        script: String,
        dir: File? = null,
        runtimeVariables: Map<String, String> = mapOf()
    ): String {
        val isWindows = AgentEnv.getOS() == OSType.WINDOWS
        return if (isWindows) BatScriptUtil.executeEnhance(script, runtimeVariables, dir)
        else ShellUtil.executeEnhance(script, runtimeVariables, dir)
    }
}
