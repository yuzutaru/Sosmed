package com.yustar.core.data.remote.model

/**
 * Created by Yustar Pramudana on 23/09/22.
 */

data class Resource<out T>(val status: Status?, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: T?, message: String?): Resource<T> {
            return Resource(Status.ERROR, error, message)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data,null)
        }
    }
}