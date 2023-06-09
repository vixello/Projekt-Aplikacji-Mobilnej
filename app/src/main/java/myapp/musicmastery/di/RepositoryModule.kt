package myapp.musicmastery.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.grpc.Context.Storage
import myapp.musicmastery.data.repository.*
import myapp.musicmastery.util.FirebaseStorageConstants
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    //instance of goalrepository
    fun provideGoalRepository(
        // FirebaseFirestore arleady createdin Firebase module.
        // It has to be mentioned in some place here for dagger hilt to not throw an error
        database: FirebaseFirestore
    ): GoalRepository{
        return GoalImpRepository(database)
    }
    @Provides
    @Singleton
    fun provideRecordingRepository(
        // FirebaseFirestore arleady createdin Firebase module.
        // It has to be mentioned in some place here for dagger hilt to not throw an error
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): RecordingRepository{
        return RecordingImpRepository(database, storageReference)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthenticationRepository{
        return AuthImpRepository(auth,database,appPreferences, gson)
    }

}