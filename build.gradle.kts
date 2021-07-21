import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import com.moowork.gradle.node.npm.NpmTask

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "5.2.0"
  id("com.moowork.node") version "1.3.1"
}

group = "se.kry"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.1.1"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "se.kry.poller.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
   mainClassName = launcherClassName
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-auth-sql-client")
  
  testImplementation("org.hamcrest:hamcrest:2.2")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

node {
  version = "10.15.3"
  npmVersion = "6.4.1"
  download = true
  nodeModulesDir = File("src/main/frontend")
  workDir = file("${projectDir}/.gradle/nodejs")
}

val buildFrontend by tasks.creating(NpmTask::class) {
  setArgs(listOf("run", "build"))
  dependsOn("npmInstall")
}

val copyToWebRoot by tasks.creating(Copy::class) {
  from("src/main/frontend/build")
  destinationDir = File("webroot")
  dependsOn(buildFrontend)
}

val processResources by tasks.getting(ProcessResources::class) {
  dependsOn(copyToWebRoot)
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

task("create-schema", JavaExec::class){
  main = "se.kry.poller.database.CreateSchema"
  classpath = sourceSets["main"].runtimeClasspath
  args = listOf("run")
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--launcher-class=$launcherClassName")
}

tasks.wrapper {
  gradleVersion = "5.2"
}