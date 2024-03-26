package com.example.jetreader.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetreader.components.FABContent
import com.example.jetreader.components.InputField
import com.example.jetreader.components.ReaderAppBar
import com.example.jetreader.model.Item
import com.example.jetreader.model.MBook
import com.example.jetreader.navigation.ReaderScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController,viewModel: SearchScreenViewModelV2 = hiltViewModel()) {
    Scaffold(topBar = {
        ReaderAppBar(title = "Search Book", icon = Icons.Default.ArrowBack, navController = navController, showProfile = false){
            navController.popBackStack()
        }
    }, floatingActionButton = {

    }) { it ->
        Surface(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            Column {
                SearchForm(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),viewModel = viewModel){ search ->
                    viewModel.searchBooks(search)
                }
                Spacer(modifier = Modifier.height(13.dp))
                BookList(navController,viewModel)
            }



        }

    }
}

@Composable
fun BookList(navController: NavController, viewModel: SearchScreenViewModelV2) {
    val list = viewModel.list
    if(viewModel.isLoading){
        LinearProgressIndicator()
    }else{
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)){
            items(items = list){book ->
                BookRow(book,navController)
            }
        }
    }

}

@Composable
fun BookRow(book: Item, navController: NavController) {
   Card(modifier = Modifier
       .clickable {
            navController.navigate(ReaderScreens.BookDetailsScreen.name + "/${book.id}")
       }
       .fillMaxWidth()
       .height(100.dp)
       .padding(3.dp),
       shape = RectangleShape,
       colors = CardDefaults.cardColors(containerColor = Color.White),
       elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
       Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {
           var imageUrl = book?.volumeInfo?.imageLinks?.thumbnail
           if(imageUrl.isNullOrEmpty()) imageUrl = ""
           AsyncImage(
               model = ImageRequest.Builder(LocalContext.current)
                   .data(imageUrl)
                   .crossfade(true)
                   .build(),
               contentDescription = "book image",
               modifier = Modifier
                   .fillMaxHeight()
                   .width(80.dp)
                   .padding(end = 4.dp)
           )
            Column {
                Text(text = book.volumeInfo.title.toString(), overflow = TextOverflow.Ellipsis)
                Text(text = "Authors: ${book.volumeInfo.authors}", overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.titleSmall, fontStyle = FontStyle.Italic)
                Text(text = "Date: ${book.volumeInfo.publishedDate}", overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.titleSmall, fontStyle = FontStyle.Italic)
                Text(text = "Category: ${book.volumeInfo.categories}", overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.titleSmall, fontStyle = FontStyle.Italic)

            }
       }
   }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(modifier: Modifier = Modifier,
               viewModel: SearchScreenViewModelV2,
               loading:Boolean = false,
               hint: String = "Search",
               onSearch:(String) -> Unit = {}){
    Column {
        val searchQueryState = rememberSaveable{
            mutableStateOf("")
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value){
            searchQueryState.value.trim().isNotEmpty()
        }

        InputField(
            valueState = searchQueryState,
            labelId = "Search",
            enabled = true,
            onAction = KeyboardActions{
                if(!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            }
        )

    }

}