package com.pos.customer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.OrderStatus
import com.pos.customer.data.model.Table
import com.pos.customer.ui.theme.AccentAmber
import com.pos.customer.ui.theme.AccentBlue
import com.pos.customer.ui.theme.AccentOrange
import com.pos.customer.ui.theme.AccentPurple
import com.pos.customer.ui.theme.PrimaryGreen
import com.pos.customer.ui.theme.SuccessGreen
import com.pos.customer.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTableSelection: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToOrderStatus: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Restaurant POS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.selectedTable != null) {
                FloatingActionButton(
                    onClick = onNavigateToCart,
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Active Order Status Card (Visible only when there's an active order)
            AnimatedVisibility(
                visible = uiState.hasActiveOrder,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                uiState.currentOrder?.let { order ->
                    ActiveOrderCard(
                        order = order,
                        onClick = { onNavigateToOrderStatus(order.id) },
                        onRefresh = { viewModel.refreshOrderStatus() }
                    )
                }
            }

            // Table Selection Section
            if (uiState.selectedTable == null) {
                NoTableSelectedCard(onSelectTable = onNavigateToTableSelection)
            } else {
                SelectedTableCard(
                    table = uiState.selectedTable,
                    onChangeTable = onNavigateToTableSelection,
                    onBrowseMenu = onNavigateToMenu
                )
            }

            // Quick Actions
            QuickActionsSection(
                onBrowseMenu = onNavigateToMenu,
                onViewOrders = { /* TODO */ },
                onCallWaiter = { /* TODO */ }
            )
        }
    }
}

@Composable
fun ActiveOrderCard(
    order: Order,
    onClick: () -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when (order.status) {
                OrderStatus.PENDING -> AccentAmber.copy(alpha = 0.1f)
                OrderStatus.CONFIRMED -> AccentBlue.copy(alpha = 0.1f)
                OrderStatus.PREPARING -> AccentOrange.copy(alpha = 0.1f)
                OrderStatus.READY -> AccentPurple.copy(alpha = 0.1f)
                OrderStatus.SERVED -> SuccessGreen.copy(alpha = 0.1f)
                OrderStatus.CANCELLED -> Color.Red.copy(alpha = 0.1f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                when (order.status) {
                                    OrderStatus.PENDING -> AccentAmber
                                    OrderStatus.CONFIRMED -> AccentBlue
                                    OrderStatus.PREPARING -> AccentOrange
                                    OrderStatus.READY -> AccentPurple
                                    OrderStatus.SERVED -> SuccessGreen
                                    OrderStatus.CANCELLED -> Color.Red
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (order.status) {
                                OrderStatus.PENDING -> Icons.Default.Notifications
                                OrderStatus.CONFIRMED -> Icons.Default.CheckCircle
                                OrderStatus.PREPARING -> Icons.Default.Restaurant
                                OrderStatus.READY -> Icons.Default.ShoppingCart
                                OrderStatus.SERVED -> Icons.Default.CheckCircle
                                OrderStatus.CANCELLED -> Icons.Default.Notifications
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Order #${order.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getStatusText(order.status),
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (order.status) {
                                OrderStatus.PENDING -> AccentAmber
                                OrderStatus.CONFIRMED -> AccentBlue
                                OrderStatus.PREPARING -> AccentOrange
                                OrderStatus.READY -> AccentPurple
                                OrderStatus.SERVED -> SuccessGreen
                                OrderStatus.CANCELLED -> Color.Red
                            }
                        )
                    }
                }
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress indicator
            OrderProgressBar(status = order.status)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.items.sumOf { it.quantity }} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "$${String.format("%.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderProgressBar(status: OrderStatus) {
    val steps = listOf(
        OrderStatus.PENDING,
        OrderStatus.CONFIRMED,
        OrderStatus.PREPARING,
        OrderStatus.READY,
        OrderStatus.SERVED
    )
    val currentStep = steps.indexOf(status)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        if (index <= currentStep) PrimaryGreen else Color.LightGray,
                        RoundedCornerShape(2.dp)
                    )
            )
            if (index < steps.size - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Order Received"
        OrderStatus.CONFIRMED -> "Confirmed"
        OrderStatus.PREPARING -> "Preparing"
        OrderStatus.READY -> "Ready for Pickup"
        OrderStatus.SERVED -> "Served"
        OrderStatus.CANCELLED -> "Cancelled"
    }
}

@Composable
fun NoTableSelectedCard(onSelectTable: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onSelectTable),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PrimaryGreen.copy(alpha = 0.2f),
                                PrimaryGreen.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Your Table",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose a table to start ordering",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSelectTable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Select Table")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun SelectedTableCard(
    table: Table,
    onChangeTable: () -> Unit,
    onBrowseMenu: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "T${table.number}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Table ${table.number}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${table.capacity} guests",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = onChangeTable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.3f),
                        contentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Change")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBrowseMenu,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Browse Menu")
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onBrowseMenu: () -> Unit,
    onViewOrders: () -> Unit,
    onCallWaiter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                icon = Icons.Default.Restaurant,
                label = "Menu",
                onClick = onBrowseMenu,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            QuickActionButton(
                icon = Icons.Default.ShoppingCart,
                label = "Orders",
                onClick = onViewOrders,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            QuickActionButton(
                icon = Icons.Default.Notifications,
                label = "Call Waiter",
                onClick = onCallWaiter,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryGreen,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
