package com.example.jetreader.screens.home
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.jetreader.components.FABContent
import com.example.jetreader.components.ListCard
import com.example.jetreader.components.ReaderAppBar
import com.example.jetreader.components.TitleSection
import com.example.jetreader.model.MBook
import com.example.jetreader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, homeViewModel: HomeScreenViewModel = hiltViewModel()) {
   Scaffold(topBar = {
                     ReaderAppBar(title = "A.Reader", showProfile = true, navController = navController)
   }, floatingActionButton = {
        FABContent{
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
   }) {
    Surface(modifier = Modifier
        .padding(it)
        .fillMaxSize()) {
        HomeContent(navController = navController, homeViewModel)
    }
   }
}

@Composable
fun HomeContent(navController: NavController, homeViewModel: HomeScreenViewModel){
    var list = emptyList<MBook>()
    var currentUserObject = FirebaseAuth.getInstance().currentUser

    if(!homeViewModel.data.value.data.isNullOrEmpty()){
        list = homeViewModel.data.value?.data!!.toList().filter {
            it.userId == currentUserObject?.uid.toString()
        }
    }

    val currentUser = if(!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0)
    }else{
        "N/A"
    }
    Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Your reading \n " + " activity right now")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "profile", modifier = Modifier
                    .clickable {
                        navController.navigate(ReaderScreens.StatsScreen.name)
                    }
                    .size(45.dp), tint = MaterialTheme.colorScheme.secondary)
                Text(text = currentUser!!, modifier = Modifier.padding(3.dp), color = Color.Red, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Clip)
                Divider()
            }
        }
        ReadingRightNowArea(books = list, navController = navController)
        TitleSection(label = "Reading List")
        BookListArea(list,navController)
    }
}


@Composable
fun BookListArea(lisOfBooks: List<MBook>, navController: NavController) {
    HorizontalScrollComponent(lisOfBooks){
      navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollComponent(lisOfBooks: List<MBook>, onCardPress: (String) -> Unit = {}) {
    val scrollState = rememberScrollState()
    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(280.dp)
        .horizontalScroll(scrollState)) {
        for (book in lisOfBooks){
          ListCard(book){
              onCardPress(book.id.toString())
          }
        }
    }
}

@Composable
fun ReadingRightNowArea(books: List<MBook>, navController: NavController){
    //Filter books by reading now
    val readingNowList = books.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }
    HorizontalScrollComponent(readingNowList){
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}




