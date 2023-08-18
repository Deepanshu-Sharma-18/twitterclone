package com.example.twitterclone.model

data class User(
    val name: String,
    val userId: String,
    val profilePic: String,
    val email:String,
    val bio: String,
    val noOfTweets: Long,
    val following : Int,
    val followers : Int,
)