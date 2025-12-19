package com.example.deepfakeapp.deepfakedetector

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class DeepfakeDetector(context: Context) {
    private var interpreter: Interpreter

    init {
        val model = loadModelFile(context)
        interpreter = Interpreter(model)
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd("celebs_Xception.tflite") // Ensure file is in 'assets/'
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
    }

    fun predictFace(inputData: Array<Array<Array<FloatArray>>>): Float {
        val outputData = Array(1) { FloatArray(1) } // Updated to match [1,1] shape
        interpreter.run(inputData, outputData)
        return outputData[0][0] // Extracting the single value
    }

    // Release Interpreter when no longer needed
    fun close() {
        interpreter.close()
    }
}
