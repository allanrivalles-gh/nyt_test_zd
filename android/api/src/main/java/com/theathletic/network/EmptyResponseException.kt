package com.theathletic.network

import java.io.IOException

class EmptyResponseException(
    override val message: String = "Unexpected Empty Response"
) : IOException()