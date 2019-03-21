package com.autolink.sayaradz.util

import android.content.Context
import com.autolink.sayaradz.api.ApiBuilder
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.repository.brand.BrandsRepository
import com.autolink.sayaradz.repository.model.ModelsRepository
import com.autolink.sayaradz.repository.tariff.TariffRepository
import com.autolink.sayaradz.repository.user.UserRepository
import com.autolink.sayaradz.repository.version.VersionsRepository
import com.autolink.sayaradz.util.RepositoryKey.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


enum class RepositoryKey{
        USER_REPOSITORY,
        BRANDS_REPOSITORY,
        MODELS_REPOSITORY,
        VERSIONS_REPOSITORY,
        TARIFF_REPOSITORY
}

interface ServiceLocator{

    companion object {

        private val LOCK = Any()
        private var instance: ServiceLocator? = null
        fun instance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(context)
                }
                return instance!!
            }
        }
    }

    fun getRepository(key:RepositoryKey): IRepository
    fun getNetworkExecutor():Executor
    fun getDiskIOExecutor():Executor
    fun getSayaraDzApi():SayaraDzApi


}


open class DefaultServiceLocator(val context:Context):ServiceLocator{

    companion object {
        private const val SAYARA_DZ_API_BASE_URL = "https://us-central1-sayaradz-75240.cloudfunctions.net/sayaraDzApi/api/v1/"
    }

    private val DISK_IO = Executors.newSingleThreadExecutor()
    private val NETWORK_IO  = Executors.newFixedThreadPool(3)

    private val restApi by lazy {
        ApiBuilder.create(SAYARA_DZ_API_BASE_URL,SayaraDzApi::class.java,context = context)
    }


    override fun getRepository(key: RepositoryKey): IRepository {
            return when(key){
                USER_REPOSITORY -> UserRepository(
                    context,
                    getNetworkExecutor(),
                    getDiskIOExecutor()
                )
                BRANDS_REPOSITORY ->  BrandsRepository(
                    getSayaraDzApi(),
                    getNetworkExecutor(),
                    getDiskIOExecutor()
                )
                MODELS_REPOSITORY ->  ModelsRepository(
                    getSayaraDzApi(),
                    getNetworkExecutor(),
                    getDiskIOExecutor()
                )
                VERSIONS_REPOSITORY -> VersionsRepository(
                    getSayaraDzApi(),
                    getNetworkExecutor(),
                    getDiskIOExecutor()
                )
                TARIFF_REPOSITORY -> TariffRepository(
                    getSayaraDzApi(),
                    getNetworkExecutor(),
                    getDiskIOExecutor()
                )
            }
    }

    override fun getNetworkExecutor() :Executor = NETWORK_IO

    override fun getDiskIOExecutor() :Executor = DISK_IO

    override fun getSayaraDzApi(): SayaraDzApi  = restApi
}