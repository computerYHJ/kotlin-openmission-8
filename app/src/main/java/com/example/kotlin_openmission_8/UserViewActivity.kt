package com.example.kotlin_openmission_8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_openmission_8.databinding.UserviewBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserViewActivity : AppCompatActivity(){
    private lateinit var binding: UserviewBinding

    private var user: User = User()
    private var currentDate = LocalDate.now()
    private var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var monthFormatter = DateTimeFormatter.ofPattern("MM월")
    private var currentMonth = currentDate.format(monthFormatter)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id: String = intent.getStringExtra("userID")!!

        lifecycleScope.launch {
            user = UserRepository.getLoginUser(id)!!
            binding.mainViewUserName.text = user.userName
            binding.countUserName.text = user.userName
            binding.userViewWorkoutCountTextView.text = user.workoutCount.toString()
            binding.userViewMonthGoalTextView.text = "${(user.monthGoal - user.workoutCount)}회 남았습니다."
            val endWorkOut = user.endWorkout
            if(!endWorkOut.isEmpty()){
                val parsedDate = LocalDate.parse(endWorkOut, formatter)
                val viewEndWorkout = parsedDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
                binding.userViewWorkoutDurationTextView.text = viewEndWorkout
            }

        }

        binding.userViewNowMonthText.text = currentMonth


    }
}