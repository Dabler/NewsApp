package com.dabler.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dabler.newsapp.network.Article
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.skydoves.landscapist.glide.GlideImage

const val NEWS_LIST_SCREEN = "newsList"
const val NEWS_DETAILS_SCREEN = "newsDetails"
const val ARTICLE_ID_PARAMETER = "articleId"

class MainActivity : AppCompatActivity() {

    private val newsViewModel: NewsViewModel by viewModels()

    private val light = Font(R.font.librefranklinlight, FontWeight.W300)
    private val regular = Font(R.font.librefranklinregular, FontWeight.W400)
    private val medium = Font(R.font.librefranklinmedium, FontWeight.W500)
    private val semibold = Font(R.font.librefranklinbold, FontWeight.W600)
    private val bold = Font(R.font.librefranklinbold, FontWeight.W700)

    private val libreFrankFamily = FontFamily(light, regular, medium, semibold, bold)

    private val libreFrankTypography = Typography(
        defaultFontFamily = libreFrankFamily,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppNavigator() }
    }

    @Composable
    fun AppNavigator() {
        val navController: NavHostController = rememberNavController()
        //TODO 25.03 Add network loading and errors handling
        val articles: List<Article>? by newsViewModel.articles.observeAsState()
        if(articles.isNullOrEmpty()) return
        MaterialTheme(typography = libreFrankTypography) {
            NavHost(navController, startDestination = NEWS_LIST_SCREEN) {
                composable(NEWS_LIST_SCREEN) { NewsListView(navController, articles) }
                composable(
                    "$NEWS_DETAILS_SCREEN/{$ARTICLE_ID_PARAMETER}",
                    arguments = listOf(navArgument(ARTICLE_ID_PARAMETER) { type = NavType.IntType })
                ) { backStackEntry ->
                    NewsDetails(
                        navController,
                        //TODO 25.03 solve better force unwraps
                        articles!![backStackEntry.arguments?.getInt(ARTICLE_ID_PARAMETER) ?: 0]
                    )
                }
            }
        }
    }

    @Composable
    fun NewsListView(navController: NavController, articles: List<Article>?) {
        Column {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 10.dp,
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = getString(R.string.app_title),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                })
            LazyColumn {
                items(articles ?: listOf()) { article ->
                    val articleIndex = articles?.indexOf(article)
                    val onClick = { navController.navigate("$NEWS_DETAILS_SCREEN/$articleIndex") }
                    if (articleIndex == 0) {
                        FirstListRow(article) { onClick() }
                    } else {
                        ListRow(article) { onClick() }
                    }
                }
            }
        }
    }

    @Composable
    fun ListRow(article: Article, onClick: () -> Unit) {
        Column(modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(article.author ?: "", color = Color.Gray)
                    Text(article.title ?: "", modifier = Modifier.width(320.dp))
                }
                Row(
                    modifier = Modifier.size(50.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        imageModel = article.urlToImage ?: "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
        }
    }

    @Composable
    fun FirstListRow(article: Article, onClick: () -> Unit) {
        Column(modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }) {
            GlideImage(
                imageModel = article.urlToImage ?: "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                Column {
                    Text(article.author ?: "", color = Color.Gray)
                    Text(article.title ?: "")
                }
            }
            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
        }
    }

    @Composable
    fun NewsDetails(navController: NavController, article: Article) {
        Column {
            Box() {
                GlideImage(
                    imageModel = article.urlToImage ?: "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = null, // decorative element
                    modifier = Modifier
                        .background(Color.White)
                        .padding(10.dp)
                        .clickable { navController.navigate("newsList") }
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = article.author ?: "", fontWeight = FontWeight.Bold)
                Text(
                    text = article.title ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(text = article.content ?: "", modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}