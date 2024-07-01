package com.theathletic.liveblog.ui

import android.webkit.WebView
import androidx.compose.foundation.lazy.LazyListState
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DayNightMode
import com.theathletic.ui.ResourceString.StringWrapper

object LiveBlogPreviewData {
    private const val LiveBlogDescription = "<p>It took 10 weeks, but the <a class='ath_autolink' href='https://theathletic.com/team/chiefs/'>Chiefs</a> are in first place in the AFC West.</p><p></p><p><a class='ath_autolink' href='https://theathletic.com/player/nfl/chiefs/patrick-mahomes/'>Patrick Mahomes</a> passed for 400 yards and five touchdowns <a href=\"https://theathletic.com/news/patrick-mahomes-chiefs-take-first-place-in-afc-west-with-dominant-win-over-raiders/WrUcSUxujpeG/\" target=_self>in a 41-14 road win</a> against the <a class='ath_autolink' href='https://theathletic.com/team/raiders/'>Raiders</a> on Sunday night. The Chiefs outgained the Raiders by more than 200 yards, and the victory put Kansas City a half-game ahead of Las Vegas and Los Angeles in the division standings <a href=\"https://theathletic.com/2955929/2021/11/15/here-come-the-patriots-and-chiefs-updated-nfl-playoff-picture/\" target=_self>and firmly in the playoff picture</a>.</p><p></p><p>After punting on their first drive, the Chiefs scored on seven of their next eight possessions, with a missed field goal the only aberration. They scored more than 40 points for the second time this season. The 14 points allowed to the Raiders (5-4) were the third-fewest allowed by Kansas City this season.</p><p></p><p>After losing three of their first five games, the Chiefs (6-4) have won four of their past five and three in a row.</p><p></p><p><a href=\"https://theathletic.com/checkout2/introannual33/\" target=_self>New subscribers can get 33 percent off a subscription</a> to coverage of all sports at <em>The Athletic</em>. Want a free subscription to <em>The Athletic</em>? <a href=\"https://theathletic.com/betmgm-redirect?redirect_type=offer&zone_id=1649446\" target=_self rel=\"sponsored\">Sign up for BetMGM</a>, bet \$10 and win \$200 in free bets plus a free three-month subscription (or renewal).</p><p></p><p><strong><a href=\"https://theathletic.com/news/patrick-mahomes-chiefs-take-first-place-in-afc-west-with-dominant-win-over-raiders/WrUcSUxujpeG/\" target=_self>Chiefs 41, Raiders 14, Final</a></strong> | <a href=\"https://theathletic.com/2780290/2021/11/14/chiefs-at-raiders-spread-odds-picks-and-trends-expert-predictions-for-a-week-10-afc-west-clash-on-sunday-night-football/\" target=_self>Odds</a> | <a href=\"https://theathletic.com/nfl/boxscore/?id=17830\" target=_self>Box score</a></p>"
    const val displaySystemThemeButton = true
    const val liveBlogPostsCount = 0
    val listState = LazyListState()
    val contentTextSize = ContentTextSize.LARGE
    val dayNightMode = DayNightMode.DAY_MODE
    val tweetMap = mutableMapOf<String, WebView>()
    val LiveBlog = LiveBlogUi.LiveBlog(
        id = "1",
        title = "Premier League and soccer news live updates: Steven Gerrard to Villa latest, Eddie Howe Newcastle unveiling and more",
        publishedAt = StringWrapper("updated 2hrs"),
        authorName = "The Athletic Staff",
        authorId = 1,
        imageUrl = "",
        description = LiveBlogDescription,
        permalink = "",
        linkForEmbed = "",
        posts = listOf(),
        tweets = listOf(),
        hasNextPage = false,
        sponsorPresentedBy = null,
        sponsorBanner = null,
        isLive = true
    )
    val LiveBlogPostBasic = LiveBlogUi.LiveBlogPostBasic(
        id = "1",
        title = "The White Sox win 9-8 on a Tim Anderson walk-off home run!",
        publishedAt = StringWrapper("8 min ago"),
        imageUrl = "",
        description = LiveBlogDescription,
        authorId = 0,
        authorName = "Joe Vardon",
        authorDescription = "Senior NBA Insider",
        avatarUrl = "",
        relatedArticles = emptyList(),
        tweets = listOf()
    )

    val LiveBlogPostSponsored = LiveBlogUi.LiveBlogPostSponsored(
        id = "2",
        articleId = "id",
        title = "Klay Thompson passes his captain's license exam",
        excerpt = "All that time on the Bay paid off as Klay is now a full-time ferry captain.",
        updatedAt = StringWrapper("20 min ago"),
        imageUrl = "https://cdn.theathletic.com/app/uploads/2021/12/14190752/POWER_RANKINGS_VISA_15-2-1024x683.jpg",
        sponsorPresentedBy = LiveBlogUi.SponsorImage(
            imageUriLight = "https://cdn-media.theathletic.com/live-blogs/sponsors/visa-logo.png",
            imageUriDark = "https://cdn-media.theathletic.com/live-blogs/sponsors/visa-logo.png",
            label = "Brought to you by"
        )
    )

    val RelatedArticle = LiveBlogUi.RelatedArticle(
        id = 12,
        title = "Ja'Marr Chase Super Bowl props, odds, expert predictions: Can wide receiver live up to lofty prop lines as a rookie?",
        imageUrl = "https://cdn.theathletic.com/app/uploads/2021/12/14190752/POWER_RANKINGS_VISA_15-2-1024x683.jpg",
        authorName = "Dan Duggan",
        commentCount = "35",
        showComments = true
    )

    val Interactor = object : LiveBlogUi.Interactor {
        override fun onUrlClick(url: String) { }
        override fun onShareClick() { }
        override fun onBackClick() { }
        override fun onTextStyleClick() { }
        override fun onRelatedArticleClick(id: Long, liveBlogPostId: String) { }
        override fun onFabClick() { }
        override fun onBlogAuthorClick(authorId: Long, liveBlogId: String) { }
        override fun onPostAuthorClick(authorId: Long, liveBlogPostId: String) { }
        override fun onInitialPostScroll() {}

        override fun trackLiveBlogContent(liveBlogId: String) { }
        override fun trackLiveBlogPostContent(liveBlogPostId: String) { }
        override fun loadMorePosts() { }
        override fun onSponsoredArticleClick(id: String) { }
    }

    val BottomSheetInteractor = object : LiveBlogUi.BottomSheetInteractor {
        override fun onTextSizeChange(textSize: ContentTextSize) { }
        override fun onDayNightToggle(dayNightMode: DayNightMode) { }
        override fun trackTextSizeChange() { }
    }
}