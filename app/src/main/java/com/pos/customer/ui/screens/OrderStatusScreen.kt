package com.pos.customer.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.OrderStatus
import com.pos.customer.ui.theme.AccentAmber
import com.pos.customer.ui.theme.AccentBlue
import com.pos.customer.ui.theme.AccentOrange
import com.pos.customer.ui.theme.AccentPurple
import com.pos.customer.ui.theme.PrimaryGreen
import com.pos.customer.ui.theme.SuccessGreen
import com.pos.customer.viewmodel.OrderStatusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: OrderStatusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load order when screen opens
    androidx.compose.runtime.LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Order Status",
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
                actions = {
                    IconButton(onClick = { viewModel.refreshStatus() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (uiState.order == null) {
            EmptyOrderView(onNavigateHome = onNavigateToHome)
        } else {
            OrderStatusContent(
                modifier = Modifier.padding(paddingValues),
                order = uiState.order!!,
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}

@Composable
fun EmptyOrderView(onNavigateHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Order Not Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "The order you're looking for doesn't exist",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateHome,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back to Home")
        }
    }
}

@Composable
fun OrderStatusContent(
    modifier: Modifier = Modifier,
    order: Order,
    onNavigateToHome: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Status Card
        StatusCard(order = order)

        // Order Details
        OrderDetailsCard(order = order)

        // Actions
        if (order.status == OrderStatus.SERVED) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your order has been served!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Thank you for dining with us. Enjoy your meal!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToHome,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back to Home")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatusCard(order: Order) {
    val steps = listOf(
        OrderStatusStep(
            status = OrderStatus.PENDING,
            label = "Order Received",
            icon = Icons.Default.CheckCircle,
            color = SuccessGreen
        ),
        OrderStatusStep(
            status = OrderStatus.CONFIRMED,
            label = "Confirmed",
            icon = Icons.Default.Info,
            color = AccentBlue
        ),
        OrderStatusStep(
            status = OrderStatus.PREPARING,
            label = "Preparing",
            icon = Icons.Default.Restaurant,
            color = AccentOrange
        ),
        OrderStatusStep(
            status = OrderStatus.READY,
            label = "Ready",
            icon = Icons.Default.ShoppingCart,
            color = AccentPurple
        ),
        OrderStatusStep(
            status = OrderStatus.SERVED,
            label = "Served",
            icon = Icons.Default.CheckCircle,
            color = SuccessGreen
        )
    )

    val currentStepIndex = steps.indexOfFirst { it.status == order.status }
    val progress = if (currentStepIndex >= 0) {
        (currentStepIndex + 1).toFloat() / steps.size
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Progress Bar
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryGreen,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status Steps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, step ->
                    val isActive = index <= currentStepIndex
                    val isCurrent = index == currentStepIndex

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> step.color
                                        isActive -> step.color.copy(alpha = 0.5f)
                                        else -> Color.LightGray.copy(alpha = 0.3f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = if (isActive) Color.White else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isActive) step.color else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Status Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (order.status) {
                        OrderStatus.PENDING -> AccentAmber.copy(alpha = 0.1f)
                        OrderStatus.CONFIRMED -> AccentBlue.copy(alpha = 0.1f)
                        OrderStatus.PREPARING -> AccentOrange.copy(alpha = 0.1f)
                        OrderStatus.READY -> AccentPurple.copy(alpha = 0.1f)
                        OrderStatus.SERVED -> SuccessGreen.copy(alpha = 0.1f)
                        OrderStatus.CANCELLED -> Color.Red.copy(alpha = 0.1f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getOrderStatusText(order.status),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
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

            // Estimated Time
            if (order.status != OrderStatus.SERVED && order.status != OrderStatus.CANCELLED) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = AccentAmber.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AccentAmber
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Estimated time: ${order.estimatedTimeMinutes} minutes",
                            color = AccentAmber,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

data class OrderStatusStep(
    val status: OrderStatus,
    val label: String,
    val icon: ImageVector,
    val color: Color
)

fun getOrderStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Order Received"
        OrderStatus.CONFIRMED -> "Order Confirmed"
        OrderStatus.PREPARING -> "Being Prepared"
        OrderStatus.READY -> "Ready for Pickup"
        OrderStatus.SERVED -> "Order Served"
        OrderStatus.CANCELLED -> "Order Cancelled"
    }
}

@Composable
fun OrderDetailsCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Order Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.menuItem.imageUrl,
                        contentDescription = item.menuItem.name,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.menuItem.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "x${item.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "$${String.format("%.2f", item.totalPrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.Divider()
            Spacer(modifier = Modifier.height(12.dp))

            // Totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text("$${String.format("%.2f", order.subtotal)}", style = MaterialTheme.typography.bodyMedium)
            }

            if (order.discount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Discount", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(
                        "-$${String.format("%.2f", order.discount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tax", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text("$${String.format("%.2f", order.tax)}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$${String.format("%.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Table Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Table Number",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            "${order.tableNumber}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Order Time",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            order.createdAt.toString().substring(11, 16),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
