package com.yudiz.dynamodbdemo.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive
import com.amazonaws.regions.Region
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.google.gson.Gson
import com.yudiz.dynamodbdemo.data.PlayerDataModel
import com.yudiz.dynamodbdemo.util.DynamoDBHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ListVM(application: Application) : AndroidViewModel(application) {

    private val state = MutableLiveData<ListScreenState>(ListScreenState.Idle)
    fun state(): LiveData<ListScreenState> = state

    private val credentialsProvider =
        CognitoCachingCredentialsProvider(
            application.applicationContext, DynamoDBHelper.COGNITO_IDP_ID,
            DynamoDBHelper.COGNITO_IDP_REGION
        )

    private val dbClient =
        AmazonDynamoDBClient(credentialsProvider).apply {
            /**
             * Don't forget to mention the region for database client. If not, it defaults to US_EAST_1
             */
            setRegion(Region.getRegion(DynamoDBHelper.COGNITO_IDP_REGION))
        }

    private lateinit var table: Table

    private suspend fun getTable(): Table {
        if (::table.isInitialized)
            return table
        return suspendCoroutine { continuation ->
            table = Table.loadTable(dbClient, DynamoDBHelper.TABLE_NAME)
            continuation.resume(table)
        }
    }

    internal fun getAllPlayers() {
        execute {
            val players = arrayListOf<PlayerDataModel>()
            getTable().scan(
                ScanOperationConfig()
            ).allResults.forEach {
                players.add(Gson().fromJson(Document.toJson(it), PlayerDataModel::class.java))
            }
            state.postValue(ListScreenState.PlayerListFetched(players))
        }
    }

    internal fun deletePlayer(pos: Int, id: String) {
        execute {
            getTable().deleteItem(Primitive(id))
            state.postValue(ListScreenState.PlayerDeleted(pos))
        }
    }

    internal fun createPlayer(player: PlayerDataModel) {
        execute {
            getTable().putItem(Document.fromJson(Gson().toJson(player)))
            getAllPlayers()
        }
    }

    internal fun updatePlayer(player: PlayerDataModel) {
        execute {
           getTable().updateItem(
                getTable().getItem(Primitive(player.id)).apply {
                    putAll(Document.fromJson(Gson().toJson(player)))
                },
                Primitive(player.id), UpdateItemOperationConfig().apply {
                    returnValue = ReturnValue.ALL_NEW
                }
            )

            getAllPlayers()
        }
    }

    private fun execute(executionBlock: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                state.postValue(ListScreenState.Loading)
                executionBlock.invoke(this)
                /**
                 * delay is added because postValue is synchronized. (if not, posting idle state right after posting execution value will create issues)
                 */
                delay(500)
                state.postValue(ListScreenState.Idle)
            } catch (e: Exception) {
                state.postValue(ListScreenState.Error)
            }
        }
    }
}