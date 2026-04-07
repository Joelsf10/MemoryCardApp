package com.curso.memorycardapp.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.curso.memorycardapp.ui.model.CardData

@Composable
fun CardGrid(
    cards: List<CardData>,
    onCardClick: (Int) -> Unit,
    columns: Int = 4,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(cards) { index, card ->
            MemoryCard(
                cardData = card,
                onClick = { onCardClick(index) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}