package com.makeforge.forgefit.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.makeforge.forgefit.BuildConfig
import com.makeforge.forgefit.data.local.database.ForgeFitDatabase
import com.makeforge.forgefit.network.OpenRouterApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                .addHeader("HTTP-Referer", "https://github.com/viperxc24odmaker/ForgeFIt1.0")
                .addHeader("X-Title", "ForgeFit")
                .build()
            chain.proceed(request)
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenRouterApiService(okHttpClient: OkHttpClient, gson: Gson): OpenRouterApiService {
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OpenRouterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ForgeFitDatabase {
        return Room.databaseBuilder(
            context,
            ForgeFitDatabase::class.java,
            "forgefit.db"
        )
            .addMigrations(ForgeFitDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(db: ForgeFitDatabase) = db.workoutDao()

    @Provides
    @Singleton
    fun provideJogDao(db: ForgeFitDatabase) = db.jogDao()
}
