package com.example.dicerollerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicerollerapp.ui.theme.DiceRollerAppTheme
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerAppTheme {
                DiceRollerApp()
            }
        }
    }
}

@Composable
fun DiceRollerApp() {
    var rollHistory by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF49426E),
                        Color(0xFF37355C)
                    )
                )
            ), // Slight purple gradient background color
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF630AFC),
                                Color(0xFFD997FC),
                                Color(0xFFC06CFF),
                                Color(0xFFE0ACFF),
                                Color(0xFFD997FC),
                                Color(0xFFB156FC),
                                Color(0xFF630AFC)
                            )
                        )
                    ), // Ensure full column background color
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp) // Padding for equal space between the sides and the top of the app
                        .graphicsLayer {
                            shadowElevation = 24.dp.toPx()
                            shape = RoundedCornerShape(16.dp)
                            clip = true
                        }
                        .background(Color(0xFF37355C), RoundedCornerShape(16.dp)) // Darker purple background color with rounded corners
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROLL CALL",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB8B8C9)
                        )
                        Button(
                            onClick = { showHistoryDialog = true },
                            enabled = rollHistory.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (rollHistory.isNotEmpty()) Color(0xFF6A6AD8) else Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("HISTORY", color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                var numberOfDice by remember { mutableIntStateOf(1) }
                var diceType by remember { mutableIntStateOf(6) }
                var modifierValue by remember { mutableIntStateOf(0) }
                var results by remember { mutableStateOf(listOf<Int>()) }
                var total by remember { mutableIntStateOf(0) }
                var rollMessage by remember { mutableStateOf("") }
                var pendingNumberOfDice by remember { mutableIntStateOf(1) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .graphicsLayer {
                            shadowElevation = 24.dp.toPx()
                            shape = RoundedCornerShape(16.dp)
                            clip = true
                        }
                        .background(Color(0xFF37355C), RoundedCornerShape(16.dp)) // Dark purple box with rounded edges
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DiceTypeSlider(diceType, Modifier.weight(1f)) { selectedType ->
                                diceType = selectedType
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ModifierEntryBox(modifierValue) { value ->
                                modifierValue = value
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NumberOfDiceSlider(pendingNumberOfDice, Modifier.weight(1f)) { selectedNumber ->
                                pendingNumberOfDice = selectedNumber
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .wrapContentSize(Alignment.Center)
                ) {
                    DiceWithButtonAndImage(
                        diceType = diceType,
                        results = results,
                        onDieClick = { index ->
                            results = results.toMutableList().apply {
                                this[index] = (1..diceType).random()
                            }
                            total = results.sum()
                            rollMessage = "You rolled: ${results.joinToString(", ")}"
                            val timestamp = getCurrentTime()
                            rollHistory = rollHistory + Pair(rollMessage, timestamp)
                        },
                        onLongPress = { index ->
                            results = results.toMutableList().apply {
                                removeAt(index)
                            }
                            numberOfDice = results.size
                            total = results.sum() // Update total after deletion
                            rollMessage = "You rolled: ${results.joinToString(", ")}"
                        },
                        onEdit = { index, newType ->
                            results = results.toMutableList().apply {
                                this[index] = (1..newType).random()
                            }
                        },
                        updateTotal = { updatedResults ->
                            total = updatedResults.sum()
                            rollMessage = "You rolled: ${updatedResults.joinToString(", ")}"
                        } // Pass the updateTotal function
                    )
                }

                BottomSection(
                    total = total,
                    modifierValue = modifierValue,
                    rollMessage = rollMessage,
                    onRollClick = {
                        numberOfDice = pendingNumberOfDice
                        results = List(numberOfDice) { (1..diceType).random() }
                        total = results.sum()
                        rollMessage = "You rolled: ${results.joinToString(", ")}"
                        val timestamp = getCurrentTime()
                        rollHistory = rollHistory + Pair(rollMessage, timestamp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp) // Padding for equal space between the sides and the bottom of the app
                        .graphicsLayer {
                            shadowElevation = 24.dp.toPx()
                            shape = RoundedCornerShape(16.dp)
                            clip = true
                        }
                        .background(Color(0xFF37355C), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                )
            }

            if (showHistoryDialog) {
                RollHistoryDialog(
                    rollHistory = rollHistory,
                    onDismiss = { showHistoryDialog = false },
                    onClearHistory = { rollHistory = emptyList() }
                )
            }
        }
    )
}

@Composable
fun TextContainer(rollMessage: String, total: Int, modifierValue: Int) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 24.dp.toPx()
                shape = RoundedCornerShape(12.dp)
                clip = true
            }
            .background(Color(0xFFECC093), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = rollMessage,
                color = Color(0xFF49426E),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            val finalTotal = total + modifierValue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total: $total",
                    color = Color(0xFF49426E),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " + $modifierValue",
                    color = Color(0xFF49426E),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " = $finalTotal",
                    color = Color(0xFF49426E),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RollHistoryDialog(
    rollHistory: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onClearHistory: () -> Unit
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showConfirmationDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6AD8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("YES", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6AD8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("NO", color = Color.White)
                }
            },
            title = { Text(text = "Clear History", fontWeight = FontWeight.Bold, color = Color(0xFF6A6AD8)) },
            text = { Text(text = "Are you sure you want to clear the history?", color = Color(0xFF6A6AD8)) },
            shape = RoundedCornerShape(16.dp)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6AD8)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CLOSE", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { showConfirmationDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6AD8)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CLEAR HISTORY", color = Color.White)
            }
        },
        title = {
            Text(text = "HISTORY", fontWeight = FontWeight.Bold, color = Color(0xFF6A6AD8))
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Adjust width to fill max available width
                    .fillMaxHeight(0.7f)
                    .background(Color.Transparent) // Transparent background
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF202020), RoundedCornerShape(16.dp)) // Very dark gray background with rounded corners
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
                    ) {
                        rollHistory.reversed().forEach { (roll, time) ->
                            Column(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = roll, color = Color(0xFF6A6AD8))
                                Text(text = time, color = Color(0x806A6AD8), fontSize = 10.sp) // Smaller and faint color for time
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(horizontal = 32.dp) // Increase horizontal padding for wider dialog
            .fillMaxWidth() // Fill max width
            .background(Color.Transparent) // Make dialog background transparent
    )
}

