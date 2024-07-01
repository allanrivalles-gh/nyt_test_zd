package com.theathletic.themes

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.theathletic.ui.R

class AthFont {
    companion object {
        val Slab = FontFamily(
            Font(R.font.slab_regular_bold)
        )
        val SlabInline = FontFamily(
            Font(R.font.slab_regular_inline)
        )
        val Calibre = FontFamily(
            Font(R.font.calibre_regular, FontWeight.Normal),
            Font(R.font.calibre_medium, FontWeight.Medium),
            Font(R.font.calibre_semibold, FontWeight.SemiBold),
        )
        val TiemposHeadline = FontFamily(
            Font(R.font.tiempos_headline_regular)
        )
        val TiemposText = FontFamily(
            Font(R.font.tiempos_text_regular, FontWeight.Normal),
            Font(R.font.tiempos_text_medium, FontWeight.Medium),
            Font(R.font.tiempos_semibold, FontWeight.SemiBold),
            Font(R.font.tiempos_bold, FontWeight.Bold)
        )
        val Sohne = FontFamily(
            Font(R.font.sohne_buch, FontWeight.Normal)
        )
    }
}

class AthTextStyle {
    object Slab {
        object Bold {
            val Large = TextStyle(
                fontFamily = AthFont.Slab,
                fontSize = 42.sp,
                lineHeight = 1.142.em,
                letterSpacing = (-0.005).em
            )
            val Medium = TextStyle(
                fontFamily = AthFont.Slab,
                fontStyle = FontStyle.Normal,
                fontSize = 27.sp,
                lineHeight = 0.925.em,
                letterSpacing = (-0.005).em
            )
            val Small = TextStyle(
                fontFamily = AthFont.Slab,
                fontSize = 18.sp,
                lineHeight = 1.em,
                letterSpacing = 0.01.em
            )
        }

        object Inline {
            val Medium = TextStyle(
                fontFamily = AthFont.SlabInline,
                fontSize = 27.sp,
                lineHeight = 1.7472.em
            )
            val Small = TextStyle(
                fontFamily = AthFont.SlabInline,
                fontSize = 18.sp,
                lineHeight = 1.em,
                letterSpacing = 0.01.em
            )
        }
    }

    object TiemposHeadline {
        object Medium {
            val ExtraExtraLarge = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Medium,
                fontSize = 42.sp,
                lineHeight = 1.095.em,
                letterSpacing = 0.01.em
            )
            val ExtraLarge = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Medium,
                fontSize = 36.sp,
                lineHeight = 1.111.em,
                letterSpacing = 0.01.em
            )
            val Large = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Medium,
                fontSize = 30.sp,
                lineHeight = 1.2.em,
                letterSpacing = 0.01.em
            )
        }

        object Regular {
            val ExtraExtraLarge = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Normal,
                fontSize = 42.sp,
                lineHeight = 1.095.em,
                letterSpacing = 0.01.em
            )
            val ExtraLarge = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                lineHeight = 1.111.em,
                letterSpacing = 0.01.em
            )
            val Large = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Normal,
                fontSize = 30.sp,
                lineHeight = 1.2.em,
                letterSpacing = 0.01.em
            )
            val Medium = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 1.208.em,
                letterSpacing = 0.01.em
            )
            val Small = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 1.2.em,
                letterSpacing = 0.01.em
            )
            val ExtraSmall = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 1.222.em,
                letterSpacing = 0.01.em
            )
            val ExtraExtraSmall = TextStyle(
                fontFamily = AthFont.TiemposHeadline,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 1.312.em,
                letterSpacing = 0.002.em
            )
        }
    }

    object Sohne {
        val Data = TextStyle(
            fontFamily = AthFont.Sohne,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 1.0.em
        )
    }

    object Calibre {
        object Headline {
            object Regular {
                val ExtraLarge = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 48.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = -(0.01).em
                )
                val Small = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp,
                    lineHeight = 1.2.em
                )
            }

            object SemiBold {
                val Large = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 36.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.015.em
                )
                val Medium = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.015.em
                )
                val Small = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.01.em
                )
            }

            object Medium {
                val Small = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    lineHeight = 1.0.em,
                )
            }
        }

        object Utility {
            object Medium {
                val ExtraLarge = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    lineHeight = 1.0.em
                )
                val Large = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.014.em,
                    lineHeight = 19.sp
                )
                val Small = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.015.em
                )
                val ExtraSmall = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.015.em
                )
            }

            object Regular {
                val ExtraExtraLarge = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 1.0.em,
                )
                val ExtraLarge = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 1.0.em,
                )
                val Large = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    letterSpacing = 0.014.em,
                    lineHeight = 19.sp
                )
                val Small = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.01.em
                )
                val ExtraSmall = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.015.em
                )
            }
        }

        object Tag {
            object Medium {
                val Large = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 1.0.em,
                    letterSpacing = 0.015.em
                )
            }
        }
    }

    object TiemposBody {
        object Medium {
            val Large = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 1.111.em
            )
            val Medium = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 1.5.em
            )
            val Small = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 1.5.em
            )
            val ExtraSmall = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 1.5.em
            )
        }

        object Regular {
            val Large = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Light,
                fontSize = 18.sp,
                lineHeight = 1.222.em
            )
            val Medium = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                lineHeight = 1.125.em
            )
            val Small = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                lineHeight = 1.3.em
            )
            val ExtraSmall = TextStyle(
                fontFamily = AthFont.TiemposText,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                lineHeight = 1.5.em
            )
        }
    }

    companion object {
        val Navigation = TextStyle(
            fontFamily = AthFont.Calibre,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
        )
    }

    // Section for one-off styles
    object LiveBlog {
        val SponsoredPostExcerpt = TextStyle(
            fontFamily = AthFont.Calibre,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            letterSpacing = 0.025.em,
            lineHeight = 1.22.em
        )
    }

    object LiveRoom {
        val SheetHeader = TextStyle(
            fontFamily = AthFont.Calibre,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        )

        val ChatBadge = TextStyle(
            fontFamily = AthFont.Calibre,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 0.05.em
        )
    }
}