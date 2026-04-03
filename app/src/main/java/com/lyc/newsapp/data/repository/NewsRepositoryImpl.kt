package com.lyc.newsapp.data.repository

import com.lyc.newsapp.core.result.Resource
import com.lyc.newsapp.data.mapper.toNews
import com.lyc.newsapp.data.remote.NewsApi
import com.lyc.newsapp.domain.model.News
import com.lyc.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi
) : NewsRepository {
    override suspend fun getNewsList(
        language: String,
        country: String,
    ): Flow<Resource<List<News>>> {
        return flow {
            emit(Resource.Loading(true))

            val newsResponse = try {
                newsApi.getLatestNews()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("无法连接服务器，请检查网络连接"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("服务器返回错误: ${e.code()}"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("加载过程中发生错误: ${e.message}"))
                emit(Resource.Loading(false))
                return@flow
            }
            val nextPage = newsResponse.nextPage
            val newsDtoList = newsResponse.results
            newsDtoList?.let { newsList ->
                val remoteList = newsList.map {
                    it.toNews(nextPage = nextPage!!)
                }
                emit(Resource.Success(remoteList))
                emit(Resource.Loading(false))
            }

        }
    }

    override suspend fun getNewsById(
        id: String,
        language: String,
        country: String,
    ): Flow<Resource<News>> {
        return flow {
            emit(Resource.Loading(true))

            val newsResponse = try {
                newsApi.getNewsById(id = id)
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }
            val newsDtoList = newsResponse.results
            newsDtoList?.let { newsList ->
                if (newsList.isNotEmpty()) {
                    // 使用空字符串作为默认值，避免 nextPage 为 null 时的崩溃
                    val news = newsList[0].toNews(nextPage = newsResponse.nextPage ?: "")
                    emit(Resource.Success(news))
                } else {
                    emit(Resource.Error("未找到新闻"))
                }
            } ?: run {
                emit(Resource.Error("未找到新闻"))
            }
            emit(Resource.Loading(false))

        }
    }

    override suspend fun getNewsListByCategory(
        category: String,
        language: String,
        country: String,
    ): Flow<Resource<List<News>>> {
        return flow {
            emit(Resource.Loading(true))

            val newsResponse = try {
                newsApi.getNewsByCategory(
                    category = category,
                    language = language,
                    country = country
                )
            }  catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("无法连接服务器，请检查网络连接"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("服务器返回错误: ${e.code()}"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("加载过程中发生错误: ${e.message}"))
                emit(Resource.Loading(false))
                return@flow
            }
            val nextPage = newsResponse.nextPage
            val newsDtoList = newsResponse.results
            newsDtoList?.let { newsList ->
                val remoteList = newsList.map {
                    it.toNews(nextPage = nextPage?:"")
                }
                emit(Resource.Success(remoteList))
                emit(Resource.Loading(false))

            }

        }
    }

    override suspend fun getNextPage(
        category: String,
        nextPage: String,
        language: String,
        country: String,
    ): Flow<Resource<List<News>>> {
        return flow {
            emit(Resource.Loading(true))
            val newsResponse = try {
                if(category == "all"){
                    newsApi.getAllNewsListNextPage(page = nextPage)
                } else{
                    newsApi.getNextPage(
                        category = category,
                        page = nextPage,
                        language = language,
                        country = country
                    )
                }
            }  catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("无法连接服务器，请检查网络连接"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("服务器返回错误: ${e.code()}"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("加载过程中发生错误: ${e.message}"))
                emit(Resource.Loading(false))
                return@flow
            }
            val nextPage1 = newsResponse.nextPage
            val newsDtoList = newsResponse.results
            newsDtoList?.let { newsList ->
                val remoteList = newsList.map {
                    it.toNews(nextPage = nextPage1!!)
                }
                emit(Resource.Success(remoteList))
                emit(Resource.Loading(false))

            }
        }
    }

    override suspend fun searchNews(
        query: String,
        language: String,
        country: String,
    ): Flow<Resource<List<News>>> {
        return flow {
            emit(Resource.Loading(true))

            val newsResponse = try {
                newsApi.searchNews(
                    q = query,
                    language = language,
                    country = country
                )
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("无法连接服务器，请检查网络连接"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("服务器返回错误: ${e.code()}"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("搜索过程中发生错误: ${e.message}"))
                emit(Resource.Loading(false))
                return@flow
            }
            
            val nextPage = newsResponse.nextPage
            val newsDtoList = newsResponse.results
            
            if (newsDtoList.isNullOrEmpty()) {
                // 搜索结果为空
                emit(Resource.Success(emptyList()))
                emit(Resource.Loading(false))
            } else {
                // 搜索结果不为空
                val remoteList = newsDtoList.map {
                    it.toNews(nextPage = nextPage ?: "") // 使用空字符串代替 null
                }
                emit(Resource.Success(remoteList))
                emit(Resource.Loading(false))
            }
        }
    }

}
