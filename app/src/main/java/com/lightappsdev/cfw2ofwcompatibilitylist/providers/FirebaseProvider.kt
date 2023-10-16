package com.lightappsdev.cfw2ofwcompatibilitylist.providers

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseProvider {

    @Singleton
    @Provides
    fun providesFirebaseStorage(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }
}