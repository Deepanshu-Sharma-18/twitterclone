package com.example.twitterclone.screens

import android.annotation.SuppressLint
import android.util.Log
import java.util.Date
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.twitterclone.R
import com.example.twitterclone.components.CommentsCard
import com.example.twitterclone.components.VideoPlayer
import com.example.twitterclone.provider.AuthViewModel
import com.example.twitterclone.provider.MainViewModel
import com.example.twitterclone.utils.Screens
import com.example.twitterclone.utils.formatTimestamp
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch

@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState",
    "CoroutineCreationDuringComposition"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TweetDetail(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    documentId: String,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val listner by mainViewModel.getTweet(documentId).collectAsState(initial = null)
    val likes by mainViewModel.Likes(documentId = documentId).collectAsState(initial = null)

    if (
        listner == null || likes == null
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp), color = Color(0xff1DA1F2))
        }
    } else {
        val liked = remember {
            mutableStateOf(false)
        }
        var exist = remember {
            mutableStateOf<String?>(null)
        }
        GlobalScope.launch {
            exist.value = mainViewModel.searchUserExist(listner!!["userId"].toString())
        }
        if (exist.value != null) {

            val data by mainViewModel.getUser(exist.value!!).collectAsState(initial = null)

            if (data == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(color = Color.White),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = Color(0xff1DA1F2)
                    )
                }
            } else {

                if (data == mutableMapOf<String, Any>("error" to "No User Found"))
                else {

                    if (likes!!.size() != 0) {
                        liked.value = false
                        for (user in likes!!.documents) {
                            if (user.id == authViewModel.currentuser!!.uid) {
                                liked.value = true
                            }
                        }
                    }

                    Scaffold(modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Tweet",
                                        fontSize = 20.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.W400,
                                        modifier = Modifier.padding(start = 15.dp)
                                    )
                                },
                                navigationIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = "iconback",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(25.dp)
                                            .clickable {
                                                navController.popBackStack()
                                            })
                                },
                                colors = TopAppBarDefaults.mediumTopAppBarColors(
                                    containerColor = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .background(color = Color.White)
                                .padding(horizontal = 5.dp, vertical = 60.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.White)
                                    .padding(vertical = 20.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                AsyncImage(
                                    model = data!!["profilePic"],
                                    contentDescription = "profile pic",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(shape = RoundedCornerShape(corner = CornerSize(50)))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${data!!["name"]}",
                                        fontWeight = FontWeight.W700,
                                        color = Color(0xff14171A),
                                        fontSize = 25.sp
                                    )
                                    Text(
                                        text = "@${data!!["userId"]}",
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xff657786),
                                        fontSize = 17.sp
                                    )

                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Color.White)
                                    .padding(horizontal = 15.dp)
                            ) {

                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "${listner!!["content"]}",
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFF3C3D3F),
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(25.dp))
                                if (listner!!["url"] == "") {
                                } else {
                                    if (listner!!["url"].toString().contains("images")) {
                                        AsyncImage(

                                            model = listner!!["url"],
                                            contentDescription = "post-image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(end = 15.dp)
                                                .clip(
                                                    shape = RoundedCornerShape(
                                                        corner = CornerSize(
                                                            20.dp
                                                        )
                                                    )
                                                ),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    } else {
                                        VideoPlayer(uri = null, link = listner!!["url"].toString())

                                    }
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                                Divider(
                                    thickness = 1.dp,
                                    color = Color(0xffAAB8C2).copy(alpha = 0.3F),
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = Color.White),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(modifier = Modifier.width(60.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = Color.White),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {

                                            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.reply),
                                                contentDescription = "list",
                                                tint = Color(0xff657786),
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {
                                                        navController.navigate(Screens.CreateComment.name + "/$documentId")
                                                    })
                                            Spacer(modifier = Modifier.width(7.dp))
                                            Text(
                                                text = listner!!["commentNo"].toString(),
                                                fontWeight = FontWeight.W400,
                                                color = Color(0xff657786),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Box(modifier = Modifier.width(60.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = Color.White),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {

                                            Icon(imageVector = if (liked.value) ImageVector.vectorResource(
                                                id = R.drawable.likesolid
                                            ) else ImageVector.vectorResource(
                                                id = R.drawable.like
                                            ),
                                                contentDescription = "likes",
                                                tint = if (liked.value) Color(0xFFCC1D1D) else Color(
                                                    0xff657786
                                                ),
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {
                                                        GlobalScope.launch {

                                                            mainViewModel.updateLike(
                                                                documentId = documentId,
                                                                initalValue = listner!!["likesCount"] as Long
                                                            )

                                                        }

                                                    })
                                            Spacer(modifier = Modifier.width(7.dp))
                                            Text(
                                                text = listner!!["likesCount"].toString(),
                                                fontWeight = FontWeight.W400,
                                                color = Color(0xff657786),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Box(modifier = Modifier.width(60.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = Color.White),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {

                                            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.retweet),
                                                contentDescription = "retweet",
                                                tint = Color(0xff657786),
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {

                                                        mainViewModel.retweet(documentId = documentId)
                                                        mainViewModel.updateRetweet(
                                                            documentId = documentId,
                                                            initalValue = listner!!["retweets"] as Long
                                                        )

                                                    })
                                            Spacer(modifier = Modifier.width(7.dp))
                                            Text(
                                                text = listner!!["retweets"].toString(),
                                                fontWeight = FontWeight.W400,
                                                color = Color(0xff657786),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Box(modifier = Modifier.width(60.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = Color.White),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {

                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "list",
                                                tint = Color(0xff657786),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                                Divider(
                                    thickness = 1.dp,
                                    color = Color(0xffAAB8C2).copy(alpha = 0.3F),
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                val date = listner!!["timestamp"] as Timestamp
                                Text(
                                    text = date.toDate().toString(),
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFF3C3D3F),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Divider(
                                    thickness = 1.dp,
                                    color = Color(0xffAAB8C2).copy(alpha = 0.3F),
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = "Replies",
                                    fontWeight = FontWeight.W700,
                                    color = Color(0xFF3C3D3F),
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                val listComments = mainViewModel.getComments(documentId)
                                    .collectAsState(initial = null)
                                Log.d("COMMENTSLIST", listComments.toString())
                                if (
                                    listComments.value == null
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color = Color.White),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(60.dp),
                                            color = Color(0xff1DA1F2)
                                        )
                                    }
                                } else {
                                    if (listComments.value!!.size() == 0) {

                                        Text(
                                            text = ("No Replies"), fontWeight = FontWeight.W400,
                                            color = Color(0xFF3C3D3F),
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
                                        )
                                    }

                                    for (comment in listComments.value!!) {
                                        CommentsCard(comment.data, mainViewModel)
                                    }
                                }

                            }

                        }
                    }
                }
            }
        }

    }

}