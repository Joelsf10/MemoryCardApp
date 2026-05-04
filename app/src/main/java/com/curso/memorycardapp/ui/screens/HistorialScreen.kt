package com.curso.memorycardapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curso.memorycardapp.data.db.MemoryCardDatabase
import com.curso.memorycardapp.data.repository.Repository
import com.curso.memorycardapp.ui.data.db.PartidaEntity
import com.curso.memorycardapp.ui.model.HistorialViewModel
import com.curso.memorycardapp.ui.model.HistorialViewModelFactory
import com.curso.memorycardapp.ui.utils.isTablet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    windowSizeClass: WindowSizeClass,
    onBack: () -> Unit,
    onDetalle: (Int) -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val isTablet = windowSizeClass.isTablet()

    val db = MemoryCardDatabase.getInstance(context)
    val repo = Repository(db.partidaDao())
    val viewModel: HistorialViewModel = viewModel(factory = HistorialViewModelFactory(repo))
    val partidas by viewModel.partidas.collectAsState()
    val seleccionada by viewModel.partidaSeleccionada.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial de partidas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            "←", style = MaterialTheme.typography.headlineSmall.copy(
                                color = colorScheme.onPrimary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
            )
        }
    ) { padding ->
        if (partidas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎮", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Aún no hay partidas guardadas",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Juega una partida para verla aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else if (isTablet) {
            // TABLET: bi-panel (lista | detalle)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Panel izquierdo: lista
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(partidas) { partida ->
                        PartidaItem(
                            partida = partida,
                            isSelected = partida.id == seleccionada?.id,
                            onClick = { viewModel.seleccionarPartida(partida) }
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = colorScheme.outline.copy(alpha = 0.3f)
                )

                // Panel derecho: detalle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (seleccionada != null) {
                        DetalleInline(partida = seleccionada!!)
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Selecciona una partida para ver el detalle",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // SMARTPHONE: lista, navega a pantalla de detalle
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(partidas) { partida ->
                    PartidaItem(
                        partida = partida,
                        isSelected = false,
                        onClick = { onDetalle(partida.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PartidaItem(
    partida: PartidaEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val resultColor = when (partida.resultado) {
        "Victoria" -> Color(0xFF2E7D32)
        "Tiempo agotado" -> Color(0xFF854F0B)
        else -> colorScheme.error
    }
    val resultEmoji = when (partida.resultado) {
        "Victoria" -> "🎉"
        "Tiempo agotado" -> "⏱"
        else -> "💀"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorScheme.primaryContainer
            else colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = partida.alias,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = partida.fechaHora,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$resultEmoji ${partida.resultado}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = resultColor
                    )
                )
                Text(
                    text = "${partida.numPares} pares · ${partida.errores} err.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Detalle de partida en el panel secundario de tablets
@Composable
private fun DetalleInline(partida: PartidaEntity) {
    val colorScheme = MaterialTheme.colorScheme
    val resultColor = when (partida.resultado) {
        "Victoria" -> Color(0xFF2E7D32)
        "Tiempo agotado" -> Color(0xFF854F0B)
        else -> colorScheme.error
    }
    val resultEmoji = when (partida.resultado) {
        "Victoria" -> "🎉"; "Tiempo agotado" -> "⏱"; else -> "💀"
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Detalles de la partida…",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(resultEmoji, style = MaterialTheme.typography.headlineSmall)
            Text(
                partida.resultado,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, color = resultColor
                )
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetalleRow("Jugador", partida.alias)
                Divider(modifier = Modifier.padding(vertical = 6.dp))
                DetalleRow("Fecha", partida.fechaHora)
                Divider(modifier = Modifier.padding(vertical = 6.dp))
                DetalleRow("Pares", "${partida.numPares}")
                Divider(modifier = Modifier.padding(vertical = 6.dp))
                DetalleRow(
                    "Tiempo",
                    "%02d:%02d".format(partida.tiempoSegundos / 60, partida.tiempoSegundos % 60)
                )
                Divider(modifier = Modifier.padding(vertical = 6.dp))
                DetalleRow("Errores", "${partida.errores}")
            }
        }
    }
}

@Composable
private fun DetalleRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}