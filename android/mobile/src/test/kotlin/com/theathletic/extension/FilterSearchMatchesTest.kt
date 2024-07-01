package com.theathletic.extension

import com.google.common.truth.Truth
import org.junit.Test

class FilterSearchMatchesTest {
    // Followable representation of (name, searchText)
    class FollowableDummy {
        val name: String
        val searchText: String

        constructor() {
            name = ""
            searchText = ""
        }

        constructor(followable: String) {
            val split = followable.split(", ")
            name = split.first()
            searchText = split.last()
        }

        override fun toString(): String {
            return name
        }
    }

    // Teams
    private val teams = listOf(
        FollowableDummy("Tulsa NCAAF, Tulsa Tulsa Golden Hurricane  TUL College Football NCAAF NCAA Football "),
        FollowableDummy("Mallorca, Mallorca Mallorca  MLL La Liga LALIGA La Liga "),
        FollowableDummy("Morgan State NCAAW, Morgan State Morgan State Lady Bears  MORG Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Ball State NCAAF, Ball State Ball State Cardinals  BALL College Football NCAAF NCAA Football "),
        FollowableDummy("Lehigh NCAAW, Lehigh Lehigh Mountain Hawks  LEH Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Long Beach State NCAAM, Long Beach State Long Beach State Beach 49ers LBST Men's College Basketball NCAAM NCAA Men's Basketball "),
        FollowableDummy("Texas A&M NCAAF, Texas A&M Texas A&M Aggies  TAM College Football NCAAF NCAA Football "),
        FollowableDummy("Valladolid, Valladolid Real Valladolid  VLD La Liga LALIGA La Liga "),
        FollowableDummy("Mali, Mali Mali  MLI International Football FIFA Euro / International "),
        FollowableDummy("Women's Euros, Women's Euros"),
        FollowableDummy("Aberdeen, Aberdeen Aberdeen FC  ABE Scottish Premiership SPFL Scottish Professional Football League "),
        FollowableDummy("NJIT NCAAM, NJIT NJIT Highlanders  NJIT Men's College Basketball NCAAM NCAA Men's Basketball "),
        FollowableDummy("Pittsburgh NCAAW, Pittsburgh Pittsburgh Panthers  PITT Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Toledo NCAAW, Toledo Toledo Rockets  TOL Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Hoffenheim, Hoffenheim 1899 Hoffenheim  HOF Bundesliga BUND Bundesliga "),
        FollowableDummy("Cavaliers, Cavaliers Cleveland Cavaliers Cavs CLE NBA NBA National Basketball Association Cleveland"),
        FollowableDummy("Arkansas NCAAF, Arkansas Arkansas Razorbacks  ARK College Football NCAAF NCAA Football "),
        FollowableDummy("Tennessee NCAAW, Tennessee Tennessee Lady Volunteers  TENN Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Louisiana-Monroe NCAAF, Louisiana-Monroe Louisiana-Monroe Warhawks  ULM College Football NCAAF NCAA Football "),
        FollowableDummy("Old Dominion NCAAF, Old Dominion Old Dominion Monarchs  ODU College Football NCAAF NCAA Football "),
        FollowableDummy("Akron NCAAW, Akron Akron Zips  AKR Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("West Virginia NCAAW, West Virginia West Virginia Mountaineers  WVU Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Rotherham, Rotherham Rotherham United FC The Millers ROT Championship EFL Championship "),
        FollowableDummy("Detroit NCAAM, Detroit Detroit Titans  DET Men's College Basketball NCAAM NCAA Men's Basketball Detroit"),
        FollowableDummy("Wyoming NCAAF, Wyoming Wyoming Cowboys  WYO College Football NCAAF NCAA Football "),
        FollowableDummy("Canucks, Canucks Vancouver Canucks Nucks VAN NHL NHL National Hockey League Vancouver"),
        FollowableDummy("Lions, Lions Detroit Lions  DET NFL NFL National Football League Detroit"),
        FollowableDummy("Sky, Sky Chicago Sky  CHI WNBA WNBA Women's National Basketball Association Chicago"),
        FollowableDummy("Long Beach State NCAAW, Long Beach State Long Beach State Beach  LBSU Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Chattanooga NCAAW, Chattanooga Chattanooga Lady Mocs  CHAT Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Seattle NCAAW, Seattle Seattle Redhawks  SEA Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Georgia State NCAAM, Georgia State Georgia State Panthers  GAST Men's College Basketball NCAAM NCAA Men's Basketball Atlanta"),
        FollowableDummy("VfB Stuttgart, VfB Stuttgart VfB Stuttgart  VFB Bundesliga BUND Bundesliga "),
    )

    private val leagues = listOf(
        FollowableDummy("NFL, NFL"),
        FollowableDummy("Men's College Basketball, Men's College Basketball"),
        FollowableDummy("Boxing, Boxing"),
        FollowableDummy("Sports Betting, Sports Betting"),
        FollowableDummy("Copa del Rey, Copa del Rey"),
        FollowableDummy("Championship, Championship"),
        FollowableDummy("League One, League One"),
        FollowableDummy("Mixed Martial Arts, Mixed Martial Arts"),
        FollowableDummy("Ligue 1, Ligue 1"),
        FollowableDummy("Europa League, Europa League"),
        FollowableDummy("NWSL, NWSL"),
        FollowableDummy("Liga MX, Liga MX"),
        FollowableDummy("Premier League, Premier League"),
        FollowableDummy("WNBA, WNBA"),
        FollowableDummy("Olympics, Olympics"),
        FollowableDummy("NPSL, NPSL"),
        FollowableDummy("NBA, NBA"),
        FollowableDummy("World Cup, World Cup"),
        FollowableDummy("Gaming, Gaming"),
        FollowableDummy("CFL, CFL"),
    )

    private val authors = listOf(
        FollowableDummy("Nnamdi Onyeagwara, Nnamdi Onyeagwara"),
        FollowableDummy("Arpon Basu, Montreal"),
        FollowableDummy("Joshua Kloke, Toronto"),
        FollowableDummy("Andy Mitten, Andy Mitten"),
        FollowableDummy("Vic Tafur, Bay Area"),
        FollowableDummy("Marcus Thompson II, Bay Area"),
        FollowableDummy("Holly Shand, Holly Shand"),
        FollowableDummy("Daniel Shirley, Atlanta"),
        FollowableDummy("Leah Williamson, Leah Williamson"),
        FollowableDummy("Daniel Kaplan, Daniel Kaplan"),
        FollowableDummy("Eric Nehm, Wisconsin"),
        FollowableDummy("Peter Gammons, MLB"),
        FollowableDummy("Matt Gelb, Philadelphia"),
        FollowableDummy("Ken Pomeroy, College Basketball"),
        FollowableDummy("David Ornstein, Premier League"),
        FollowableDummy("Max Olson, College Football"),
        FollowableDummy("Kevin Papetti, Kevin Papetti"),
    )

    // Formula 1
    private val formula1 = listOf(
        FollowableDummy("Formula 1, Formula 1"),
        FollowableDummy("Luke Smith, Formula 1"),
        FollowableDummy("Madeline Coleman, Formula 1"),
    )

    // Colorado
    private val colorado = listOf(
        FollowableDummy("Colorado State NCAAF, Colorado State Colorado State Rams  CSU College Football NCAAF NCAA Football "),
        FollowableDummy("Colorado NCAAW, Colorado Colorado Buffaloes  COLO Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Colorado State NCAAW, Colorado State Colorado State Rams  CSU Women's College Basketball NCAAW NCAA Women's Basketball "),
        FollowableDummy("Avalanche, Avalanche Colorado Avalanche Lanche COL NHL NHL National Hockey League Denver"),
        FollowableDummy("Colorado State NCAAM, Colorado State Colorado State Rams  CSU Men's College Basketball NCAAM NCAA Men's Basketball "),
        FollowableDummy("Northern Colorado NCAAM, Northern Colorado Northern Colorado Bears  NCOL Men's College Basketball NCAAM NCAA Men's Basketball "),
        FollowableDummy("Colorado NCAAF, Colorado Colorado Buffaloes  COL College Football NCAAF NCAA Football Denver"),
        FollowableDummy("Rapids, Rapids Colorado Rapids  COL MLS MLS Major League Soccer Denver"),
        FollowableDummy("Rockies, Rockies Colorado Rockies  COL MLB MLB Major League Baseball Denver"),
        FollowableDummy("Colorado NCAAM, Colorado Colorado Buffaloes  COL Men's College Basketball NCAAM NCAA Men's Basketball Denver"),
        FollowableDummy("Northern Colorado NCAAW, Northern Colorado Northern Colorado Bears  UNCO Women's College Basketball NCAAW NCAA Women's Basketball "),
    )

    private val allFollowables = teams + leagues + authors + formula1 + colorado

    private fun List<FollowableDummy>.filterMatches(searchText: String) = filter {
        listOf(
            it.name,
            it.searchText,
        ).filterSearchMatches(searchText)
    }.sortedBy { it.name }

    @Test
    fun `when I search for f1 or formula 1 it returns me formula 1 related followables`() {
        val filter = allFollowables.filterMatches("f1")
        val filter2 = allFollowables.filterMatches("formula 1")
        Truth.assertThat(filter).containsExactlyElementsIn(
            formula1
        )
        Truth.assertThat(filter2).containsExactlyElementsIn(
            formula1
        )
    }

    @Test
    fun `when I search for 1899 returns Hoffenheim`() {
        val filter = allFollowables.filterMatches("1899").map { it.toString() }
        Truth.assertThat(filter).isEqualTo(listOf("Hoffenheim"))
    }

    @Test
    fun `when I search for colorado it returns the colorado related followables`() {
        val filter = allFollowables.filterMatches("colorado")
        Truth.assertThat(filter).containsExactlyElementsIn(
            colorado
        )
    }

    @Test
    fun `when I search for bay area it returns the bay area related authors`() {
        val filter = allFollowables.filterMatches("bay area").map { it.toString() }
        Truth.assertThat(filter).isEqualTo(
            listOf(
                "Marcus Thompson II",
                "Vic Tafur",
            )
        )
    }
}