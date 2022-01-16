package com.bassem.clinic_userapp.ui.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.ProfileFragmentBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Profile() : Fragment(R.layout.profile_fragment) {
    var _binding: ProfileFragmentBinding? = null
    val binding get() = _binding
    private lateinit var db: FirebaseFirestore
    var imageuri: Uri? = null
    var imageLink: String? = null
    private val pickImage = 100
    var regesited_date: String? = null
    var fileName: String? = null
    var id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences =
            activity!!.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences.getString("id", "noo")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GettingData()
       // GetProfileImage()
       GetProfileLink()
        binding?.pickImage?.setOnClickListener {
            PickImage()

        }
    }

    fun GettingData() {

        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id!!).addSnapshotListener { value, error ->

            if (error != null) {
                println("Firebase error ${error.message}")
            } else {
                binding!!.fullnameInfo.text = value?.getString("fullname")
                binding!!.ageInfo.text = value?.getDouble("age")?.toInt().toString()
                binding!!.jobInfo.text = value?.getString("job")
                binding!!.complainInfo.text = value?.getString("complain")
                binding!!.mailInfo.text = value?.getString("mail")

                binding!!.phoneInfo.text = value?.getString("phone")

                binding!!.notesInfo.text = value?.getString("note")
                val sex = value?.getString("sex")
                binding!!.sexInfo.text = sex


            }
        }
    }

    fun PickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickImage)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            imageuri = data?.data
            binding?.profileimage?.setImageURI(imageuri)
            UploadtoFirebase(imageuri!!)
        }
    }

    fun UploadtoFirebase(image: Uri) {
        var progressIndicator = ProgressDialog.show(context, "Uploading..", "please wait")
        fileName = UUID.randomUUID().toString() + ".jpg"
        val storage = FirebaseStorage.getInstance().reference.child("profile/$fileName")
        storage.putFile(image).addOnSuccessListener { it ->
            progressIndicator.dismiss()
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener {

                imageLink = it.toString()
                AddPhotoNametoFireStore(imageLink!!)
            }


        }


    }

    fun AddPhotoNametoFireStore(link: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id!!).update("image", link).addOnCompleteListener {


        }
    }

    fun GetProfileLink()  {
        var link: String? = null
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id!!).addSnapshotListener { value, error ->
            link = value?.getString("image")!!
            if (link!=null){
                GetProfileImage(link!!)
            }

        }



    }
    fun GetProfileImage(link: String){
        val imagePath= binding?.profileimage
        Glide.with(this).load(link).into(imagePath!!)
    }


}