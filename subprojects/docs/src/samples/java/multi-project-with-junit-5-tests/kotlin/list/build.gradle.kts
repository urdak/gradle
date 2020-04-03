plugins {
    `java-library`
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
