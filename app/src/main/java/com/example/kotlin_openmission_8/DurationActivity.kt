package com.example.kotlin_openmission_8

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_openmission_8.databinding.SelectDayBinding
import com.example.kotlin_openmission_8.databinding.SelectTimeBinding
import com.example.kotlin_openmission_8.databinding.WorkoutDurationBinding
import com.example.kotlin_openmission_8.validator.InputValidator
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class DurationActivity : AppCompatActivity() {
    private lateinit var binding: WorkoutDurationBinding

    private var user: User = User()

    private var id: String = ""

    private var detail: MutableMap<String, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkoutDurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }

        id = intent.getStringExtra("userID")!!

        lifecycleScope.launch {
            user = UserRepository.getLoginUser(id)!!
            detail["startWorkout"] = user.startWorkout
            detail["endWorkout"] = user.endWorkout
            detail["startTime"] = user.startTime
            detail["endTime"] = user.endTime

            defaultUiSetting()

            workoutInfoSetting()

            binding.workoutDurationBtn.setOnClickListener {
                lifecycleScope.launch { UserRepository.getUpdateUser(id, detail); finish() }
            }
        }
    }

    private fun defaultUiSetting() = with(binding) {
        displayMembershipInfo(user.startWorkout, workoutStartDaysTextView)
        displayMembershipInfo(user.endWorkout, workoutEndDaysTextView)
        displayMembershipInfo(user.startTime, workoutStartTimeTextView)
        displayMembershipInfo(user.endTime, workoutEndTimeTextView)
    }

    private fun displayMembershipInfo(input: String, textView: TextView) = with(binding) {
        textView.text = input
    }

    private fun workoutInfoSetting() = with(binding) {
        workoutStartDaysBtn.setOnClickListener {
            selectDay(binding.workoutStartDaysTextView, 1) { day -> detail["startWorkout"] = day }
        }
        workoutEndDaysBtn.setOnClickListener {
            selectDay(binding.workoutEndDaysTextView, 2) { day -> detail["endWorkout"] = day }
        }

        workoutStartTimeBtn.setOnClickListener {
            selectTime(binding.workoutStartTimeTextView, 1) { time -> detail["startTime"] = time }
        }

        workoutEndTimeBtn.setOnClickListener {
            selectTime(binding.workoutEndTimeTextView, 2) { time -> detail["endTime"] = time }
        }
    }

    private fun selectDay(textView: TextView, step: Int, onSelected: (String) -> Unit) {
        var dia = SelectDayBinding.inflate(layoutInflater)
        var dlg = BottomSheetDialog(this@DurationActivity, R.style.CustomBottomSheetDialogTheme)
        dlg.setContentView(dia.root)

        dia.selectYear.apply { minValue = 2010; maxValue = 2100; value = 2025 }

        dia.selectMonth.apply { minValue = 1; maxValue = 12; value = 11 }

        dia.selectDays.apply { minValue = 1; maxValue = 31; value = 12 }

        dia.selectYear.displayedValues =
            getDisplayDefault(dia.selectYear.minValue, dia.selectYear.maxValue, "년")
        dia.selectMonth.displayedValues =
            getDisplayDefault(dia.selectMonth.minValue, dia.selectMonth.maxValue, "월")
        dia.selectDays.displayedValues =
            getDisplayDefault(dia.selectDays.minValue, dia.selectDays.maxValue, "일")

        dia.selectDaysBtn.setOnClickListener { getSelectDay(dia, dlg, textView, step, onSelected) }

        dlg.show()
    }

    private fun getSelectDay(
        dia: SelectDayBinding,
        dlg: BottomSheetDialog,
        textView: TextView,
        step: Int,
        onSelected: (String) -> Unit
    ) {
        var select: String = ""
        var flag = true
        val year = dia.selectYear.value
        val month = dia.selectMonth.value
        val day = dia.selectDays.value

        select = String.format("%04d-%02d-%02d", year, month, day)

        if (step == 2 && InputValidator.validatorDay(user, select)) {
            flag = false; wrongEndWorkout()
        }

        if (flag) {
            binding.workoutStartDaysWarringTextView.visibility = View.INVISIBLE
            binding.workoutEndDaysWarringTextView.visibility = View.INVISIBLE
            textView.setTextColor(Color.parseColor("#000000"))
            textView.text = select
            dlg.dismiss()
            onSelected(select)
        }
    }

    private fun selectTime(textView: TextView, step: Int, onSelected: (String) -> Unit) {
        var dia = SelectTimeBinding.inflate(layoutInflater)
        var dlg = BottomSheetDialog(this@DurationActivity, R.style.CustomBottomSheetDialogTheme)
        dlg.setContentView(dia.root)

        dia.selectTimeBtn.setOnClickListener { getSelectTime(dia, dlg, textView, step, onSelected) }

        dlg.show()
    }

    private fun getSelectTime(
        dia: SelectTimeBinding,
        dlg: BottomSheetDialog,
        textView: TextView,
        step: Int,
        onSelected: (String) -> Unit
    ) {
        var select: String = ""
        val hour = dia.selectTime.hour
        val min = dia.selectTime.minute

        if (hour > 12) select = String.format("오후 %d시 %d분", (hour - 12), min)
        else select = String.format("오전 %d시 %d분", hour, min)

        textView.setTextColor(Color.parseColor("#000000"))
        textView.text = select
        dlg.dismiss()
        onSelected(select)
    }

    private fun wrongEndWorkout() {
        binding.workoutEndDaysWarringTextView.visibility = View.VISIBLE
        binding.workoutEndDaysWarringTextView.text = "시작 날짜보다 이전 날짜를 선택했습니다."
        binding.workoutEndDaysTextView.text = "버튼을 눌러 날짜를 선택하세요"
        binding.workoutEndDaysTextView.setTextColor(Color.GRAY);
    }

    private fun getDisplayDefault(start: Int, end: Int, suffix: String): Array<String> {
        val displayValues = Array<String>(end - start + 1, { "" })
        for (i in displayValues.indices) {
            displayValues[i] = (start + i).toString() + suffix
        }
        return displayValues
    }
}