@Composable
fun DiceWithButtonAndImage(
    diceType: Int,
    results: List<Int>,
    onDieClick: (Int) -> Unit,
    onLongPress: (Int) -> Unit,
    onEdit: (Int, Int) -> Unit,
    updateTotal: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableIntStateOf(-1) }
    var newType by remember { mutableIntStateOf(diceType) }
    var clickedIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        results.chunked(3).forEachIndexed { rowIndex, rowResults ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                rowResults.forEachIndexed { columnIndex, result ->
                    val index = rowIndex * 3 + columnIndex

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(100.dp) // Fixed size for all dice
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        clickedIndex = index
                                        tryAwaitRelease()
                                        clickedIndex = -1
                                    },
                                    onLongPress = {
                                        editIndex = index
                                        showEditDialog = true
                                    },
                                    onTap = {
                                        onDieClick(index)
                                    }
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (index == clickedIndex) Color.Gray.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                        // Map result to d6 image resource
                        val imageResource = when (result % 6) {
                            1 -> R.drawable.dice_1
                            2 -> R.drawable.dice_2
                            3 -> R.drawable.dice_3
                            4 -> R.drawable.dice_4
                            5 -> R.drawable.dice_5
                            else -> R.drawable.dice_6
                        }
                        Image(
                            painter = painterResource(imageResource),
                            contentDescription = result.toString(),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp) // Add padding inside the box to ensure image fits well
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog && editIndex >= 0) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        onEdit(editIndex, newType)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6AD8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("DONE", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onLongPress(editIndex)
                        showEditDialog = false
                        val updatedResults = results.toMutableList().apply { removeAt(editIndex) }
                        updateTotal(updatedResults) // Update total and rollMessage after deletion
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6AD8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("DELETE", color = Color.White)
                }
            },
            title = { Text(text = "Edit Dice", fontWeight = FontWeight.Bold, color = Color(0xFF6A6AD8)) },
            text = {
                Column {
                    Text("Select new dice type:")
                    Spacer(modifier = Modifier.height(8.dp))
                    DiceTypeSlider(newType, onTypeSelected = { newType = it })
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun BottomSection(
    total: Int,
    modifierValue: Int,
    rollMessage: String,
    onRollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onRollClick,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 16.dp,
                pressedElevation = 20.dp,
                focusedElevation = 16.dp,
                hoveredElevation = 16.dp
            ),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6AD8)),
            modifier = Modifier
                .padding(16.dp)
                .height(60.dp) // Increased height from default
                .width(200.dp) // Increased width from default
        ) {
            Text(
                text = stringResource(R.string.roll),
                color = Color.White,
                fontSize = 24.sp // Increased font size from 20.sp to 24.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextContainer(rollMessage, total, modifierValue)
    }
}

