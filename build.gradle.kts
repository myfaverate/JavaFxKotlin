plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.module.plugin)
    alias(libs.plugins.javafx.plugin)
    alias(libs.plugins.beryx.jlink)
}

group = "edu.tyut"
version = "1.0.0"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("template")
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
    implementation(libs.org.controlsfx)
    implementation(libs.formsfx.core) {
        exclude(group = "org.openjfx")
    }
    implementation(libs.validatorfx) {
        exclude(group = "org.openjfx")
    }
    implementation(libs.ikonli.javafx)
    implementation(libs.bootstrapfx.core)
    implementation(libs.tilesfx) {
        exclude(group = "org.openjfx")
    }
    implementation(libs.fxgl) {
        exclude(group = "org.openjfx")
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
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
