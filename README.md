# POS Customer Interface - Android (Kotlin)

A modern Android POS (Point of Sale) Customer Interface built with **Kotlin** and **Jetpack Compose**. This app allows restaurant customers to browse menus, place orders, pay via M-Pesa, and track their order status in real-time.

## Features

### 1. Home Screen with Order Status
- **Active Order Visibility**: Customers can see their current order status directly on the home screen
- **Auto-dismiss**: Order status card automatically disappears once the meal is served
- **Quick Actions**: Easy access to menu, orders, and waiter call

### 2. Table Selection
- Visual table layout showing availability
- Color-coded status: Available (Green), Occupied (Red), Reserved (Amber)
- Table capacity display

### 3. Menu Browsing
- Category-based menu (Starters, Main Course, Desserts, Beverages)
- Search functionality
- Popular and New item badges
- High-quality food images

### 4. Shopping Cart
- Add/remove items
- Quantity adjustment
- Special instructions per item
- Promo code support
- Real-time price calculation

### 5. M-Pesa Integration
- **STK Push**: Payment prompt sent directly to customer's phone
- Phone number input with validation
- Payment status polling
- Support for Card and Cash payments as alternatives

### 6. Order Tracking
- Real-time order status updates
- Visual progress indicator
- Estimated time remaining
- Order details with itemized list

### 7. Kitchen Interface Ready
- Architecture designed for future kitchen interface integration
- API service interfaces defined
- WebSocket support ready for real-time updates

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Preferences**: DataStore
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Async Operations**: Kotlin Coroutines + Flow

## Project Structure

```
app/src/main/java/com/pos/customer/
├── data/
│   ├── local/           # Room Database, DAOs, Preferences
│   ├── model/           # Data models (Order, MenuItem, etc.)
│   ├── remote/          # API services (M-Pesa, Kitchen)
│   └── repository/      # Repository implementations
├── di/                  # Hilt dependency injection modules
├── ui/
│   ├── screens/         # Compose screens
│   ├── components/      # Reusable UI components
│   └── theme/           # App theme (colors, typography)
├── viewmodel/           # ViewModels for screens
└── MainActivity.kt      # Entry point
```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### 1. Clone and Open
```bash
cd pos-android
```

Open the project in Android Studio.

### 2. Configure M-Pesa (Optional)
To enable real M-Pesa payments, update the following in `di/AppModule.kt`:

```kotlin
@Provides
@Singleton
@Named("mpesaRetrofit")
fun provideMpesaRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.safaricom.co.ke/") // Production URL
        // ...
    }
}
```

Add your M-Pesa credentials in `repository/OrderRepository.kt`:
- Business Shortcode
- Passkey
- Consumer Key
- Consumer Secret

### 3. Configure Kitchen Interface (Optional)
Update the Kitchen API base URL in `di/AppModule.kt`:

```kotlin
@Provides
@Singleton
@Named("kitchenRetrofit")
fun provideKitchenRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://your-kitchen-api.com/")
        // ...
    }
}
```

### 4. Build and Run
```bash
./gradlew assembleDebug
```

Or use Android Studio's Run button.

## Key Features Explained

### Order Status on Home Screen
```kotlin
// HomeViewModel.kt
val currentOrder = selectedTable?.let { table ->
    activeOrders.find { it.tableId == table.id && it.isActive }
}
```

The home screen shows the active order with a progress bar. Once the order status changes to `SERVED`, the card automatically disappears.

### M-Pesa STK Push
```kotlin
// OrderRepository.kt
suspend fun initiateMpesaPayment(
    orderId: String,
    phoneNumber: String,
    amount: Double
): Result<String> {
    // Formats phone number and initiates STK push
    // Customer receives prompt on their phone
}
```

### Kitchen Interface Integration
The `KitchenApiService` interface defines all endpoints needed for kitchen communication:
- Table management
- Menu synchronization
- Order creation and status updates
- Real-time updates via WebSocket (to be implemented)

## Screenshots

| Home | Table Selection | Menu | Cart | Checkout | Order Status |
|------|----------------|------|------|----------|--------------|
| Shows active order | Select table | Browse items | Review order | M-Pesa payment | Track progress |

## Customization

### Adding New Menu Items
Update the `getMockMenuItems()` function in `MenuRepository.kt`:

```kotlin
MenuItem(
    id = "unique-id",
    name = "Item Name",
    description = "Description",
    price = 19.99,
    category = CategoryType.MAIN_COURSE,
    imageUrl = "https://your-image-url.com",
    isAvailable = true,
    isPopular = true
)
```

### Changing Colors
Update colors in `ui/theme/Color.kt`:

```kotlin
val PrimaryGreen = Color(0xFF10B981)
val AccentOrange = Color(0xFFF97316)
// ...
```

## API Documentation

### M-Pesa Endpoints
- `POST /mpesa/stkpush/v1/processrequest` - Initiate payment
- `POST /mpesa/stkpushquery/v1/query` - Query payment status

### Kitchen Interface Endpoints
- `GET /api/tables` - Get all tables
- `POST /api/orders` - Create order
- `PUT /api/orders/{id}/status` - Update order status

## License

MIT License - feel free to use for commercial or personal projects.

## Support

For issues or feature requests, please create an issue in the repository.
