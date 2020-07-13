# docker build push

# 编译配置
0. gradle 4.8 ~ 4.10
1. 生成一个token在[account settings page](https://github.com/settings/tokens)
2. 修改gradle.properties文件中的MAVEN_CRED_USERNAME、MAVEN_CRED_PASSWORD和MAVEN_REPO_URL
或者在gradle命令运行时增加-DmavenCredUserName、-DmavenCredPassword和-DmavenRepoUrl
- mavenCredUserName：github用户名
- mavenCredPassword：第一步生成的token 
- mavenRepoUrl: maven仓库地址


  


