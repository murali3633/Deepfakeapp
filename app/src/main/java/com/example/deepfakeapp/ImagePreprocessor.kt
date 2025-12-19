import android.graphics.Bitmap
import android.graphics.Matrix

fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
    // Resize the image using a Matrix for better performance
    val resizedBitmap = Bitmap.createBitmap(299, 299, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(resizedBitmap)
    val matrix = Matrix()
    matrix.postScale(299f / bitmap.width, 299f / bitmap.height)
    canvas.drawBitmap(bitmap, matrix, null)

    // Prepare the input tensor [1, 299, 299, 3]
    val inputTensor = Array(1) { Array(299) { Array(299) { FloatArray(3) } } }

    for (y in 0 until 299) {
        for (x in 0 until 299) {
            val pixel = resizedBitmap.getPixel(x, y)

            // Extract RGB and normalize
            inputTensor[0][y][x][0] = ((pixel shr 16) and 0xFF) / 255.0f  // Red
            inputTensor[0][y][x][1] = ((pixel shr 8) and 0xFF) / 255.0f   // Green
            inputTensor[0][y][x][2] = (pixel and 0xFF) / 255.0f          // Blue
        }
    }

    return inputTensor
}
