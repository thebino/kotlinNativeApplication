import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.3.50"
    id("kotlinx-serialization") version "1.3.50"
}

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://kotlin.bintray.com/kotlinx") }
    maven { setUrl("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val hostTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else -> throw GradleException("Host OS '$hostOs' is not supported in Kotlin/Native $project.")
    }

    configure(listOf(hostTarget)) {
        binaries {
            executable {
                entryPoint = "native.main"
            }
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("io.ktor:ktor-client-curl:1.2.4")

                // HttpClient MockEngine
                api("io.ktor:ktor-client-mock:1.2.4")
                api("io.ktor:ktor-client-mock-native:1.2.4")

                // install(JsonFeature
                implementation("io.ktor:ktor-client-json-native:1.2.4")

                // KotlinxSerializer()
                implementation("io.ktor:ktor-client-serialization:1.2.4")
                implementation("io.ktor:ktor-client-serialization-jvm:1.2.4")
                implementation("io.ktor:ktor-client-serialization-native:1.2.4")


                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.2.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.11.0")

                implementation("io.ktor:ktor-client-core:1.2.4")
                implementation("io.ktor:ktor-client-json:1.2.4")
            }
        }

        val nativeTest by getting {
            dependencies {
            }
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "12"
}

tasks.withType<Wrapper> {
    gradleVersion = "5.6.1"
    distributionType = Wrapper.DistributionType.ALL
}
