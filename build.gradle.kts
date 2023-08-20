/*import org.jetbrains.kotlin.gradle.tasks.KotlinCompile*/

plugins {
    id("java")
//    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    id("org.jetbrains.intellij") version "1.8.0"
}
val arcVersion = "v141.1"

group = "com.github.Zelaux"
version = "1.2.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
dependencies {
    testImplementation("junit:junit:4.13.2")
//    implementation("com.github.Anuken.Arc:arc-core:$arcVersion")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.3")
//    type.set("IC") // Target IDE Platform
    type.set("IC") // Target IDE Platform

//    plugins.set(listOf(/* Plugin Dependencies */))
    plugins.set(listOf(
            "java",
//            "org.jetbrains.kotlin:222-1.8.21-release-380-IJ4167.29",
            "org.intellij.intelliLang",
            /*"DevKit",*/
            ))
}

file("src/parts").listFiles()?.toMutableList().let { it ?: mutableListOf() }.also { newDirs ->
    newDirs.add(file("src/main/gen"))


    sourceSets {
        main {
            java {
                newDirs += srcDirs
                java.srcDirs(newDirs)
            }
        }
    }
}

tasks {

    buildPlugin{
        dependsOn(test)
    }
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "16"
        targetCompatibility = "16"
//        langua


        val compilerArgs = options.compilerArgs
        compilerArgs.addAll(listOf("--add-exports", "java.desktop/sun.awt.image=ALL-UNNAMED"))
    }
    /*withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }*/
    this.getByName("setupInstrumentCode").actions.clear();
    this.getByName("instrumentCode").actions.clear();
    this.getByName("postInstrumentCode").actions.clear();
    patchPluginXml {
        sinceBuild.set("222.3345.118")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
/*val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.7"
}*/
// Generated content


file("src/parts").listFiles()?.toMutableList().let { it ?: mutableListOf() }.also { newDirs ->
    newDirs.add(file("src/main/gen"))


    sourceSets {
        main {
            java {
                newDirs += srcDirs
                java.srcDirs(newDirs)
            }
        }
    }
}