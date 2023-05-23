package myapp.musicmastery.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import myapp.musicmastery.data.repository.AuthImpRepository
import myapp.musicmastery.data.repository.AuthenticationRepository
import myapp.musicmastery.data.repository.GoalRepository
import myapp.musicmastery.data.repository.GoalImpRepository
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
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth
    ): AuthenticationRepository{
        return AuthImpRepository(auth,database)
    }
}