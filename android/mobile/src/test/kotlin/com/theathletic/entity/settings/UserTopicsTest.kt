package com.theathletic.entity.settings

import com.google.common.truth.Truth.assertThat
import com.theathletic.entity.main.FEED_MY_FEED_ID
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import org.junit.Test

class UserTopicsTest {

    @Test
    fun baseItem_HasExpectedDefaults() {
        val baseItem = UserTopicsBaseItem()
        assertThat(baseItem.isNCAAFBItem()).isFalse()
        assertThat(baseItem.isNCAABBItem()).isFalse()
        assertThat(baseItem.isMyFeedItem()).isFalse()
        assertThat(baseItem.getSport()).isEqualTo(Sport.UNKNOWN)
    }

    @Test
    fun userTopicLeague_Soccer_HasExpectedDefaults() {
        val league = UserTopicsItemLeague()
        league.id = League.CHAMPIONS_LEAGUE.leagueId
        assertThat(league.isNCAAFBItem()).isFalse()
        assertThat(league.isNCAABBItem()).isFalse()
        assertThat(league.isMyFeedItem()).isFalse()
        assertThat(league.getSport()).isEqualTo(Sport.SOCCER)
    }

    @Test
    fun userTopicLeague_MyFeed_HasExpectedDefaults() {
        val league = UserTopicsItemLeague()
        league.id = FEED_MY_FEED_ID
        assertThat(league.isNCAAFBItem()).isFalse()
        assertThat(league.isNCAABBItem()).isFalse()
        assertThat(league.isMyFeedItem()).isFalse()
        assertThat(league.getSport()).isEqualTo(Sport.UNKNOWN)
    }

    @Test
    fun userTopicTeam_MyFeed_HasExpectedDefaults() {
        val league = UserTopicsItemTeam()
        league.id = FEED_MY_FEED_ID
        assertThat(league.isNCAAFBItem()).isFalse()
        assertThat(league.isNCAABBItem()).isFalse()
        assertThat(league.isMyFeedItem()).isTrue()
        assertThat(league.getSport()).isEqualTo(Sport.UNKNOWN)
    }

    @Test
    fun userTopicLeague_NCAAFB_HasExpectedDefaults() {
        val league = UserTopicsItemLeague()
        league.id = League.NCAA_FB.leagueId
        assertThat(league.isNCAAFBItem()).isTrue()
        assertThat(league.isNCAABBItem()).isFalse()
        assertThat(league.isMyFeedItem()).isFalse()
        assertThat(league.getSport()).isEqualTo(Sport.FOOTBALL)
    }

    @Test
    fun userTopicLeague_NCAABB_HasExpectedDefaults() {
        val league = UserTopicsItemLeague()
        league.id = League.NCAA_BB.leagueId
        assertThat(league.isNCAAFBItem()).isFalse()
        assertThat(league.isNCAABBItem()).isTrue()
        assertThat(league.isMyFeedItem()).isFalse()
        assertThat(league.getSport()).isEqualTo(Sport.BASKETBALL)
    }

    @Test
    fun userTopicTeam_Niners_HasExpectedDefaults() {
        val niners = UserTopicsItemTeam()
        niners.id = 51
        niners.leagueId = League.NFL.leagueId
        assertThat(niners.isNCAAFBItem()).isFalse()
        assertThat(niners.isNCAABBItem()).isFalse()
        assertThat(niners.isMyFeedItem()).isFalse()
        assertThat(niners.getSport()).isEqualTo(Sport.FOOTBALL)
    }
}