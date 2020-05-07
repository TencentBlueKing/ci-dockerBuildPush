package com.tencent.bk.devops.atom.task.utils.shell

import com.tencent.bk.devops.atom.task.pojo.AgentEnv
import com.tencent.bk.devops.atom.task.pojo.OSType
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.LogOutputStream
import org.apache.commons.exec.PumpStreamHandler
import org.slf4j.LoggerFactory
import java.io.File

object CommandLineUtils {

    private val logger = LoggerFactory.getLogger(CommandLineUtils::class.java)

    private val specialChars = if (AgentEnv.getOS() == OSType.WINDOWS) {
        listOf('(', ')', '[', ']', '{', '}', '^', ';', '!', ',', '`', '~', '\'', '"')
    } else {
        listOf('|', ';', '&', '$', '>', '<', '`', '!', '\\', '"', '*', '?', '[', ']', '(', ')', '\'')
    }

    fun execute(command: String, workspace: File?, print2Logger: Boolean, prefix: String = "", printException: Boolean = false): String {

        val result = StringBuilder()

        val cmdLine = CommandLine.parse(command)
        val executor = CommandLineExecutor()
        if (workspace != null) {
            executor.workingDirectory = workspace
        }

        val outputStream = object : LogOutputStream() {
            override fun processLine(line: String?, level: Int) {
                if (line == null)
                    return

                val tmpLine = SensitiveLineParser.onParseLine(prefix + line)
                if (print2Logger) {
                    println(tmpLine)
                }
                result.append(tmpLine).append("\n")
            }
        }

        val errorStream = object : LogOutputStream() {
            override fun processLine(line: String?, level: Int) {
                if (line == null) {
                    return
                }

                val tmpLine = prefix + line
                if (print2Logger) {
                    System.err.println(tmpLine)
                }
                result.append(tmpLine).append("\n")
            }
        }
        executor.streamHandler = PumpStreamHandler(outputStream, errorStream)
        try {
            val exitCode = executor.execute(cmdLine)
            if (exitCode != 0) {
                throw RuntimeException("$prefix Script command execution failed with exit code($exitCode)")
            }
        } catch (t: Throwable) {
            if (printException) logger.warn("Fail to execute the command", t)
            if (print2Logger) logger.error("$prefix Fail to execute the command: ${t.message}")
            throw t
        }
        return result.toString()
    }

    fun solveSpecialChar(str: String): String {
        val solveStr = StringBuilder()
        val isWindows = AgentEnv.getOS() == OSType.WINDOWS
        val encodeChar = if (isWindows) '^' else '\\'
        val charArr = str.toCharArray()
        charArr.forEach { ch ->
            if (ch in specialChars) {
                solveStr.append(encodeChar)
            }

            // windows的%还要特殊处理下
            if (isWindows && ch == '%') {
                solveStr.append('%')
            }

            solveStr.append(ch)
        }

        return solveStr.toString()
    }
}
