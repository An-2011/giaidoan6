        plugins {
            id("com.android.application")
            id("org.jetbrains.kotlin.android")
            id("com.google.gms.google-services")
        }

        android {
            namespace = "com.example.ungdungcoxuongkhop"
            compileSdk = 34

            defaultConfig {
                applicationId = "com.example.ungdungcoxuongkhop"
                minSdk = 23
                targetSdk = 34
                versionCode = 1
                versionName = "1.0"

                multiDexEnabled = true
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            buildFeatures {
                viewBinding = true
            }

            tasks.withType<JavaCompile>().configureEach {
                options.compilerArgs.add("-Xlint:deprecation")
            }

            lint {
                abortOnError = false // Không dừng build nếu có lỗi nhỏ
                checkReleaseBuilds = true
                warningsAsErrors = false // Không coi cảnh báo là lỗi
            }
        }

        dependencies {
            implementation("androidx.appcompat:appcompat:1.6.1")
            implementation("androidx.core:core-ktx:1.12.0") // Cập nhật bản mới nhất
            implementation("androidx.multidex:multidex:2.0.1")

            implementation("com.google.android.material:material:1.10.0")
            implementation("androidx.activity:activity-ktx:1.8.0")
            implementation("androidx.constraintlayout:constraintlayout:2.1.4")
            implementation("androidx.cardview:cardview:1.0.0")
            implementation("androidx.recyclerview:recyclerview:1.3.1")

            // Firebase dependencies (Updated)
            implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
            implementation("com.google.firebase:firebase-messaging")
            implementation("com.google.firebase:firebase-analytics")
            implementation("com.google.firebase:firebase-auth")
            implementation("com.google.firebase:firebase-firestore")

            // Networking libraries (Updated versions)
            implementation("com.android.volley:volley:1.2.1")
            implementation("com.squareup.okhttp3:okhttp:4.11.0")
            implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")
            implementation("com.google.code.gson:gson:2.10.1")

            // Image loading libraries (Updated)
            implementation("com.github.bumptech.glide:glide:4.16.0")
            annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

            // Cập nhật thư viện play-services-auth phiên bản mới nhất
            implementation ("com.google.android.gms:play-services-auth:20.7.0")

            // Thư viện biểu đồ MPAndroidChart
            implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
        }
