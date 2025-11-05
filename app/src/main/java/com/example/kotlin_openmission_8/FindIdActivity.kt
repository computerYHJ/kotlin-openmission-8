package com.example.kotlin_openmission_8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_openmission_8.databinding.FindIdBinding

class FindIdActivity: AppCompatActivity() {

    private lateinit var binding: FindIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }
    }



}