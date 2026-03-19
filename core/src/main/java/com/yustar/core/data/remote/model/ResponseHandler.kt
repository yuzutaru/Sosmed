package com.yustar.core.data.remote.model

import com.google.gson.Gson
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException

/**
 * Created by Yustar Pramudana on 23/09/22.
 */

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1)
}

open class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> {
                if (e.code() != 502) {
                    if (e.response() != null) {
                        if (e.response()!!.errorBody() != null) {
                            val error = Gson().fromJson(e.response()!!.errorBody()!!.string(),
                                Error::class.java)

                            error.errorMsg?.let {
                                return Resource.error(null, error.errorMsg)
                            }

                            error.detail?.let {
                                return Resource.error(null, error.detail)
                            }
                        }
                    }
                }

                return Resource.error(null, getErrorMessage(e.code()))
            }
            is SocketTimeoutException -> Resource.error(null,
                getErrorMessage(ErrorCodes.SocketTimeOut.code))
            else -> Resource.error(null, getErrorMessage(Int.MAX_VALUE))
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "Unauthorised"
            403 -> "Authentication credentials were not provided"
            404 -> "Not found"
            502 -> "Unexpected Server Error"
            else -> "Something went wrong"
        }
    }
}