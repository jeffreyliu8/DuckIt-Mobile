package com.jeffreyliu.duckit.hilt


import com.jeffreyliu.duckit.data.LoginDataSource
import com.jeffreyliu.duckit.data.LoginRepository
import com.jeffreyliu.duckit.ktor.PostsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePostService(): PostsService {
        return PostsService.create()
    }

    @Provides
    @Singleton
    fun provideLoginRepository(postsService: PostsService): LoginRepository {
        val dataSource = LoginDataSource(postsService)
        return LoginRepository(dataSource)
    }
}