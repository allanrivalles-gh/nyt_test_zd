package com.theathletic.entity.authentication

/*
"Administrator": 10
"Editor": 7
"Author": 2
"Contributor": 1
"Podcast Producer": 2
"Podcast Host": 1
"Inactive Contributor": 0
"Subscriber": 0
 */

// Tt full ranges of user level are not used now, this is set up in conservative way to categorize newly added levels to lower access group
enum class UserPrivilegeLevel(val value: Long) {
    REGULAR_USER(0),
    CONTRIBUTOR(1),
    AUTHOR(2), // Tt from 2 to 6
    EDITOR(7), // Tt from 7 to 9
    ADMINISTRATOR(10); // Tt 10+

    companion object {
        fun from(value: Long?): UserPrivilegeLevel = values().firstOrNull { it.value == value }
            ?: when (value) {
                in Long.MIN_VALUE..REGULAR_USER.value -> REGULAR_USER
                in AUTHOR.value until EDITOR.value -> AUTHOR
                in EDITOR.value until ADMINISTRATOR.value -> EDITOR
                in ADMINISTRATOR.value..Long.MAX_VALUE -> ADMINISTRATOR
                else -> REGULAR_USER
            }
    }

    fun isAtLeastAtLevel(minimumPrivilege: UserPrivilegeLevel): Boolean {
        return this.value >= minimumPrivilege.value
    }

    fun isAtMostAtLevel(maximumPrivilege: UserPrivilegeLevel): Boolean {
        return this.value <= maximumPrivilege.value
    }
}