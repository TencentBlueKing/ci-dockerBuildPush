package com.tencent.bk.devops.atom.task.utils.shell

import java.io.File
import java.nio.file.Files
import org.slf4j.LoggerFactory

object ShellUtil {

    private val specialKey = listOf(".", "-")
    private val specialValue = listOf("|", "&", "(", ")")

    fun executeEnhance(
        script: String,
        runtimeVariables: Map<String, String> = mapOf(),
        dir: File? = null
    ): String {
        val enhanceScript = CommandLineUtils.solveSpecialChar(noHistory(script))
        return execute(enhanceScript, dir, runtimeVariables)
    }

    private fun noHistory(script: String): String {
        return "export HISTSIZE=0\n" +
                "export HISEFILESIZE=0\n" +
                "$script\n"
    }

    private fun execute(
        script: String,
        dir: File?,
        runtimeVariables: Map<String, String>
    ): String {
        val file = Files.createTempFile("devops_script", ".sh").toFile()

        val command = StringBuilder()
        val bashStr = script.split("\n")[0]
        if (bashStr.startsWith("#!/")) {
            command.append(bashStr).append("\n")
        }

        command.append("export DEVOPS_BUILD_SCRIPT_FILE=${file.absolutePath}\n")
        command.append("set -e\n")
        command.append(script)
        file.writeText(command.toString())
        executeUnixCommand("chmod +x ${file.absolutePath}", dir)
        val result = executeUnixCommand(file.absolutePath, dir)
        file.delete()
        return result
    }

    private fun executeUnixCommand(command: String, sourceDir: File?): String {
        return CommandLineUtils.execute(command, sourceDir, true)
    }

    private fun specialEnv(key: String, value: String): Boolean {
        specialKey.forEach {
            if (key.contains(it)) {
                return true
            }
        }

        specialValue.forEach {
            if (value.contains(it)) {
                return true
            }
        }
        return false
    }

    private const val WORKSPACE_ENV = "WORKSPACE"
    private val logger = LoggerFactory.getLogger(ShellUtil::class.java)
}
