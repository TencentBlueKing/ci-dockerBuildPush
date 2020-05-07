package com.tencent.bk.devops.atom.task.utils

import java.net.InetAddress

object EnvUtils {
    fun getHostName(): String {
        return System.getenv("HOSTNAME") ?: return (InetAddress.getLocalHost()).hostName
    }
}
