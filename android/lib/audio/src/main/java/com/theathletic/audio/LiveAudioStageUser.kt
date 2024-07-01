package com.theathletic.audio

import kotlin.math.abs

class LiveAudioStageUser(
    val id: String,
    val isMuted: Boolean = false,
)

fun getAnonymousColor(id: String) = colors[abs(id.toInt()).rem(colors.size)]
fun getAnonymousAnimal(id: String) = animals[abs(id.toInt()).rem(animals.size)]

private val colors = listOf(
    "Red",
    "Orange",
    "Yellow",
    "Green",
    "Blue",
    "Indigo",
    "Violet",
    "Teal",
    "Gold",
    "Silver",
    "Pink",
    "Grey",
)

private val animals = listOf(
    "Giraffe",
    "Elephant",
    "Horse",
    "Dog",
    "Cat",
    "Mouse",
    "T-Rex",
    "Flounder",
    "Lizard",
    "Rhino",
    "Eagle",
    "Martian",
    "Dolphin",
)