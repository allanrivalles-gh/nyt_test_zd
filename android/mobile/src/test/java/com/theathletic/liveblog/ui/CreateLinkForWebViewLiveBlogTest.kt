package com.theathletic.liveblog.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateLinkForWebViewLiveBlogTest {
    @Test
    fun `appends light theme query and post id param when light mode and post id set`() {
        val linkForEmbed = "https://theathletic.com/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/?embed=1"
        val lightMode = true
        val postId = "lHrm8InvLVhO"
        val link = createLinkForWebViewLiveBlog(linkForEmbed, lightMode, postId)
        assertThat(link).isEqualTo("https://theathletic.com/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/lHrm8InvLVhO/?embed=1&theme=light")
    }

    @Test
    fun `appends dark theme query but doesn't append post id param when not light mode and post id not set`() {
        val linkForEmbed = "https://theathletic.com/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/?embed=1"
        val lightMode = false
        val postId: String? = null
        val link = createLinkForWebViewLiveBlog(linkForEmbed, lightMode, postId)
        assertThat(link).isEqualTo("https://theathletic.com/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/?embed=1&theme=dark")
    }
}