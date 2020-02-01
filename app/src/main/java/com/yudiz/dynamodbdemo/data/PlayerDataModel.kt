package com.yudiz.dynamodbdemo.data

import android.content.Context
import android.text.SpannedString
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yudiz.dynamodbdemo.BR
import com.yudiz.dynamodbdemo.R
import java.io.Serializable


class PlayerDataModel(
) : Serializable, BaseObservable() {
    var id: String? = ""
    @Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }
    @Bindable
    var age: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.age)
        }
    @Bindable
    var type: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.type)
        }
    @Bindable
    var captain: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.captain)
        }

    fun getAgeText(): String {
        return "$age years old"
    }

    fun getNameText(context: Context): SpannedString {
        return buildSpannedString {
            append(name)
            if (captain) {
                append(" ")
                color(
                    ContextCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                ) { append("(C)") }
            }
        }
    }
}