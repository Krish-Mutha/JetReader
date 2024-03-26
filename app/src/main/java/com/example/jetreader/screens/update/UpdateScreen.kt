package com.example.jetreader.screens.update

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetreader.R
import com.example.jetreader.components.FABContent
import com.example.jetreader.components.InputField
import com.example.jetreader.components.RatingBar
import com.example.jetreader.components.ReaderAppBar
import com.example.jetreader.components.RoundedButton
import com.example.jetreader.data.DataOrException
import com.example.jetreader.model.MBook
import com.example.jetreader.navigation.ReaderScreens
import com.example.jetreader.screens.home.HomeContent
import com.example.jetreader.screens.home.HomeScreenViewModel
import com.example.jetreader.utils.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.CoroutineContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(navController: NavHostController, id: String?,viewModel: HomeScreenViewModel = hiltViewModel()) {
    Scaffold(topBar = {
        ReaderAppBar(title = "Update Book", showProfile = false, navController = navController,icon = Icons.Default.ArrowBack){
            navController.popBackStack()
        }
    }) {
        val bookInfo = produceState<DataOrException<List<MBook>,
                Boolean,
                Exception>>(initialValue = DataOrException(data = emptyList(),
            true, Exception(""))){
            value = viewModel.data.value
        }.value
        Log.d("INFO", "BookUpdateScreen: ${viewModel.data.value.data.toString()}")
        Surface(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            Column(modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                if(bookInfo.loading == true){
                    LinearProgressIndicator()
                    bookInfo.loading = false;
                }else{

                    Surface(modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(), shape = CircleShape, shadowElevation = 4.dp) {
                        ShowBookUpdate(bookInfo = viewModel.data.value, id)
                    }
                    ShowSimpleForm(book = viewModel.data.value.data?.first{book ->
                        book.id   == id
                    }, navController)


                }

            }


        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowSimpleForm(book: MBook?, navController: NavController) {
    val noteText = remember{
        mutableStateOf("")
    }
    val isStartedReading = remember{
        mutableStateOf(false)
    }
    val isFinishedReading = remember{
        mutableStateOf(false)
    }

    val ratingVal = remember{
        mutableStateOf(0)
    }
    if(book?.rating != null){
        book?.rating?.toInt().let {
            ratingVal.value = it!!
        }
    }
    val context = LocalContext.current
    SimpleForm(defaultValue = if (book?.notes.toString().isNotEmpty()) book?.notes.toString()
    else "No thoughts available."){ note ->
        noteText.value = note


    }
    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        if(book?.startedReading === null){
            TextButton(onClick = { isStartedReading.value = true }, enabled = book?.startedReading == null) {
                if(!isStartedReading.value){
                    Text(text = "Start Reading")
                }else{
                    Text(text = "Started Reading", modifier = Modifier.alpha(0.6f), color = Color.Red.copy(0.5f))
                }

            }
        }else{
            Text(text = "Started on: ${formatDate(book.startedReading!!)}")
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = { isFinishedReading.value = true }, enabled = book?.finishedReading == null) {
            if(book?.finishedReading == null){
                if(!isStartedReading.value){
                    Text(text = "Mark as Read")
                }else{
                    Text(text = "Finished Reading")
                }

            }else{
                Text(text = "Finished on ${formatDate(book.finishedReading!!)}", modifier = Modifier.alpha(0.6f), color = Color.Red.copy(0.5f))
            }

        }
    }
    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp))
    book?.rating?.toInt().let {rating ->

        RatingBar(rating = rating!!){
            ratingVal.value = rating
        }
    }
    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp)) {
        val changedNotes = book?.notes != noteText.value
        val changedRating = book?.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp = if (isFinishedReading.value) Timestamp.now() else book?.finishedReading
        val isStartedTimeStamp = if (isStartedReading.value) Timestamp.now() else book?.startedReading

        val bookUpdate = changedNotes || changedRating || isStartedReading.value || isFinishedReading.value

        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to noteText.value).toMap()

        RoundedButton(label = "update"){
            if (bookUpdate) {
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book?.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener {
                        showToast(context, "Book Updated Successfully!")
                        navController.navigate(ReaderScreens.HomeScreen.name)

                        // Log.d("Update", "ShowSimpleForm: ${task.result.toString()}")

                    }.addOnFailureListener{
                        Log.w("Error", "Error updating document" , it)
                    }
            }


        }
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(message = "ARE u sure?", openDialog){
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book?.id!!)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            openDialog.value = false
                            /*
                             Don't popBackStack() if we want the immediate recomposition
                             of the MainScreen UI, instead navigate to the mainScreen!
                            */

                            navController.navigate(ReaderScreens.HomeScreen.name)
                        }
                    }

            }
        }
        RoundedButton(label = "Delete"){
            openDialog.value = true
        }
    }

}

fun showToast(context: Context, s: String) {
    Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
}

@Composable
fun ShowAlertDialog(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = { openDialog.value = false},
            title = { Text(text = "Delete Book")},
            text = { Text(text = message)},
            confirmButton = {
                TextButton(onClick = { onYesPressed.invoke() }) {
                    Text(text = "Yes")

                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(text = "No")

                }
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue:String = "Great Book!",
    onSearch: (String) ->Unit
){
    Column {
        val textFieldValue = rememberSaveable {
            mutableStateOf(defaultValue)
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value){
            textFieldValue.value.trim().isNotEmpty()
        }

        InputField(valueState = textFieldValue, labelId = "Enter you thoughts", enabled = true,
            onAction = KeyboardActions{
                if(!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(
                    Color.White,
                    CircleShape
                )
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}


@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookId: String?) {
    Log.d("ShowBookUpdate", bookId.toString())
    Log.d("ShowBookUpdate", bookInfo.data.toString())
    Row {
        Spacer(modifier = Modifier.width(43.dp))
        Column (modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center){
            CardListItem(book = bookInfo.data!!.first(){
                it -> it.id .toString() == bookId.toString()
            }, onPressDetails = {})
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
    Card(modifier = Modifier
        .padding(
            start = 4.dp,
            end = 4.dp,
            top = 4.dp,
            bottom = 8.dp
        )
        .clip(RoundedCornerShape(20.dp))
        .clickable { }, elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.background(Color.White)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "book image",
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(end = 4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp
                        )
                    )
            )

            Column {
                Text(text = book.title.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)

                Text(text = book.authors.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 2.dp,
                        bottom = 0.dp))

                Text(text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 8.dp))

            }
        }



    }
}


