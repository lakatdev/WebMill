package hu.keszeglab.webmill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(
    onStartClick: (Boolean, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVsComputer by remember { mutableStateOf(false) }
    var smartness by remember { mutableStateOf(50f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WebMill",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Select your opponent and begin playing!",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { isVsComputer = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isVsComputer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Vs Player")
            }
            Button(
                onClick = { isVsComputer = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVsComputer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Vs Computer")
            }
        }

        if (isVsComputer) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp).width(300.dp)
            ) {
                Text(
                    text = "Computer Smartness: ${smartness.toInt()}%",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = smartness,
                    onValueChange = { smartness = it },
                    valueRange = 0f..100f,
                    steps = 99
                )
            }
        }

        Button(
            onClick = { onStartClick(isVsComputer, smartness.toInt()) },
            modifier = Modifier.padding(16.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Begin",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
