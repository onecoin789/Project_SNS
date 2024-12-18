package com.example.project_sns.domain.entity

data class MessageBodyEntity(
    val message: NotificationData
)

data class NotificationData(
    val token: String,
    val notification: MessageBody
)

data class MessageBody(
    val title: String,
    val body: String
)