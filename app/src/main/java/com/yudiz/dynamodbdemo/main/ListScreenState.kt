package com.yudiz.dynamodbdemo.main

import com.yudiz.dynamodbdemo.data.PlayerDataModel

sealed class ListScreenState {
    object Idle : ListScreenState()
    object Loading : ListScreenState()
    object Error : ListScreenState()
    data class PlayerListFetched(val playerList: List<PlayerDataModel>) : ListScreenState()
    data class PlayerDeleted(val pos: Int) : ListScreenState()
}