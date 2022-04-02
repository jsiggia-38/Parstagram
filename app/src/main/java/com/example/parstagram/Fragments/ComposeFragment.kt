package com.example.parstagram.Fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [ComposeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var ivPreview: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPreview = view.findViewById(R.id.imageView)

        view.findViewById<Button>(R.id.buttonSubmit).setOnClickListener {
            val description = view.findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            if(photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Toast.makeText(requireContext(), "photo is null", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.buttonTakePicture).setOnClickListener {
            onLaunchCamera()

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if(resultCode == AppCompatActivity.RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                ivPreview.setImageBitmap(takenImage)
            } else {
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }

    }



    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = getPhotoFileUri(photoFileName)

        if(photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider",photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if(intent.resolveActivity(requireContext().packageManager) != null) {
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }



    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            MainActivity.TAG
        )

        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun submitPost(description: String, user: ParseUser, file: File) {
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { e ->
            if(e != null) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "error saving picture", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "photo was successfully saved", Toast.LENGTH_SHORT).show()
            }
        }

    }




}