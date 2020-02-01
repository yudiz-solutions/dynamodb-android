package com.yudiz.dynamodbdemo.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.yudiz.dynamodbdemo.BR
import com.yudiz.dynamodbdemo.R
import com.yudiz.dynamodbdemo.data.PlayerDataModel
import com.yudiz.dynamodbdemo.databinding.ActListBinding
import com.yudiz.dynamodbdemo.util.RvDividerDecoration


class ListScreen : AppCompatActivity(), PlayerListAdapter.PlayerClickListener,
    View.OnClickListener {

    private lateinit var playerAdapter: PlayerListAdapter
    private lateinit var binding: ActListBinding
    private val vm: ListVM by viewModels()
    private val ADD_PLAYER_REQ = 1
    private val EDIT_PLAYER_REQ = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.act_list)
        binding.setVariable(BR.click, this)

        binding.rv.let { rv ->
            playerAdapter = PlayerListAdapter(mutableListOf(), this)
            rv.adapter = playerAdapter
            rv.addItemDecoration(RvDividerDecoration(this).setSpace(R.dimen.list_gap))
        }

        vm.state().observe(this, Observer {
            when (it) {
                is ListScreenState.Idle -> hideProgress()
                is ListScreenState.Loading -> showProgress()
                is ListScreenState.Error -> {
                    hideProgress()
                    Toast.makeText(this, "Error executing operation", Toast.LENGTH_SHORT).show()
                }
                is ListScreenState.PlayerListFetched -> playerAdapter.replaceData(it.playerList)
                is ListScreenState.PlayerDeleted -> playerAdapter.deleteItemAt(it.pos)
            }
        })

        vm.getAllPlayers()
    }

    private fun showProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
    }

    override fun onDeleteClicked(pos: Int, id: String) {
        vm.deletePlayer(pos, id)
    }

    override fun onPlayerClicked(player: PlayerDataModel) {
        startActivityForResult(
            Intent(this, AddEditPlayerScreen::class.java).putExtra(
                "user",
                player
            ), EDIT_PLAYER_REQ
        )
    }

    override fun onClick(p0: View) {
        startActivityForResult(Intent(this, AddEditPlayerScreen::class.java), ADD_PLAYER_REQ)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_PLAYER_REQ ->
                    vm.createPlayer(data?.getSerializableExtra("user") as PlayerDataModel)
                EDIT_PLAYER_REQ ->
                    vm.updatePlayer(data?.getSerializableExtra("user") as PlayerDataModel)
            }
        }
    }
}
