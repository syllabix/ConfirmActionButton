package com.crushonly.sample

import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.crushonly.confirmactionbutton.ConfirmActionButton
import com.crushonly.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val confirmActionMode = ObservableField<ConfirmActionButton.Mode>()
    val confirmButtonMessage = ObservableField<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding?.setVariable(BR.activity, this)
        confirmActionMode.set(ConfirmActionButton.Mode.READY)
    }

    fun handleClick() {
        confirmButtonMessage.set("Confirm Action?")
        confirmActionMode.set(ConfirmActionButton.Mode.CONFIRMING)
    }

    fun handleConfirmClick() {
        confirmButtonMessage.set("")
        confirmActionMode.set(ConfirmActionButton.Mode.READY)
    }
}
