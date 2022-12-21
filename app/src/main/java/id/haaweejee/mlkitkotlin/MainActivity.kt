package id.haaweejee.mlkitkotlin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import id.haaweejee.mlkitkotlin.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan izin akses.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.buttonCamera.setOnClickListener { startCamera() }
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentX.launch(intent)
    }

    private val launcherIntentX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = BitmapFactory.decodeFile(myFile.path)
            runTextRecognition(result)

            binding.imageView.setImageBitmap(result)
        }
    }

    private fun runTextRecognition(imageBitmap: Bitmap) {
        // Replace with code from the codelab to run text recognition.
        val image = InputImage.fromBitmap(imageBitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        binding.buttonCamera.isEnabled = false
        recognizer.process(image)
            .addOnSuccessListener { texts ->
                binding.buttonCamera.isEnabled = true
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                binding.buttonCamera.isEnabled = true
                e.printStackTrace()
            }
    }

    private fun processTextRecognitionResult(texts: Text) {
        // Replace with code from the codelab to process the text recognition result.
        val listData : ArrayList<String> = arrayListOf()
        for (block in texts.textBlocks) {
            val blockText = block.text
            listData.add(blockText)
        }
        Log.d("DATA", listData.toString())

        binding.tvData.text = listData.toString()
    }


    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}