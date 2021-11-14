package com.glushko.kodetestteask.data_layer.datasource.response

import com.glushko.kodetestteask.business_logic_layer.domain.User

data class ResponseUsers(
    var success: Boolean = true,
    val items: List<User> = listOf()
) {
}