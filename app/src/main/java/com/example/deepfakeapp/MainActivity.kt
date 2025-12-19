package com.example.deepfakeapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.deepfakeapp.deepfakedetector.DeepfakeDetector
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var btnUpload: Button
    private lateinit var btnSubmit: Button
    private lateinit var resultText: TextView
    private lateinit var deepfakeDetector: DeepfakeDetector
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagePreview = findViewById(R.id.imagePreview)
        btnUpload = findViewById(R.id.btnUpload)
        btnSubmit = findViewById(R.id.btnSubmit)
        resultText = findViewById(R.id.txtResult)

        // Initialize TensorFlow Lite model
        deepfakeDetector = DeepfakeDetector(this)

        btnUpload.setOnClickListener {
            showImagePicker()
        }

        btnSubmit.setOnClickListener {
            if (imageUri != null) {
                // Show a toast when the image is submitted
                Toast.makeText(this, "Image submitted successfully!", Toast.LENGTH_SHORT).show()

                processAndDetectDeepfake(imageUri!!)
            } else {
                Toast.makeText(this, "Please upload an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePicker() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image")

        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
                2 -> {} // Do nothing
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imagePreview.setImageURI(imageUri)
            btnSubmit.visibility = View.VISIBLE
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imagePreview.setImageURI(imageUri)
            btnSubmit.visibility = View.VISIBLE
        }
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile("IMG_$timestamp", ".jpg", storageDir)
    }

    private fun processAndDetectDeepfake(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

            // Preprocess the image
            val inputArray = preprocessImage(bitmap)

            // Run deepfake detection
            val output = deepfakeDetector.predictFace(inputArray)

            // Fix: Ensure output[0] is accessed correctly
            val isFake = output > 0.5  // Assuming 0 = real, 1 = fake

            // Show result
            resultText.text = if (isFake) "⚠️ Fake Face Detected!" else "✅ Real Face Detected!"
            resultText.visibility = View.VISIBLE

        } catch (e: Exception) {
            Toast.makeText(this, "Error processing image!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, true)
        val inputTensor = Array(1) { Array(299) { Array(299) { FloatArray(3) } } }

        for (y in 0 until 299) {
            for (x in 0 until 299) {
                val pixel = resizedBitmap.getPixel(x, y)

                inputTensor[0][y][x][0] = ((pixel shr 16) and 0xFF) / 255.0f
                inputTensor[0][y][x][1] = ((pixel shr 8) and 0xFF) / 255.0f
                inputTensor[0][y][x][2] = (pixel and 0xFF) / 255.0f
            }
        }
        return inputTensor
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::deepfakeDetector.isInitialized) {
            deepfakeDetector.close()
        }
    }
}
