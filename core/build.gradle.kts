plugins {
    id("java")
    id("se.solrike.sonarlint") version "2.1.0"
}

group = "me.harry0198.infoheads.core"
version = "2.5.3"
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val junitVersion = "5.12.2"
val mockitoVersion = "5.15.2"
val snakeYamlVersion = "2.2"
val gsonVersion = "2.11.0"
val slf4jVersion = "1.7.25"
val guiceVersion = "7.0.0"

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    implementation("com.google.inject:guice:$guiceVersion")

    // https://mvnrepository.com/artifact/org.yaml/snakeyaml
    implementation("org.yaml:snakeyaml:$snakeYamlVersion")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
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
    jvmArgs( "--add-opens", "java.base/java.lang=ALL-UNNAMED")
}