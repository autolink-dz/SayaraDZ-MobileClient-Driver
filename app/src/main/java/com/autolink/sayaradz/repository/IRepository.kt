package com.autolink.sayaradz.repository

import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executor


interface IRepository{
    val networkExecutor:Executor
    val diskExecutor:Executor

    fun clear()
}