@Composable
fun DiceTypeSlider(selectedType: Int, modifier: Modifier = Modifier, onTypeSelected: (Int) -> Unit) {
    val diceTypes = listOf(4, 6, 8, 10, 12, 20)
    val typeRange = 0f..(diceTypes.size - 1).toFloat()

    Column(modifier = modifier.height(56.dp)) {
        Text(text = "d$selectedType", fontSize = 18.sp, color = Color.White)
        Slider(
            value = diceTypes.indexOf(selectedType).toFloat(),
            onValueChange = { value ->
                val index = value.toInt().coerceIn(typeRange.start.toInt(), typeRange.endInclusive.toInt())
                onTypeSelected(diceTypes[index])
            },
            valueRange = typeRange,
            steps = diceTypes.size - 2, // Because steps should be count-1 for a range of discrete values
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFECC093),
                activeTrackColor = Color(0xFFECC093),
                inactiveTrackColor = Color(0xFF49426E)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun NumberOfDiceSlider(selectedNumber: Int, modifier: Modifier = Modifier, onNumberSelected: (Int) -> Unit) {
    val numberRange = 1..6

    Column(modifier = modifier.height(56.dp)) {
        Text(text = "$selectedNumber", fontSize = 18.sp, color = Color.White)
        Slider(
            value = selectedNumber.toFloat(),
            onValueChange = { value ->
                val number = value.toInt().coerceIn(numberRange)
                onNumberSelected(number)
            },
            valueRange = numberRange.first.toFloat()..numberRange.last.toFloat(),
            steps = numberRange.last - numberRange.first - 1,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFECC093),
                activeTrackColor = Color(0xFFECC093),
                inactiveTrackColor = Color(0xFF49426E)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ModifierEntryBox(modifierValue: Int, onValueChange: (Int) -> Unit) {
    var text by remember { mutableStateOf(modifierValue.toString()) }
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        value = if (isFocused && text == "0") "" else text,
        onValueChange = {
            if (it.length <= 4) {
                text = it
                onValueChange(it.toIntOrNull()?.coerceAtMost(9999) ?: 0)
            }
        },
        label = { Text("Modifier") },
        placeholder = { Text(text = "0") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp), // Ensures consistent text size
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = Color(0xFFECC093), // Set background color if needed
            unfocusedContainerColor = Color(0xFFECC093), // Set background color if needed
            unfocusedLabelColor = Color(0xFF2D353A), // Set text color if needed
            focusedLabelColor = Color(0xFF2D353A), // Set text color if needed
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .graphicsLayer {
                shadowElevation = 24.dp.toPx()
                shape = RoundedCornerShape(8.dp)
                clip = true
            }
            .width(100.dp)
            .padding(start = 8.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (!isFocused && text.isEmpty()) {
                    text = "0"
                    onValueChange(0)
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun DiceRollerAppPreview() {
    DiceRollerAppTheme {
        DiceRollerApp()
    }
}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("hh:mm a, MM/dd/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}
