package com.curso.memorycardapp.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.curso.memorycardapp.ui.model.CardData
import com.curso.memorycardapp.R

@Composable
fun MemoryCard(
    cardData: CardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.7f)  // Proporción similar a una carta real
            .clickable(
                enabled = !cardData.isMatched,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Frente de la carta (imagen)
            if (cardData.isFaceUp) {
                Image(
                    painter = painterResource(id = cardData.imageRes),
                    contentDescription = "Carta ${cardData.id}",
                    contentScale = ContentScale.Crop
                )
            }
            // Dorso de la carta
            else {
                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Dorso de carta",
                    contentScale = ContentScale.Crop
                )
            }

            // Efecto visual si está emparejada
            if (cardData.isMatched) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
        }
    }
}