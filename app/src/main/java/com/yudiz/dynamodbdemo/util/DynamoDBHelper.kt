package com.yudiz.dynamodbdemo.util

import com.amazonaws.regions.Regions

object DynamoDBHelper {
    const val COGNITO_IDP_ID = "<your-identity-pool-id>"
    val COGNITO_IDP_REGION = Regions.US_EAST_2  //allotted region
    const val TABLE_NAME = "Players"    //your table name
}