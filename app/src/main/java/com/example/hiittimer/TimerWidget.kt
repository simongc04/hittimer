package com.example.hiittimer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerWidget(
    title: String,
    time: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onNameChange: (String) -> Unit
) {
    var displayedTime by remember { mutableIntStateOf(time) }
    var isEditing by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf(title) }

    LaunchedEffect(time) {
        displayedTime = time
    }

    LaunchedEffect(title) {
        newTitle = title
    }

    // Usar Card y aplicar el fondo a través de Modifier
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF1F1F1F)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isEditing) {
                Log.d("TimerWidget", "entrando en modo edición")
                // TextField para editar el nombre del temporizador
                TextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .background(Color(0xFF1F1F1F)),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            Log.d("TimerWidget", "Saliendo del modo edición")
                            finishEditing(newTitle, onNameChange, title) {
                                isEditing = false
                            }
                        }
                    )
                )
            } else {
                // Mostrar el nombre del temporizador como un Text cuando no está en modo edición
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { isEditing = true } // Hacer clic para entrar en modo edición
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "➖",
                    fontSize = 24.sp,
                    color = Color.Green,
                    modifier = Modifier.clickable {
                        if (displayedTime > 0) {
                            onDecrease()
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format(Locale.US, "%02d:%02d", displayedTime / 60, displayedTime % 60),
                    fontSize = 36.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "➕",
                    fontSize = 24.sp,
                    color = Color.Green,
                    modifier = Modifier.clickable {
                        onIncrease()
                    }
                )
            }
        }
    }
}

fun finishEditing(newTitle: String, onNameChange: (String) -> Unit, originalTitle: String, onFinish: () -> Unit) {
    if (newTitle != originalTitle) {
        onNameChange(newTitle)
    }
    onFinish()
}
