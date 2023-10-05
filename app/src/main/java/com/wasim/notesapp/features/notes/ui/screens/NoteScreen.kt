package com.wasim.notesapp.features.notes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.wasim.notesapp.R
import com.wasim.notesapp.data.model.Notes
import com.wasim.notesapp.data.model.NotesResponse
import com.wasim.notesapp.features.notes.ui.viewmodel.NoteViewModel
import com.wasim.notesapp.features.notes.ui.viewmodel.events.NotesEvent
import com.wasim.notesapp.ui.theme.Background
import com.wasim.notesapp.ui.theme.ContentColor
import com.wasim.notesapp.ui.theme.Red
import com.wasim.notesapp.utils.ApiState
import com.wasim.notesapp.utils.showToast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotesScreen(
    viewModel: NoteViewModel = hiltViewModel()
) {
    val context= LocalContext.current

    var search by remember { mutableStateOf("") }
    val noteStates = viewModel.getNoteEventFlow.value
    var title by remember {
        mutableStateOf("")
    }
    var desc by remember {
        mutableStateOf("")
    }
    var id by remember {
        mutableStateOf(0)
    }
    var isAddDialog by remember {
        mutableStateOf(false)
    }

    var isUpdateDialog by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }
    //for get notes
    LaunchedEffect(key1 = true){
        viewModel.onEvent(NotesEvent.GetNoteEvent)
    }

    //for add note
    LaunchedEffect(key1 = true){
        viewModel.addNoteEventFlow.collectLatest {
            isLoading = when(it){
                is ApiState.Success -> {
                    viewModel.onEvent(NotesEvent.GetNoteEvent)
                    isAddDialog = false
                    context.showToast("Note Added")
                    true
                }
                is ApiState.Failure -> {
                    context.showToast(it.msg)
                    false
                }
                ApiState.Loading -> true
            }
        }
    }

    //for delete notes
    LaunchedEffect(key1 = true){
        viewModel.deleteNoteEventFlow.collectLatest {
            isLoading = when(it){
                is ApiState.Success -> {
                    viewModel.onEvent(NotesEvent.GetNoteEvent)
                    context.showToast("Note Deleted")
                    true
                }
                is ApiState.Failure -> {
                    context.showToast(it.msg)
                    false
                }
                ApiState.Loading -> true
            }
        }
    }

    //for update note
    LaunchedEffect(key1 = true){
        viewModel.updateNoteEventFlow.collectLatest {
            isLoading = when(it){
                is ApiState.Success -> {
                    viewModel.onEvent(NotesEvent.GetNoteEvent)
                    isUpdateDialog = false
                    context.showToast("Note Updated")
                    true
                }
                is ApiState.Failure -> {
                    context.showToast(it.msg)
                    false
                }
                ApiState.Loading -> true
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isAddDialog = true
            }, backgroundColor = Red) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        )
        {
            AppSearchBar(search = search, onValueChange = {
                search = it
            }, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 50.dp))
        }
    }

    if (isAddDialog)
        showDialogBox(
            title = title,
            description = desc,
            onTitleChange = { title = it },
            onDesChange = { desc = it },
            onClose = { isAddDialog = it }
        ) {
            viewModel.onEvent(NotesEvent.AddNoteEvent(Notes(title,desc)))
        }

    if (isUpdateDialog)
        showDialogBox(
            title = title,
            description = desc,
            onTitleChange = { title = it },
            onDesChange = { desc = it },
            onClose = { isUpdateDialog = it }
        ) {
            viewModel.onEvent(NotesEvent.UpdateNotes(id,Notes(title,desc)))
        }

    if (isLoading){
        LoadingDialog()
    }

    if(noteStates.isLoading)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
            CircularProgressIndicator(color = Red)
        }
    if (noteStates.data.isNotEmpty()){
        LazyVerticalGrid(columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(15.dp)
            ){

                val filteredNotes: List<NotesResponse> = if (search.isEmpty()){
                    emptyList()
                }else{
                    val resultData: ArrayList<NotesResponse> = arrayListOf()
                    for(temp in noteStates.data){
                        if(temp.title.contains(search,true) ||
                            temp.description.contains(search,true)){
                            resultData.add(temp)
                        }
                    }
                    resultData
                }

                items(filteredNotes, key = {
                    it.id
                }){
                    NotesEachRow(notesResponse = it, onUpdate = {
                            title=it.title
                            desc=it.description
                            id=it.id
                    }) {
                        viewModel.onEvent(NotesEvent.DeleteNotes(it.id))
                    }
                }
            }
    }
}

@Composable
fun showDialogBox(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDesChange: (String) -> Unit,
    onClose: (Boolean) -> Unit,
    onClick: () -> Unit
) {

    val focusRequester = FocusRequester()
    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
    }

    AlertDialog(onDismissRequest = { },
        buttons = {
            Button(
                onClick = {
                    onClick
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Red,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 15.dp)
            ) {
                Text(text = "Save")
            }
        },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Background,
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = TopEnd) {
                IconButton(onClick = { onClose(false) }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Red)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                AppTextField(
                    text = title,
                    placeHolder = stringResource(R.string.title),
                    modifier = Modifier.focusRequester(focusRequester),
                    onValueChange = onTitleChange
                )
                Spacer(modifier = Modifier.height(15.dp))
                AppTextField(
                    text = description,
                    placeHolder = stringResource(R.string.desc),
                    modifier = Modifier.height(300.dp),
                    onValueChange = onDesChange
                )
            }
        }

    )
}

@Composable
fun AppTextField(
    text: String,
    placeHolder: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit
) {
    TextField(value = text, onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(text = placeHolder, color = Color.Black.copy(0.4f))
        }
    )
}

@Composable
fun NotesEachRow(
    notesResponse: NotesResponse,
    modifier: Modifier = Modifier,
    onUpdate:()->Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUpdate() }
            .background(
                color = ContentColor, RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = notesResponse.title, style = TextStyle(
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W600
                    ),
                    modifier = Modifier.weight(0.7f)
                )
                IconButton(
                    onClick = { onDelete() }, modifier = Modifier
                        .weight(0.3f)
                        .align(CenterVertically)
                )
                {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "", tint = Red)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = notesResponse.description, style = TextStyle(
                    color = Color.Black.copy(0.6f),
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = notesResponse.updated_at.split("T")[0], style = TextStyle(
                    color = Color.Black.copy(0.3f),
                    fontSize = 10.sp
                )
            )
        }
    }
}


@Composable
fun AppSearchBar(
    search: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {

    TextField(value = search, onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = ContentColor,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "", tint = Red)
        },
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(onClick = {
                    onValueChange("")
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "")
                }
            }
        },
        placeholder = {
            Text(
                text = "Search notes...", style = TextStyle(
                    color = Color.Black.copy(0.5f)
                )
            )

        }
    )
}
@Composable
fun LoadingDialog(){
    Dialog(onDismissRequest = {  }) {
        CircularProgressIndicator(color = Red)
    }
}