import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.androidx.room.gradle) // Plugin de Room
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlinSerialization)
    //kotlin("kapt")
}

// Configuración de Room
room {
    // Define dónde se guardarán los archivos de esquema de la base de datos
    schemaDirectory("$projectDir/schemas")
}
kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.room.runtime)
            //implementation(libs.voyager.navigator)
            //implementation(libs.voyager.screenmodel)
            //implementation(libs.compose.navigation.core)
            //implementation(libs.compose.navigation.serialization)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.compose.material3)
            //implementation(libs.compose.material3.adaptive)
            //implementation(platform(libs.firebase.bom))
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)

            //implementation(libs.androidx.compose.material.iconsExtended)
            //implementation(libs.voyager.hilt)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.androidx.sqlite.bundled)
            // Lectura básica de archivos (generalmente ya incluida con kotlin-stdlib)
            implementation(kotlin("stdlib-jdk8"))

            // Usamos las referencias definidas en libs.versions.toml
            implementation(libs.commonmark)

            // Opcional: Si usas la extensión de tablas
            implementation(libs.commonmark.gfm.tables)
            implementation(libs.sqlite.jdbc)
            implementation(libs.dagger.core)
            implementation(libs.slf4j.simple)
            //implementation(compose.desktop.current.compose.ui)
            implementation(libs.compose.ui)
            // 2. ¡Añade tu dependencia de íconos!

            // Si usaste la Opción A (Core):
            //implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            implementation(libs.ktor.server.core.jvm)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.server.call.logging)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.websockets)
            // 1. Retrofit + Converter
            implementation(libs.retrofit.core)
            implementation(libs.retrofit.kotlinx.converter)

            // 2. Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.okhttp.core)
            implementation(libs.cloudinary.http5)
            //implementation(libs.androidx.navigation.compose.jvmstubs)
            //implementation("org.commonmark:commonmark:0.27.0")



        }
    }
}
// Configuración de KSP para generar el código de Room
dependencies {
    // El compilador de Room debe ser añadido como KSP para la generación de código
    // Se añade a commonMainMetadata para que funcione en KMP
    add("kspCommonMainMetadata", libs.androidx.room.compiler)

    // Para el target de escritorio (JVM), también se añade
    add("kspJvm", libs.androidx.room.compiler)
    add("kapt", libs.dagger.compiler)
}


compose.desktop {
    application {
        mainClass = "com.jaco.cc3d.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.jaco.cc3d"
            packageVersion = "1.0.0"
        }
    }
}
