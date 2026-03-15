package com.pos.customer.di

import android.content.Context
import androidx.room.Room
import com.pos.customer.data.local.POSDatabase
import com.pos.customer.data.remote.KitchenApiService
import com.pos.customer.data.remote.MpesaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): POSDatabase {
        return Room.databaseBuilder(
            context,
            POSDatabase::class.java,
            "pos_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMenuDao(database: POSDatabase) = database.menuDao()

    @Provides
    @Singleton
    fun provideOrderDao(database: POSDatabase) = database.orderDao()

    @Provides
    @Singleton
    fun provideTableDao(database: POSDatabase) = database.tableDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("kitchenRetrofit")
    fun provideKitchenRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://your-kitchen-api.com/") // Replace with actual API URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideKitchenApiService(@Named("kitchenRetrofit") retrofit: Retrofit): KitchenApiService {
        return retrofit.create(KitchenApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("mpesaRetrofit")
    fun provideMpesaRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://sandbox.safaricom.co.ke/") // Use production URL for live
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMpesaApiService(@Named("mpesaRetrofit") retrofit: Retrofit): MpesaApiService {
        return retrofit.create(MpesaApiService::class.java)
    }
}
