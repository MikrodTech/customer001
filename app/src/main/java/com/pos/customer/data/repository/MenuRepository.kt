package com.pos.customer.data.repository

import com.pos.customer.data.local.MenuDao
import com.pos.customer.data.model.Category
import com.pos.customer.data.model.CategoryType
import com.pos.customer.data.model.MenuItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val menuDao: MenuDao
) {
    val categories = listOf(
        Category(CategoryType.STARTERS, "Starters", "🥗"),
        Category(CategoryType.MAIN_COURSE, "Main Course", "🍽️"),
        Category(CategoryType.DESSERTS, "Desserts", "🍰"),
        Category(CategoryType.BEVERAGES, "Beverages", "🥤")
    )

    fun getAllMenuItems(): Flow<List<MenuItem>> = menuDao.getAllMenuItems()

    fun getMenuItemsByCategory(category: CategoryType): Flow<List<MenuItem>> = 
        menuDao.getMenuItemsByCategory(category)

    fun searchMenuItems(query: String): Flow<List<MenuItem>> = 
        menuDao.searchMenuItems(query)

    fun getPopularItems(): Flow<List<MenuItem>> = menuDao.getPopularItems()

    fun getNewItems(): Flow<List<MenuItem>> = menuDao.getNewItems()

    suspend fun getMenuItemById(id: String): MenuItem? = menuDao.getMenuItemById(id)

    suspend fun seedMenuItems() {
        val items = getMockMenuItems()
        menuDao.insertAll(items)
    }

    private fun getMockMenuItems(): List<MenuItem> = listOf(
        // Starters
        MenuItem(
            id = "s1",
            name = "Caesar Salad",
            description = "Fresh romaine lettuce with parmesan, croutons, and classic Caesar dressing",
            price = 12.99,
            category = CategoryType.STARTERS,
            imageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=400&h=400&fit=crop",
            isAvailable = true,
            isPopular = true
        ),
        MenuItem(
            id = "s2",
            name = "Sushi Platter",
            description = "Assorted fresh sushi rolls with wasabi and pickled ginger",
            price = 24.99,
            category = CategoryType.STARTERS,
            imageUrl = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400&h=400&fit=crop",
            isAvailable = true,
            isNew = true
        ),
        MenuItem(
            id = "s3",
            name = "Bruschetta",
            description = "Grilled bread topped with fresh tomatoes, garlic, and basil",
            price = 9.99,
            category = CategoryType.STARTERS,
            imageUrl = "https://images.unsplash.com/photo-1572695157363-bc31c5dd3c8b?w=400&h=400&fit=crop",
            isAvailable = true
        ),
        MenuItem(
            id = "s4",
            name = "Chicken Wings",
            description = "Crispy wings with your choice of buffalo, BBQ, or honey garlic sauce",
            price = 14.99,
            category = CategoryType.STARTERS,
            imageUrl = "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=400&h=400&fit=crop",
            isAvailable = true,
            isPopular = true
        ),
        // Main Course
        MenuItem(
            id = "m1",
            name = "Grilled Salmon",
            description = "Atlantic salmon with lemon butter sauce and seasonal vegetables",
            price = 28.99,
            category = CategoryType.MAIN_COURSE,
            imageUrl = "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400&h=400&fit=crop",
            isAvailable = true,
            isPopular = true
        ),
        MenuItem(
            id = "m2",
            name = "Ribeye Steak",
            description = "12oz prime ribeye with mashed potatoes and grilled asparagus",
            price = 42.99,
            category = CategoryType.MAIN_COURSE,
            imageUrl = "https://images.unsplash.com/photo-1600891964092-4316c288032e?w=400&h=400&fit=crop",
            isAvailable = true
        ),
        MenuItem(
            id = "m3",
            name = "Pasta Carbonara",
            description = "Creamy pasta with pancetta, egg, and parmesan cheese",
            price = 19.99,
            category = CategoryType.MAIN_COURSE,
            imageUrl = "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=400&h=400&fit=crop",
            isAvailable = true
        ),
        MenuItem(
            id = "m4",
            name = "Chicken Tikka Masala",
            description = "Tender chicken in rich tomato cream sauce with naan bread",
            price = 22.99,
            category = CategoryType.MAIN_COURSE,
            imageUrl = "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400&h=400&fit=crop",
            isAvailable = true,
            isNew = true
        ),
        // Desserts
        MenuItem(
            id = "d1",
            name = "Chocolate Fondant",
            description = "Warm chocolate cake with a molten center, served with vanilla ice cream",
            price = 11.99,
            category = CategoryType.DESSERTS,
            imageUrl = "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=400&h=400&fit=crop",
            isAvailable = true,
            isPopular = true
        ),
        MenuItem(
            id = "d2",
            name = "Tiramisu",
            description = "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone",
            price = 9.99,
            category = CategoryType.DESSERTS,
            imageUrl = "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400&h=400&fit=crop",
            isAvailable = true
        ),
        MenuItem(
            id = "d3",
            name = "Cheesecake",
            description = "New York style cheesecake with berry compote",
            price = 10.99,
            category = CategoryType.DESSERTS,
            imageUrl = "https://images.unsplash.com/photo-1524351199678-941a58a3df26?w=400&h=400&fit=crop",
            isAvailable = true
        ),
        // Beverages
        MenuItem(
            id = "b1",
            name = "Fresh Orange Juice",
            description = "Freshly squeezed orange juice",
            price = 6.99,
            category = CategoryType.BEVERAGES,
            imageUrl = "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=400&h=400&fit=crop",
            isAvailable = true
        ),
        MenuItem(
            id = "b2",
            name = "Iced Caramel Latte",
            description = "Espresso with caramel syrup and cold milk over ice",
            price = 7.99,
            category = CategoryType.BEVERAGES,
            imageUrl = "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&h=400&fit=crop",
            isAvailable = true,
            isPopular = true
        ),
        MenuItem(
            id = "b3",
            name = "Mojito",
            description = "Refreshing mint and lime cocktail with rum",
            price = 12.99,
            category = CategoryType.BEVERAGES,
            imageUrl = "https://images.unsplash.com/photo-1551538827-9c037cb4f32a?w=400&h=400&fit=crop",
            isAvailable = true
        ),
        MenuItem(
            id = "b4",
            name = "Green Smoothie",
            description = "Blend of spinach, kale, apple, and ginger",
            price = 8.99,
            category = CategoryType.BEVERAGES,
            imageUrl = "https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=400&h=400&fit=crop",
            isAvailable = true,
            isNew = true
        )
    )
}
