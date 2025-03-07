plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.spring.dependency-management") version "1.1.3"
}

repositories {
//    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.tencent.devops.ci-plugins:java-plugin-sdk:1.1.9")
    implementation(kotlin("stdlib-jdk8"))
}

dependencyManagement {
    dependencies {
        dependency("com.fasterxml.jackson:jackson-bom:2.12.3")
    }
}

tasks.shadowJar {
    archiveBaseName.set("dockerBuildPush")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.register<Copy>("copyTaskZhJson") {
    from("task.json")
    into("$buildDir/libs")
}

tasks.register<Copy>("copyI18n") {
    from("src/main/resources/i18n")
    into("$buildDir/libs/i18n")
}

tasks.register<Copy>("copyZhResource") {
    from("docs/desc.md")
    from("images/logo.png")
    into("$buildDir/libs/file")
    rename("desc.md", "README.md")
}

tasks.register<Zip>("packageCN") {
    dependsOn(tasks.clean)
    dependsOn(tasks.shadowJar)
    dependsOn(tasks.named("copyTaskZhJson"))
    dependsOn(tasks.named("copyZhResource"))
    dependsOn(tasks.named("copyI18n"))
    into("dockerBuildPush")
    destinationDirectory.set(layout.buildDirectory.dir("out"))
    archiveFileName.set("dockerBuildPush.zip")
    from(layout.buildDirectory.dir("libs"))
}


tasks.register("package") {
    dependsOn(tasks.named("packageCN"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.tencent.bk.devops.atom.AtomRunner"
    }
}

