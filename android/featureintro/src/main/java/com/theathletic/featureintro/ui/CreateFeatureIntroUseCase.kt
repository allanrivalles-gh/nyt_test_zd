package com.theathletic.featureintro.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureintro.R
import com.theathletic.featureintro.data.local.FeatureIntro
import com.theathletic.featureswitch.Features
import com.theathletic.notification.NotificationOption

class CreateFeatureIntroUseCase @AutoKoin constructor(
    val features: Features,
) {

    operator fun invoke(useExamples: Boolean = false): FeatureIntro {

        return FeatureIntro(
            pages = if (useExamples) createPlaceholderExampleIntroPages() else createIntroPages(),
            // for production replace the null below with the correct destination url
            destinationUrl = when {
                features.isNbaGameHubFeatureIntroEnabled -> "theathletic://league/3/schedule"
                features.isNhlGameHubFeatureIntroEnabled -> "theathletic://league/1/schedule"
                features.isTopSportsNewsFeatureIntroEnabled -> {
                    "theathletic://notification_settings/enable/${NotificationOption.TOP_SPORTS_NEWS.notification}"
                }
                useExamples -> "theathletic://scores"
                else -> ""
            }
        )
    }

    private fun createPlaceholderExampleIntroPages(): List<FeatureIntro.IntroPage> {
        return mutableListOf<FeatureIntro.IntroPage>().apply {
            add(
                FeatureIntro.IntroPage(
                    analyticsView = "",
                    title = R.string.article_share_title,
                    description = R.string.gifts_confirm_info_description,
                    image = R.drawable.img_example_image_1,
                    buttonLabel = R.string.global_next
                )
            )
            add(
                FeatureIntro.IntroPage(
                    analyticsView = "",
                    title = R.string.auth_options_signup_title,
                    description = R.string.box_score_timeline_no_key_moments_description,
                    image = R.drawable.img_example_image_2,
                    buttonLabel = R.string.main_navigation_discover
                )
            )
        }
    }

    /**
     Add production intro pages here and when removing this feature just return an empty list
     **/
    private fun createIntroPages(): List<FeatureIntro.IntroPage> {
        return mutableListOf<FeatureIntro.IntroPage>().apply {

            when {
                features.isNbaGameHubFeatureIntroEnabled -> {
                    add(
                        FeatureIntro.IntroPage(
                            analyticsView = "nba_game_hub_modal",
                            title = R.string.feature_intro_title_nba,
                            description = R.string.feature_intro_description,
                            image = R.drawable.img_nba_game_hub_intro,
                            buttonLabel = R.string.feature_intro_cta_label
                        )
                    )
                }
                features.isNhlGameHubFeatureIntroEnabled -> {
                    add(
                        FeatureIntro.IntroPage(
                            analyticsView = "nhl_game_hub_modal",
                            title = R.string.feature_intro_title_nhl,
                            description = R.string.feature_intro_description,
                            image = R.drawable.img_nhl_game_hub_intro,
                            buttonLabel = R.string.feature_intro_cta_label
                        )
                    )
                }
                features.isTopSportsNewsFeatureIntroEnabled -> {
                    add(
                        FeatureIntro.IntroPage(
                            analyticsView = "top_sports_news_modal",
                            title = R.string.feature_intro_title_top_sports_news,
                            description = R.string.feature_intro_description_top_sports_news,
                            image = R.drawable.img_top_sports_news_intro,
                            buttonLabel = R.string.feature_intro_cta_top_sports_news
                        )
                    )
                }
            }
        }
    }
}