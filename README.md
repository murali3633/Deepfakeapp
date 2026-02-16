# Deepfake Detection App 🕵️‍♂️🚫

An Android application designed to detect deepfake images using a deep learning model (Xception) directly on your device. 
 
## 🚀 Features
- **Dual Input Methods**: Import images from your **Gallery** or capture new ones using the **Camera**.
- **On-Device Analysis**: Uses **TensorFlow Lite** for fast, offline deepfake detection.
- **Instant Feedback**: Real-time classification with clear "Real" or "Fake" results.
- **Privacy First**: All processing happens locally on the phone; no images are uploaded to the cloud.

## 🛠️ Tech Stack
- **Language**: [Kotlin](https://kotlinlang.org/)
- **Platform**: Android SDK
- **Machine Learning**: [TensorFlow Lite](https://www.tensorflow.org/lite)
- **Model Architecture**: Xception Network (optimized for mobile)

## 📂 Project Structure
- `app/src/main/java`: Contains the Kotlin source code (`MainActivity`, `DeepfakeDetector`, etc.).
- `app/src/main/ml`: Stores the TFLite model (`celebs_Xception.tflite`).
- `app/src/main/res`: UI layouts and assets.

## ⚙️ Installation & Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/Deepfakeapp.git
   ```
2. **Open in Android Studio**:
   - File -> Open -> Select the cloned directory.
3. **Sync Gradle**: Allow Android Studio to download dependencies.
4. **Run the App**: Connect an Android device or use an emulator to run the application.

## 📝 Usage
1. Open the app.
2. Provide necessary permissions (Camera/Storage) if prompt.
3. Tap **Upload** to select an image from the gallery or take a photo.
4. Tap **Submit** to run the detection.
5. View the result displayed on the screen.

## 🤝 Contributing
Contributions are welcome! Please open an issue or submit a pull request for any improvements.

## 📄 License
[MIT License](LICENSE)
