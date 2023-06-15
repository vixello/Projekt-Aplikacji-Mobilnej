package myapp.musicmastery.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import myapp.musicmastery.util.SharedPrefConst
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences(SharedPrefConst.LOCAL_SHARES_PREF, Context.MODE_PRIVATE)
    }
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}