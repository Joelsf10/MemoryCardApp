package com.curso.memorycardapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.curso.memorycardapp.data.db.MemoryCardDatabase
import com.curso.memorycardapp.data.repository.Repository
import com.curso.memorycardapp.ui.data.db.PartidaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialDetalleScreen(
    partidaId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    var partida by remember { mutableStateOf<PartidaEntity?>(null) }

    LaunchedEffect(partidaId) {
        val db = MemoryCardDatabase.getInstance(context)
        val repo = Repository(db.partidaDao())
        partida = repo.obtenerDetalle(partidaId)
    }

    val topBarColor = when (partida?.resultado) {
        "Victoria" -> Color(0xFF3B6D11)
        "Tiempo agotado" -> Color(0xFF854F0B)
        else -> colorScheme.error
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalles de la partida",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            "←",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (partida != null) topBarColor else colorScheme.primary
                )
            )
        }
    ) { padding ->
        partida?.let { p ->
            val resultEmoji = when (p.resultado) {
                "Victoria" -> "🎉"
                "Tiempo agotado" -> "⏱"
                else -> "💀"
            }
            val resultColor = when (p.resultado) {
                "Victoria" -> Color(0xFF2E7D32)
                "Tiempo agotado" -> Color(0xFF854F0B)
                else -> colorScheme.error
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Resultado destacado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(resultEmoji, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = p.resultado,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = resultColor
                        )
                    )
                }

                // Tarjeta con todos los datos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant,
                        contentColor = colorScheme.onSurfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetalleRow("Jugador", p.alias)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        DetalleRow("Fecha/hora", p.fechaHora)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        DetalleRow("Pares", "${p.numPares}")
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        DetalleRow("Tiempo", formatSegundos(p.tiempoSegundos))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        DetalleRow("Errores", "${p.errores}")
                    }
                }
            }
        } ?: run {
            // Cargando
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando...", color = colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DetalleRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

private fun formatSegundos(segundos: Int): String =
    "%02d:%02d".format(segundos / 60, segundos % 60)