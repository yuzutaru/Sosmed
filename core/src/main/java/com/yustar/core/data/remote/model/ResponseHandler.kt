package com.yustar.core.data.remote.model

import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
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

    fun <T : Any> handleResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            Resource.success(response.body())
        } else {
            val errorBody = response.errorBody()?.string()
            val error = try {
                Gson().fromJson(errorBody, Error::class.java)
            } catch (e: Exception) {
                null
            }
            val message = error?.errorMsg ?: error?.detail ?: getErrorMessage(response.code())
            Resource.error(null, message)
        }
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> {
                if (e.code() != 502) {
                    if (e.response() != null) {
                        if (e.response()!!.errorBody() != null) {
                            val error = try {
                                Gson().fromJson(
                                    e.response()!!.errorBody()!!.string(),
                                    Error::class.java
                                )
                            } catch (ex: Exception) {
                                null
                            }

                            error?.errorMsg?.let {
                                return Resource.error(null, it)
                            }

                            error?.detail?.let {
                                return Resource.error(null, it)
                            }
                        }
                    }
                }

                return Resource.error(null, getErrorMessage(e.code()))
            }

            is SocketTimeoutException -> Resource.error(
                null,
                getErrorMessage(ErrorCodes.SocketTimeOut.code)
            )

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