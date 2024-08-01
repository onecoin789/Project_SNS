package com.example.project_sns.ui.view.main.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage

class MyProfileMakePostFragment : Fragment() {

    private var _binding: FragmentMyProfileMakePostBinding? = null
    private val binding get() = _binding!!

    private lateinit var uri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileMakePostBinding.inflate(inflater, container, false)

        initView()
        getPhoto()
//        binding.btnMakeConfirm.setOnClickListener {
//            uploadPhoto(uri)
//        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.ivMakeBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getPhoto() {

        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        uri = result.data?.data!!
                        binding.ivMakePhoto.clipToOutline = true
                        binding.ivMakePhoto.setImageURI(uri)
                    }
                }
            }

        binding.clMakePhotoFrame.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            registerForActivityResult.launch(intent)
        }
    }

//    private fun uploadPhoto(uri: Uri?) {
//
//        if (uri != null) {
//            val storage = Firebase.storage
//            val storageRef = storage.getReference("${auth.currentUser?.uid}")
//            val fileName = Timestamp.now()
//            val mountainsRef = storageRef.child("${fileName}.png")
//            val downloadUri = storageRef.downloadUrl
//
//
//
//            val uploadTask = mountainsRef.putFile(uri)
//            uploadTask.addOnSuccessListener { _ ->
//                Toast.makeText(requireContext(), "성공", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener {
//                Toast.makeText(requireContext(), "실패", Toast.LENGTH_SHORT).show()
//            }
//        }

}