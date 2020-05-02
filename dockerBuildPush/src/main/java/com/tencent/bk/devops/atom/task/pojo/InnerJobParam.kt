
package com.tencent.bk.devops.atom.task.pojo

import com.tencent.bk.devops.atom.pojo.AtomBaseParam
import lombok.Data
import lombok.EqualsAndHashCode

@Data
@EqualsAndHashCode(callSuper = true)
class InnerJobParam : AtomBaseParam() {
    val bizId: String = ""
    val scriptType: String = ""
    val scriptContent: String = ""
    val scriptParam: String = ""
    val timeout: Int? = 1000
    val account: String = ""
    val targetEnvType: String = ""
    val targetIpList: String = ""
}
