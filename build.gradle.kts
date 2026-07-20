plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.graalvm.buildtools.native") version "1.1.1"
}

group = "org.arig"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

val jfxVersion = "24.0.1"
val jfxPlatform: String = run {
	val os = System.getProperty("os.name").lowercase()
	val arch = System.getProperty("os.arch")
	when {
		"mac" in os && "aarch64" in arch -> "mac-aarch64"
		"mac" in os -> "mac"
		"win" in os -> "win"
		else -> "linux"
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")

	listOf("javafx-controls", "javafx-graphics", "javafx-base", "javafx-media", "javafx-web").forEach { module ->
		implementation("org.openjfx:$module:$jfxVersion:$jfxPlatform")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	archiveFileName.set("arig-cipher.jar")
}

tasks.named<Jar>("jar") {
	enabled = false
}

graalvmNative {
	binaries.named("main") {
		imageName.set("arig-cipher")
		buildArgs.addAll(
			"--no-fallback",
			"--enable-native-access=ALL-UNNAMED",
			"-H:+AddAllCharsets",
		)
	}
}

// Affiche la version propre (sans -SNAPSHOT) — utilisé par le CI
tasks.register("printVersion") {
	group = "help"
	doLast { println(version.toString().substringBefore("-")) }
}

// Build natif via Liberica NIK + copie dans dist/ : ./gradlew distNative
tasks.register<Copy>("distNative") {
	group = "distribution"
	description = "Compile en natif (Liberica NIK) et copie le binaire dans dist/"
	dependsOn("nativeCompile")
	from(layout.buildDirectory.dir("native/nativeCompile")) {
		include("arig-cipher*")
	}
	into(layout.projectDirectory.dir("dist"))
}

// Package avec jpackage pour usage local : ./gradlew distJpackage
tasks.register<Exec>("distJpackage") {
	group = "distribution"
	description = "Crée un installeur natif avec jpackage dans dist/"
	dependsOn("bootJar")

	val outputDir = layout.projectDirectory.dir("dist").asFile
	val os = System.getProperty("os.name").lowercase()
	val appVersion = version.toString().substringBefore("-")
	val iconFile = when {
		"mac" in os -> layout.projectDirectory.file("src/main/resources/org/arig/lucifer/fx/logo.icns").asFile
		"win" in os -> layout.projectDirectory.file("src/main/resources/org/arig/lucifer/fx/logo.ico").asFile
		else        -> layout.projectDirectory.file("src/main/resources/org/arig/lucifer/fx/logo.png").asFile
	}

	doFirst { outputDir.mkdirs() }

	commandLine(buildList {
		add("jpackage")
		add("--name"); add("ARIG Cipher")
		add("--app-version"); add(appVersion)
		add("--input"); add(layout.buildDirectory.dir("libs").get().asFile.absolutePath)
		add("--main-jar"); add("arig-cipher.jar")
		add("--dest"); add(outputDir.absolutePath)
		add("--java-options"); add("--enable-native-access=ALL-UNNAMED")
		if (iconFile != null) { add("--icon"); add(iconFile.absolutePath) }
	})
}