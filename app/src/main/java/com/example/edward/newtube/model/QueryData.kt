package com.example.edward.newtube.model


/**
 * Created by Edward on 6/22/2018.
 */
class QueryData (val query: String, val type: Type = Type.QUERY_STRING)
enum class Type{
    QUERY_STRING,
    RELATED_VIDEO_ID
}


