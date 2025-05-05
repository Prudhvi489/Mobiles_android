package com.mobivault.ui.login.model

data class LoginResponse(
    val createdAt: String?="",
    val email: String?="",
    val id: Int?=0,
    val mobileNumber: String?="",
    val name: String?="",
    val token: String?=""
)