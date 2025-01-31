package com.example.noteroomdb

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.noteroomdb.roomDb.Note
import com.example.noteroomdb.roomDb.NoteDatabase
import com.example.noteroomdb.ui.theme.NoteRoomDBTheme
import com.example.noteroomdb.viewModel.NoteViewModel
import com.example.noteroomdb.viewModel.Repositary

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            name = "note.db"
        ).build()
    }
    private val viewModel by viewModels<NoteViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NoteViewModel(Repositary(db)) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteRoomDBTheme {
                var name by remember {
                    mutableStateOf("")
                }
                var body by remember {
                    mutableStateOf("")
                }
                val note = Note(
                    noteName = name,
                    noteBody = body
                )
                var noteList by remember {
                    mutableStateOf(listOf<Note>())
                }
                viewModel.getNotes().observe(this) {
                    noteList = it
                }
                val context = LocalContext.current
                var isFilled by remember {
                    mutableStateOf(true)
                }
                var clearTextFieldTrigger by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        LaunchedEffect(key1 = clearTextFieldTrigger) {
                            name = ""
                            body = ""
                        }
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = {
                                Text(
                                    text = "Name",
                                    fontFamily = FontFamily.SansSerif
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isFilled
                        )

                        OutlinedTextField(
                            value = body,
                            onValueChange = { body = it },
                            label = {
                                Text(
                                    text = "Body",
                                    fontFamily = FontFamily.SansSerif
                                )
                            },
                            singleLine = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            isError = !isFilled
                        )
                        Button(
                            onClick = {
                                if (name.isNotEmpty() && body.isNotEmpty()) {
                                    viewModel.upsertNote(note)
                                    clearTextFieldTrigger = !clearTextFieldTrigger

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please fill all the fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isFilled = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Save",
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyColumn {
                            items(noteList) { note ->
                                Column {
                                    NoteItem(
                                        name = note.noteName,
                                        body = note.noteBody,
                                        onDelete = { viewModel.deleteNote(note) }
                                    )

                                    HorizontalDivider(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(6.dp)
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NoteItem(name: String, body: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Name: $name",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Body: $body",
                fontFamily = FontFamily.SansSerif,
            )
        }
        IconButton(
            onClick = {
                onDelete()
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = null
            )
        }

    }
}