package ru.te3ka.homework18

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.te3ka.homework18.databinding.FragmentCreateBinding
import ru.te3ka.homework18.model.Attraction
import ru.te3ka.homework18.viewmodel.AttractionViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val PHOTO_PREFIX = "yyyy-MM-dd-HH-mm-ss"

class CreateFragment : Fragment(), SurfaceHolder.Callback {

    private lateinit var attractionViewModel: AttractionViewModel
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentPhotoPath: String
    private var photoFile: File? = null

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null

    private val PERMISSION_REQUEST_CAMERA = 101
    private val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 102

    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attractionViewModel = ViewModelProvider(this).get(AttractionViewModel::class.java)

        surfaceView = binding.cameraPreview
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)

        binding.buttonCreatePhotoFromCamera.setOnClickListener {
            takePhoto()
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider!!)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        preview = Preview.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        preview?.setSurfaceProvider { request ->
            val surface = surfaceHolder.surface
            if (surface != null && surface.isValid) {
                request.provideSurface(surface, ContextCompat.getMainExecutor(requireContext())) { result ->
                    Log.d("CreateFragment", "Surface request result: $result")
                }
            }
        }

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(requireActivity().windowManager.defaultDisplay.rotation)
            .build()

        cameraProvider.unbindAll()

        try {
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CreateFragment", "Use case binding failed", e)
        }
    }

    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        } else {
            val photoFile = createImageFile()

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture?.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        requireActivity().runOnUiThread {
                            Glide.with(this@CreateFragment).load(photoFile).into(binding.imageAttractionPhoto)
                            galleryAddPic(photoFile)

                            val attraction = Attraction(
                                photoPath = photoFile.absolutePath,
                                dateTaken = SimpleDateFormat(PHOTO_PREFIX, Locale.getDefault()).format(Date())
                            )
                            attractionViewModel.insert(attraction)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CreateFragment", "Photo capture failed: ${exception.message}", exception)
                    }
                })
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(PHOTO_PREFIX, Locale.getDefault()).format(Date())
        val storageDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Attractions"
        )
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val photoFile = File(storageDir, "JPEG_${timeStamp}.jpg")
        currentPhotoPath = photoFile.absolutePath
        return photoFile
    }

    private fun galleryAddPic(photoFile: File) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val contentUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            mediaScanIntent.data = contentUri
            requireContext().sendBroadcast(mediaScanIntent)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraProvider?.unbindAll()
    }
}
