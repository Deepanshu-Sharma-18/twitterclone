package com.example.twitterclone.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.twitterclone.components.TweetCard
import com.example.twitterclone.provider.AuthViewModel
import com.example.twitterclone.provider.MainViewModel
import com.example.twitterclone.ui.theme.RalewayFontFamily
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun SearchProfileScreen(navController: NavController, mainViewModel: MainViewModel, documentId: String ,authViewModel: AuthViewModel) {
    val scrollState  = rememberScrollState()
    val data by mainViewModel.getUser(documentId).collectAsState(initial = null)
    val bool by mainViewModel.isFollowing(documentId).collectAsState(initial = null)
    Log.d("isfollowing" , "${bool}")

    if (data == null || bool == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp), color = Color(0xff1DA1F2))
        }
    }else{

        Scaffold(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                ConstraintLayout(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                ) {
                    val ( image,topPart,edit ) = createRefs()

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(color = Color(0xff1DA1F2))
                        .constrainAs(topPart) {
                            top.linkTo(parent.top, 0.dp)
                            start.linkTo(parent.start, 0.dp)
                            bottom.linkTo(parent.bottom, 50.dp)

                        }) {

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 30.dp)
                            ,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }, modifier = Modifier
                                    .size(30.dp)
                                    .clip(shape = RoundedCornerShape(corner = CornerSize(50)))
                                    .background(color = Color(0xff657786)),

                                ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "back",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }


                        }
                    }

                    Box(modifier = Modifier.constrainAs(image){
                        top.linkTo(parent.top,100.dp)
                        start.linkTo(parent.start,30.dp)
                        bottom.linkTo(parent.bottom, (10).dp)
                    }) {
                        AsyncImage(
                            model = data!!["profilePic"],
                            contentDescription = "profile pic",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(shape = RoundedCornerShape(corner = CornerSize(50)))
                        )
                    }

                    Box(modifier = Modifier.constrainAs(edit){
                        top.linkTo(parent.top,160.dp)
                        end.linkTo(parent.end, (30).dp)
                        bottom.linkTo(parent.bottom, (0).dp)
                    }) {
                        OutlinedButton(
                            onClick = {
                                    mainViewModel.follow(documentId , data!!["followers"] as Long, documentId = documentId, userId = data!!["userId"].toString())

                            },
                            modifier = Modifier.background(color = Color.White)
                        ) {

                            Text(
                                text = if(bool!!.data == null) "Follow" else "Unfollow",
                                fontWeight = FontWeight.W500,
                                color = Color(0xff14171A)
                            )
                        }
                    }

                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {


                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${data!!["name"]}",
                        fontWeight = FontWeight.W700,
                        color = Color(0xff14171A),

                        fontSize = 25.sp
                    )
                    Text(
                        text = "@${data!!["userId"]}",
                        fontWeight = FontWeight.W400,
                        color = Color(0xff657786)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = "${data!!["bio"]}",
                            fontWeight = FontWeight.W400,
                            color = Color(0xff657786),
                            fontFamily = RalewayFontFamily,
                            fontSize = 15.sp
                        )

                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            ) {
                                append("${data!!["following"]}")
                            }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xff657786),
                                    fontSize = 15.sp
                                )
                            ) {
                                append(" Following")
                            }
                        })
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            ) {
                                append("${data!!["followers"]}")
                            }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xff657786),
                                    fontSize = 15.sp
                                )
                            ) {
                                append(" Followers")
                            }
                        })
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = "Tweets",
                        fontWeight = FontWeight.W700,
                        color = Color(0xff14171A),
                        fontSize = 19.sp,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(7.dp))

                    for (i in 1..data!!["noOfTweets"] as Long){
                        TweetCard("${documentId}$i",navController = navController, mainViewModel = mainViewModel, authViewModel = authViewModel )
                    }
                }
            }
        }
    }
}


