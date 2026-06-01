# 🌾 Pearl Millet Care – AI Disease Detection App

> Developed as an AI-assisted agricultural disease detection solution for Pearl Millet crop monitoring and field-level disease identification.

Pearl Millet Care is an AI-powered Android application designed to detect common pearl millet crop diseases directly from field images using Computer Vision and Deep Learning.

The application combines TensorFlow Lite, image preprocessing, responsive Android UI design, and bilingual support to provide a farmer-friendly crop disease detection experience.

---

# 📱 Project Overview

This application helps identify pearl millet diseases from crop images captured using a mobile device.

The system follows a multi-stage prediction workflow:

1. Detect whether the image contains a pearl millet crop
2. Validate image quality and crop visibility
3. Predict the disease category
4. Display disease details and management guidance

The app is designed for:

* Farmers
* Agricultural students
* Researchers
* Field-level crop monitoring

---

# ✨ Key Features

## 🔍 AI-Based Disease Detection

* TensorFlow Lite image classification
* Multi-stage prediction pipeline
* Confidence-based prediction handling
* Millet crop validation before prediction

## 🌱 Supported Diseases

* Blast Disease
* Downy Mildew
* Rust Disease
* Smut Disease
* Ergot Disease

## 📖 Disease Information System

Each disease includes:

* Symptoms
* Botanical name
* Favorable conditions
* Disease management guidance
* Treatment recommendations

## 🌐 Bilingual Support

* English
* Tamil

## 📷 Smart Image Validation

The system validates:

* Crop visibility
* Image clarity
* Millet crop presence
* Prediction confidence

## 🎨 Responsive Mobile UI

* Built using Jetpack Compose
* Responsive layouts for multiple screen sizes
* Tamil-aware typography scaling
* Tablet and foldable-friendly layouts

---

# 🛠️ Tech Stack

## Android Development

* Kotlin
* Jetpack Compose
* Material 3
* CameraX

## AI / Machine Learning

* TensorFlow Lite
* Python
* OpenCV

## Image Processing

* Image preprocessing pipeline
* Image quality analysis
* Confidence threshold validation

## Data & Storage

* Room Database
* DataStore Preferences

---

# 🧠 AI Prediction Workflow

```text
Capture Image
      ↓
Image Quality Validation
      ↓
Millet Crop Verification
      ↓
Disease Classification
      ↓
Confidence Analysis
      ↓
Result & Disease Management
```

---

# 📂 Project Structure

```text
millet-disease-detection-app/
│
├── README.md
├── screenshots/
├── android-app/
├── training-code/
├── inference/
├── model/
├── demo/
└── dataset-info/
```

---

# 📸 Application Screenshots

## Home Screen

<img width="720" height="1600" alt="WhatsApp Image 2026-05-23 at 7 13 07 PM (1)" src="https://github.com/user-attachments/assets/f24b4ded-d401-4bfe-a1f3-e45a61eb2ece" />


## Camera Scan Screen

*Add screenshot here*

## Disease Prediction Result

*Add screenshot here*

## Tamil Interface

*Add screenshot here*

## Disease Management Screen

*Add screenshot here*

---

# 👨‍💻 Role & Contribution

Designed and developed the complete end-to-end mobile application, including:

* Android application development
* TensorFlow Lite integration
* AI prediction workflow
* Image preprocessing pipeline
* Responsive Jetpack Compose UI
* Bilingual English/Tamil interface
* Disease information and management system
* Multi-stage crop validation workflow

---

# 🚀 Installation

## Clone Repository

```bash
git clone https://github.com/vinitharockiasamy/millet-disease-detection-app.git
```

## Open Project

1. Open Android Studio
2. Select the `android-app` folder
3. Sync Gradle files

## Run Application

1. Connect Android device or emulator
2. Click Run ▶️

---

# 📦 Current Capabilities

* Offline AI prediction
* Mobile disease detection
* Multi-image prediction support
* Local disease information system
* Prediction history support
* Responsive mobile UI
* English and Tamil support

---

# 🧪 Testing

The application has been tested for:

* Camera workflow
* Disease prediction flow
* Responsive layouts
* Tamil UI rendering
* Image validation handling
* Navigation stability
* Offline functionality

---

# 🎯 Target Users

* Farmers
* Agricultural students
* Researchers
* Agricultural extension workers

---

# 🔒 Offline Support

The application works completely offline after installation.

No internet connection is required for disease detection and prediction.

---

# 📌 Future Improvements

* Additional crop support
* More disease classes
* Remote model update support
* Advanced analytics dashboard
* Voice assistance integration
* Expanded multilingual support

---

# 👨‍💻 Developer

Vinith Arockiasamy

Project Engineer | AI & Mobile Application Development

GitHub:
https://github.com/vinitharockiasamy

LinkedIn:
*Add LinkedIn profile here*

---

# 🙏 Acknowledgements

* TensorFlow Lite
* Android Jetpack Compose
* OpenCV
* Agricultural research references
* Open-source Android community
