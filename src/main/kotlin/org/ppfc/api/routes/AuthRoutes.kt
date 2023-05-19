package org.ppfc.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.ppfc.api.model.auth.request.AuthNewPasswordRequiredChallengeRequest
import org.ppfc.api.model.auth.request.AuthRequest
import org.ppfc.api.model.auth.request.RefreshAccessTokenRequest
import org.ppfc.api.model.auth.response.AuthResponse
import org.ppfc.api.model.auth.response.AuthResponseStatus
import org.ppfc.api.security.auth.*

fun Route.authRouting() {
    val authProvider: AuthProvider by inject()

    post("/authenticate") {
        val authRequest = call.receive<AuthRequest>()

        when (
            val result = authProvider.auth(
                username = authRequest.username,
                password = authRequest.password
            )
        ) {
            is AuthResult.Success -> {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        status = AuthResponseStatus.SUCCESS,
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken
                    )
                )
            }

            is AuthResult.Failure -> {
                when (result.error) {
                    is AuthError.InternalError -> {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = AuthResponse(
                                status = AuthResponseStatus.FAILURE,
                                error = result.error.message
                            )
                        )
                    }

                    is AuthError.NotAuthorized -> {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = AuthResponse(
                                status = AuthResponseStatus.FAILURE,
                                error = result.error.message
                            )
                        )
                    }
                }
            }

            is AuthResult.NewPasswordRequired -> {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        status = AuthResponseStatus.NEW_PASSWORD_REQUIRED,
                        session = result.session
                    )
                )
            }
        }
    }

    post("/authNewPasswordRequiredChallenge") {
        val challengeRequest = call.receive<AuthNewPasswordRequiredChallengeRequest>()

        when (
            val result = authProvider.authChallengeSetNewPassword(
                username = challengeRequest.username,
                password = challengeRequest.password,
                session = challengeRequest.session
            )
        ) {
            is AuthChallengeSetNewPasswordResult.Success -> {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        status = AuthResponseStatus.SUCCESS,
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken
                    )
                )
            }

            is AuthChallengeSetNewPasswordResult.Failure -> {
                when (result.error) {
                    is AuthError.InternalError -> {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = AuthResponse(
                                status = AuthResponseStatus.FAILURE,
                                error = result.error.message
                            )
                        )
                    }

                    is AuthError.NotAuthorized -> {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = AuthResponse(
                                status = AuthResponseStatus.FAILURE,
                                error = result.error.message
                            )
                        )
                    }
                }
            }
        }
    }

    post("/refreshAccessToken") {
        val refreshAccessTokenRequest = call.receive<RefreshAccessTokenRequest>()

        when (
            val result = authProvider.refreshAccessToken(
                refreshToken = refreshAccessTokenRequest.refreshToken
            )
        ) {
            is RefreshAccessTokenResult.Success -> {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        status = AuthResponseStatus.SUCCESS,
                        accessToken = result.accessToken,
                        refreshToken = refreshAccessTokenRequest.refreshToken
                    )
                )
            }

            is RefreshAccessTokenResult.Failure -> {
                when (result.error) {
                    is AuthError.InternalError -> {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = AuthResponse(
                                status = AuthResponseStatus.FAILURE,
                                error = result.error.message
                            )
                        )
                    }

                    is AuthError.NotAuthorized -> {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = AuthResponse(
                                status = AuthResponseStatus.FAILURE,
                                error = result.error.message
                            )
                        )
                    }
                }
            }
        }
    }
}