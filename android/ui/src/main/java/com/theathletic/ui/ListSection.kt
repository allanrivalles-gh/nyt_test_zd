package com.theathletic.ui

interface ListSection {
    val sectionId: String
    val titleResId: Int
}

class DynamicListSection(
    override val sectionId: String,
    override val titleResId: Int
) : ListSection