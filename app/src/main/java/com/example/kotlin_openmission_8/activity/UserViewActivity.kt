package com.example.kotlin_openmission_8.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_openmission_8.R
import com.example.kotlin_openmission_8.model.User
import com.example.kotlin_openmission_8.model.UserRepository
import com.example.kotlin_openmission_8.databinding.CheckWorkoutTimeBinding
import com.example.kotlin_openmission_8.databinding.UserviewBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class UserViewActivity : AppCompatActivity() {
    private lateinit var binding: UserviewBinding

    private var user: User = User()

    private var id: String = ""
    private var currentDate = LocalDate.now()
    private var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var monthFormatter = DateTimeFormatter.ofPattern("MM월")
    private var currentMonth = currentDate.format(monthFormatter)

    private var timerFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("userID")!!

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            user = UserRepository.getLoginUser(id)!!
            setting(user)
        }
    }

//    override fun onRestart() {
//        super.onRestart()
//        setting(user)
//    }

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
            val baseAnim = AnimationUtils.loadAnimation(this@UserViewActivity, R.anim.card_animation)

            val animOne = baseAnim.cloneWithOffset(0)
            binding.one.startAnimation(animOne)

            val animTwo = baseAnim.cloneWithOffset(200)
            binding.two.startAnimation(animTwo)

            val animThree = baseAnim.cloneWithOffset(380)
            binding.three.startAnimation(animThree)
        })
    }

    private fun setting(user: User) {
        lifecycleScope.launch {
            uiSetting(user)
            runAnimation(user.startWorkout, user.endWorkout)
            binding.workoutDaysSelect.setOnClickListener {
                val intent = Intent(this@UserViewActivity, DurationActivity::class.java)
                intent.putExtra("userID", user.userID)
                startActivity(intent)
            }
            binding.userViewWorkoutStartBtn.setOnClickListener { lifecycleScope.launch{ timer() } }
            binding.userViewCheckTime.setOnClickListener { lifecycleScope.launch { checkWorkoutTime() } }
        }
        binding.userViewNowMonthText.text = currentMonth

    }

    private fun uiSetting(user: User) {
        binding.mainViewUserName.text = user.userName
        binding.countUserName.text = user.userName
        binding.userViewWorkoutCountTextView.text = user.workoutCount.toString()
        binding.userViewMonthGoalTextView.text = "${(user.monthGoal - user.workoutCount)}회 남았습니다."
        val endWorkOut = user.endWorkout
        if (!endWorkOut.isEmpty()) {
            val parsedDate = LocalDate.parse(endWorkOut, formatter)
            val viewEndWorkout = parsedDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            binding.userViewWorkoutDurationTextView.text = viewEndWorkout
        }
    }

    private suspend fun timer() = with(binding) {
        when(timerFlag){
            false -> startTimer()
            true -> stopTimer()
        }
    }

    private fun startTimer() = with(binding) {
        AlertDialog.Builder(this@UserViewActivity, R.style.CustomAlertDialogTheme)
            .setTitle("운동 시작하기")
            .setMessage("오늘의 운동을 시작하시겠습니까?")
            .setPositiveButton("시작하기") { po, p1 ->
                userViewChrono.base = SystemClock.elapsedRealtime()
                userViewChrono.start()
                timerFlag = true
            }
            .show()
    }

    private suspend fun stopTimer() = with(binding) {
        AlertDialog.Builder(this@UserViewActivity, R.style.CustomAlertDialogTheme)
            .setTitle("운동 종료하기")
            .setMessage("오늘의 운동을 종료하시겠습니까?")
            .setPositiveButton("종료하기") { po, p1 ->
                userViewChrono.stop()
                timerFlag = false
                userViewWorkoutCountTextView.text = (user.workoutCount + 1).toString()
                userViewMonthGoalTextView.text = "${(user.monthGoal - user.workoutCount) - 1}회 남았습니다."
                val detail = mapOf<String,String>("workoutCount" to "${user.workoutCount + 1}")
                lifecycleScope.launch { UserRepository.getUpdateUser(id, detail, 2)}
            }
            .show()
    }

    private suspend fun checkWorkoutTime() {
        val dia = CheckWorkoutTimeBinding.inflate(layoutInflater)
        val dlg = AlertDialog.Builder(this@UserViewActivity, R.style.CustomAlertDialogTheme).create()
        dlg.setView(dia.root)

        dia.checkStartTime.text = user.startTime
        dia.checkEndTime.text = user.endTime

        dlg.show()

        dlg.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun Animation.cloneWithOffset(offset: Long): Animation {
        val newAnim = AnimationUtils.loadAnimation(this@UserViewActivity, R.anim.card_animation)
        newAnim.startOffset = offset
        return newAnim
    }

}