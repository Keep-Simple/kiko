package com.kiko.models

data class Reservation(var tenantId: Int? = null, var approved: Boolean = false, var banned: Boolean = false)

