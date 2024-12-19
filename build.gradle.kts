plugins {
    application
    kotlin("jvm") version "2.0.21"
    id(id = "org.javamodularity.moduleplugin") version "1.8.12"
    id(id = "org.openjfx.javafxplugin") version "0.0.13"
    id(id = "org.beryx.jlink") version "3.1.1"
}

group = "edu.tyut"
version = "1.0"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("edu.tyut.template")
    mainClass.set("edu.tyut.template.HelloApplicationKt")
}

kotlin {
    jvmToolchain(21)
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing", "javafx.media")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    implementation("net.synedra:validatorfx:0.5.0") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("eu.hansolo:tilesfx:21.0.3") {
        exclude(group = "org.openjfx")
    }
    implementation("com.github.almasb:fxgl:17.3") {
        exclude(group = "org.openjfx")
        exclude(group = "org.jetbrains.kotlin")
    }
    val junitVersion = "5.10.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.test {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("distributions/app-${javafx.platform.classifier}.zip"))
    options.addAll(listOf("--strip-debug", "--compress", "zip-9", "--no-header-files", "--no-man-pages"))
    launcher {
        name = application.applicationName.lowercase()
    }
    jpackage {
        imageName = application.applicationName.lowercase()
        installerName = application.applicationName.lowercase()
        appVersion = version.toString()
        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            println("windows平台 -> applicationName: ${application.applicationName.lowercase()}")
            icon = "src/main/resources/application.ico"
            installerOptions = listOf("--win-dir-chooser", "--win-menu", "--win-shortcut", "--win-menu-group", application.applicationName.lowercase())
        }
    }
}
/**
 * app-win.zip 直接解压运行 bin/app.bat
 */
tasks.named("jlinkZip") {
    group = "distribution"
}

tasks.register<JavaExec>("runKotlinMain") {
    mainClass.set("edu.tyut.template.HelloApplicationKt") // Kotlin main class
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<Copy>("copyDependencies") {
    // 获取所有运行时类路径的依赖 JAR 文件
    from(configurations.runtimeClasspath) // 获取运行时类路径依赖
    into(layout.buildDirectory.file("modules")) // 将依赖复制到 build/modules 目录
}
