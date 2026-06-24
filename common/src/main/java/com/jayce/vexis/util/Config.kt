package com.jayce.vexis.util

object Config {

    const val ZERO = 0
    const val LONG_ZERO = 0L
    const val NIL = ""
    const val BASIC_LETTER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    const val EVENT_TYPE_DEFAULT = 0
    const val EVENT_TYPE_CHAT = 1
    const val EVENT_TYPE_FEEDBACK = 2
    const val EVENT_TYPE_EXIT = 3
    const val EVENT_TYPE_ROLE = 4

    const val ACTION_BROADCAST_LOGOUT = "com.jayce.vexis.broadcast.logout"
    const val ACTION_BROADCAST_NOTIFY = "com.jayce.vexis.broadcast.notify"

    const val FRAGMENT_FEEDBACK = "feedback"
    const val FRAGMENT_KIT = "kitFragment"
    const val FRAGMENT_HISTORY = "historicalAxis"
    const val FRAGMENT_ARTICLE = "articleFragment"
    const val FRAGMENT_SENIOR = "senior"
    const val FRAGMENT_FILE = "mediaLibrarFragment"
    const val FRAGMENT_MAP = "mapFragment"

    const val URL_PREFIX = "https://com.jayce.vexis"
    const val SERVER_DOMAIN = "xiaomiqiu.com"

    const val AVATAR_SAVE_TIME = "cursorTime"

    const val MEDIA_TYPE_ALL = "*/*"
    const val MEDIA_TYPE_IMAGE = "image/*"

    const val COOKIE_USER_ID = "userId"
    const val COOKIE_UUID = "cookId"

    const val BASE_FACTOR_ONE = 1
    const val BASE_FACTOR_SEVEN = 7
    const val BASE_FACTOR_TEN = 10
    const val BASE_FACTOR_TWELVE = 12
    const val BASE_FACTOR_TWENTY_FOUR = 24
    const val BASE_FACTOR_THIRTY = 30
    const val BASE_FACTOR_SIXTY = 60
    const val BASE_FACTOR_HUNDRED = 100
    const val BASE_FACTOR_THREE_SIX_FIVE = 365
    const val BASE_FACTOR_THOUSAND = 1000
    const val BASE_FACTOR_THOUSAND_THOUSAND = 1000000

    const val TIME_LEVEL_TEN_MICRO_SECOND = 0
    const val TIME_LEVEL_HUNDRED_MICRO_SECOND = 1
    const val TIME_LEVEL_MILLI_SECOND = 2
    const val TIME_LEVEL_TEN_MILLI_SECOND = 3
    const val TIME_LEVEL_HUNDRED_MILLI_SECOND = 4
    const val TIME_LEVEL_SECOND = 5
    const val TIME_LEVEL_MINUTE = 6
    const val TIME_LEVEL_HOUR = 7
    const val TIME_LEVEL_DAY = 8
    const val TIME_LEVEL_MONTH = 9
    const val TIME_LEVEL_YEAR = 10
    const val TIME_LEVEL_TEN_YEAR = 11
    const val TIME_LEVEL_HUNDRED_YEAR = 12
    const val TIME_LEVEL_THOUSAND_YEAR = 13

    const val MICRO_SECOND = 1L
    const val MILLI_SECOND = BASE_FACTOR_THOUSAND * MICRO_SECOND
    const val SECOND = BASE_FACTOR_THOUSAND * MILLI_SECOND
    const val MINUTE = BASE_FACTOR_SIXTY * SECOND
    const val HOUR = BASE_FACTOR_SIXTY * MINUTE
    const val DAY = BASE_FACTOR_TWENTY_FOUR * HOUR
    const val YEAR_BASE = 1L
    const val YEAR_BASE_TEN = BASE_FACTOR_TEN * YEAR_BASE
    const val YEAR_BASE_HUNDRED = BASE_FACTOR_TEN * YEAR_BASE_TEN
}