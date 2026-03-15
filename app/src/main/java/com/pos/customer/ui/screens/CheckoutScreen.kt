package com.pos.customer.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.customer.data.model.PaymentMethod
import com.pos.customer.ui.theme.AccentBlue
import com.pos.customer.ui.theme.AccentGreen
import com.pos.customer.ui.theme.AccentOrange
import com.pos.customer.ui.theme.PrimaryGreen
import com.pos.customer.ui.theme.SuccessGreen
import com.pos.customer.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOrderStatus: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
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
        AnimatedContent(
            targetState = uiState.paymentComplete,
            label = "checkout_content"
        ) { isComplete ->
            if (isComplete && uiState.order != null) {
                PaymentSuccessScreen(
                    orderId = uiState.order!!.id,
                    onTrackOrder = { onNavigateToOrderStatus(uiState.order!!.id) },
                    onOrderMore = onNavigateToHome
                )
            } else {
                CheckoutForm(
                    modifier = Modifier.padding(paddingValues),
                    uiState = uiState,
                    cartItems = cartItems,
                    subtotal = viewModel.subtotal,
                    discount = viewModel.discount,
                    tax = viewModel.tax,
                    total = viewModel.total,
                    onPaymentMethodChange = { viewModel.selectPaymentMethod(it) },
                    onPhoneChange = { viewModel.updatePhoneNumber(it) },
                    onProcessPayment = { viewModel.processPayment() }
                )
            }
        }
    }
}

@Composable
fun CheckoutForm(
    modifier: Modifier = Modifier,
    uiState: CheckoutViewModel.CheckoutUiState,
    cartItems: List<com.pos.customer.data.model.CartItem>,
    subtotal: Double,
    discount: Double,
    tax: Double,
    total: Double,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onPhoneChange: (String) -> Unit,
    onProcessPayment: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Payment Method Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // M-Pesa Option
                PaymentMethodOption(
                    icon = "📱",
                    title = "M-Pesa",
                    subtitle = "Pay with M-Pesa",
                    isSelected = uiState.selectedPaymentMethod == PaymentMethod.MPESA,
                    onClick = { onPaymentMethodChange(PaymentMethod.MPESA) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card Option
                PaymentMethodOption(
                    icon = "💳",
                    title = "Card",
                    subtitle = "Credit or Debit card",
                    isSelected = uiState.selectedPaymentMethod == PaymentMethod.CARD,
                    onClick = { onPaymentMethodChange(PaymentMethod.CARD) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cash Option
                PaymentMethodOption(
                    icon = "💵",
                    title = "Cash",
                    subtitle = "Pay at counter",
                    isSelected = uiState.selectedPaymentMethod == PaymentMethod.CASH,
                    onClick = { onPaymentMethodChange(PaymentMethod.CASH) }
                )
            }
        }

        // M-Pesa Phone Input
        if (uiState.selectedPaymentMethod == PaymentMethod.MPESA) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "M-Pesa Phone Number",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.phoneNumber,
                        onValueChange = onPhoneChange,
                        placeholder = { Text("e.g., 0712345678") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "You will receive an M-Pesa prompt on your phone",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Order Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Order Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Items count
                Text(
                    "${cartItems.sumOf { it.quantity }} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodyMedium)
                }

                if (discount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(
                            "-$${String.format("%.2f", discount)}",
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
                    Text("Tax (8%)", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("$${String.format("%.2f", tax)}", style = MaterialTheme.typography.bodyMedium)
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
                        "$${String.format("%.2f", total)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        if (uiState.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = uiState.error,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Pay Button
        Button(
            onClick = onProcessPayment,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isProcessing && !uiState.isWaitingForMpesa
        ) {
            if (uiState.isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else if (uiState.isWaitingForMpesa) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Waiting for M-Pesa confirmation...")
                }
            } else {
                Text(
                    "Pay $${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PaymentMethodOption(
    icon: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.background
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            2.dp,
            PrimaryGreen
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun PaymentSuccessScreen(
    orderId: String,
    onTrackOrder: () -> Unit,
    onOrderMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuccessGreen.copy(alpha = 0.1f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(SuccessGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Order Confirmed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Your order #$orderId has been placed successfully",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Order Status Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatusStep(
                        icon = Icons.Default.CheckCircle,
                        label = "Confirmed",
                        isActive = true,
                        color = SuccessGreen
                    )
                    StatusStep(
                        icon = Icons.Default.Info,
                        label = "Preparing",
                        isActive = false,
                        color = AccentOrange
                    )
                    StatusStep(
                        icon = Icons.Default.CheckCircle,
                        label = "Ready",
                        isActive = false,
                        color = AccentBlue
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = AccentOrange.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AccentOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Estimated time: 25 minutes",
                            color = AccentOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onTrackOrder,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Track Order")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onOrderMore,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray.copy(alpha = 0.3f),
                contentColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Order More")
        }
    }
}

@Composable
fun StatusStep(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) color else Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) Color.White else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive) color else Color.Gray
        )
    }
}
