# TrainBot Android App

TrainBot is an Android application that uses on-device machine learning to classify images using TensorFlow Lite. The project is built with Kotlin and Gradle, and is structured as a standard Android Studio project.

## Features
- Image classification using a custom TensorFlow Lite model (`mobilenet_v2.tflite`)
- GPU acceleration with TensorFlow Lite GPU delegate
- Modern Android development with Kotlin DSL and AndroidX

## Project Structure
- `app/` - Main Android application module
  - `src/main/java/com/example/trainbot/` - App source code (see `ImageClassifier.kt` for ML logic)
  - `build.gradle.kts` - Module build configuration
  - `proguard-rules.pro` - ProGuard rules
- `gradle/` - Gradle wrapper and version catalog
- `build.gradle.kts` - Project-level Gradle configuration
- `settings.gradle.kts` - Project settings
- `.idea/` - Android Studio project configuration (not required for building)

## Dependencies
- TensorFlow Lite: `org.tensorflow:tensorflow-lite:2.14.0`
- TensorFlow Lite Support: `org.tensorflow:tensorflow-lite-support:0.4.4`
- TensorFlow Lite GPU: `org.tensorflow:tensorflow-lite-gpu:2.14.0`
- TensorFlow Lite Select TF Ops: `org.tensorflow:tensorflow-lite-select-tf-ops:2.14.0`
- AndroidX libraries
- Material Components

## How to Build
1. Clone the repository:
   ```sh
   git clone https://github.com/TrainBotApp/app-Android.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and build the project.
4. Place your TensorFlow Lite model (`mobilenet_v2.tflite`) in `app/src/main/assets/`.
5. Run the app on an emulator or device.

## Notes
- **Do not commit build artifacts or APKs.** The `.gitignore` is set up to prevent this.
- The app uses a default image size of 224x224 for classification.
- Labels for classification results should be placed in the assets folder if available.

## License
This project is licensed under the Apache License 2.0.
