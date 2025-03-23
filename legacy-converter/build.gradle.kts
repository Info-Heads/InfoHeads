plugins {
    id("java")
    id("se.solrike.sonarlint") version "2.1.0"
}

group = "me.harry0198.infoheads"
version = "1.0"
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

var junitVersion = "5.11.4"
var gsonVersion = "2.12.1"
var spigotVersion = "1.21-R0.1-SNAPSHOT"


dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.google.code.gson:gson:$gsonVersion")
    testImplementation(project(":core"))
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    compileOnly("com.google.code.gson:gson:$gsonVersion")

    implementation(project(":core"))
}

tasks.sonarlintMain {
    dependencies {
        sonarlintPlugins("org.sonarsource.java:sonar-java-plugin:8.9.0.37768")
    }
    reports {
        create("xml") {
            enabled.set(true)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}