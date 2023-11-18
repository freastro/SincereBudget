package com.sincerepost.budget.account

import kotlinx.serialization.Serializable

@Serializable
data class Account(val accountName: String, val vendorName: String, val vendorUrl: String,
                   val userName: String)
