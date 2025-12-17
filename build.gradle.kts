plugins {
    java
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
}

application {
    mainClass.set("filemanager.FileManagerApp")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "filemanager.FileManagerApp"
    }

    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.javadoc {
    options.encoding = "UTF-8"
}
