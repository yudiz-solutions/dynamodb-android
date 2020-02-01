package com.yudiz.dynamodbdemo.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yudiz.dynamodbdemo.BR
import com.yudiz.dynamodbdemo.R
import com.yudiz.dynamodbdemo.data.PlayerDataModel
import com.yudiz.dynamodbdemo.databinding.ActAddEditPlayerBinding

class AddEditPlayerScreen : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActAddEditPlayerBinding
    private lateinit var player: PlayerDataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.act_add_edit_player)
        binding.setVariable(BR.click, this)

        player = intent.getSerializableExtra("user") as? PlayerDataModel ?: PlayerDataModel()

        binding.setVariable(BR.player, player)
    }

    override fun onClick(p0: View) {
        setResult(
            Activity.RESULT_OK, Intent().apply {
                player.id.let {
                    if (it.isNullOrEmpty())
                        player.id = System.currentTimeMillis().toString()
                }
                putExtra("user", player)
            }
        )
        finish()
    }
}
