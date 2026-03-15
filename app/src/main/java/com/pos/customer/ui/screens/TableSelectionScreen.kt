package com.pos.customer.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.customer.data.model.Table
import com.pos.customer.data.model.TableStatus
import com.pos.customer.ui.theme.AccentAmber
import com.pos.customer.ui.theme.AccentRed
import com.pos.customer.ui.theme.PrimaryGreen
import com.pos.customer.viewmodel.TableSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableSelectionScreen(
    onNavigateBack: () -> Unit,
    onTableSelected: () -> Unit,
    viewModel: TableSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Select Your Table",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Available count
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryGreen.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.availableCount}",
                        style = MaterialTheme.typography.displayMedium,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "tables available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusLegendItem(color = PrimaryGreen, label = "Available")
                StatusLegendItem(color = AccentRed, label = "Occupied")
                StatusLegendItem(color = AccentAmber, label = "Reserved")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Table Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.tables) { table ->
                    TableItem(
                        table = table,
                        isSelected = uiState.selectedTable?.id == table.id,
                        onClick = {
                            if (table.status == TableStatus.AVAILABLE) {
                                viewModel.selectTable(table)
                            }
                        }
                    )
                }
            }

            // Continue Button
            if (uiState.selectedTable != null) {
                Button(
                    onClick = onTableSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Continue with Table ${uiState.selectedTable.number}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StatusLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun TableItem(
    table: Table,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    val backgroundColor = when (table.status) {
        TableStatus.AVAILABLE -> PrimaryGreen.copy(alpha = 0.1f)
        TableStatus.OCCUPIED -> AccentRed.copy(alpha = 0.1f)
        TableStatus.RESERVED -> AccentAmber.copy(alpha = 0.1f)
    }

    val borderColor = when (table.status) {
        TableStatus.AVAILABLE -> PrimaryGreen.copy(alpha = 0.3f)
        TableStatus.OCCUPIED -> AccentRed.copy(alpha = 0.3f)
        TableStatus.RESERVED -> AccentAmber.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .scale(scale)
            .clickable(
                enabled = table.status == TableStatus.AVAILABLE,
                onClick = onClick
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = PrimaryGreen,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (table.status) {
                            TableStatus.AVAILABLE -> PrimaryGreen
                            TableStatus.OCCUPIED -> AccentRed
                            TableStatus.RESERVED -> AccentAmber
                        }.copy(alpha = if (table.status == TableStatus.AVAILABLE) 1f else 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "T${table.number}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${table.capacity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
