package com.example.kotlin_openmission_8

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_openmission_8.databinding.UserviewBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class UserViewActivity : AppCompatActivity(){
    private lateinit var binding: UserviewBinding

    private var user: User = User()

    private var id: String = ""
    private var currentDate = LocalDate.now()
    private var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var monthFormatter = DateTimeFormatter.ofPattern("MM월")
    private var currentMonth = currentDate.format(monthFormatter)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("userID")!!

        setting()
    }

    private fun percent(startWorkout: String, endWorkout: String): Int {

        val startDate = LocalDate.parse(startWorkout, formatter)
        val futureDate = LocalDate.parse(endWorkout, formatter)

        val totalDays = ChronoUnit.DAYS.between(startDate, futureDate)
        val elapsedDays = ChronoUnit.DAYS.between(startDate, currentDate)

        val progressPercentage = elapsedDays.toDouble() / totalDays * 100
        var result = progressPercentage.toInt()
        if (result >= 100) result = 100
        return result
    }

    private fun runAnimation(start: String, end: String) {
        binding.progressBar.post({
            // Firestore 데이터를 이용해 progressBar 업데이트
            binding.progressBar.setProgressWithAnimation(
                percent(start, end),
                1500
            )
            // 애니메이션 실행
            val animOne = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_animation)
            animOne.setStartOffset(0)
            binding.one.startAnimation(animOne)

            val animTwo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_animation)
            animTwo.setStartOffset(200)
            binding.two.startAnimation(animTwo)

            val animThree = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_animation)
            animThree.setStartOffset(380)
            binding.three.startAnimation(animThree)
        })
    }

    private fun setting(){
        lifecycleScope.launch {
            user = UserRepository.getLoginUser(id)!!
            uiSetting(user)
            runAnimation(user.startWorkout, user.endWorkout)
            binding.workoutDaysSelect.setOnClickListener {
                val intent = Intent(this@UserViewActivity, DurationActivity::class.java)
                intent.putExtra("userID", user.userID)
                startActivity(intent)
            }
        }
        binding.userViewNowMonthText.text = currentMonth
    }

    private fun uiSetting(user: User){
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


}