package com.theathletic.entity.main

enum class Sport(val homeTeamFirst: Boolean = false) {
    FOOTBALL, SOCCER(homeTeamFirst = true), BASEBALL, BASKETBALL, HOCKEY, BOXING, GOLF, MMA, UNKNOWN
}

enum class League(
    val leagueId: Long,
    val sport: Sport
) {
    NHL(1L, Sport.HOCKEY),
    NFL(2L, Sport.FOOTBALL),
    NBA(3L, Sport.BASKETBALL),
    MLB(4L, Sport.BASEBALL),
    NCAA_FB(9L, Sport.FOOTBALL),
    NCAA_BB(10L, Sport.BASKETBALL),
    CFL(11L, Sport.FOOTBALL),
    LNH(12L, Sport.HOCKEY),
    WNBA(13L, Sport.BASKETBALL),
    GOLF(14L, Sport.GOLF),
    FANTASY_FOOTBALL(21L, Sport.FOOTBALL),
    FANTASY_BASKETBALL(25L, Sport.BASKETBALL),
    FANTASY_HOCKEY(26L, Sport.HOCKEY),
    FANTASY_BASEBALL(27L, Sport.BASEBALL),
    MLS(5L, Sport.SOCCER),
    EPL(6L, Sport.SOCCER),
    UWC(45L, Sport.SOCCER),
    CHAMPIONS_LEAGUE(7L, Sport.SOCCER),
    LIGA_MX(8L, Sport.SOCCER),
    SOCCER(15L, Sport.SOCCER),
    INTERNATIONAL(16L, Sport.SOCCER),
    LA_LIGA(17L, Sport.SOCCER),
    NPSL(18L, Sport.SOCCER),
    USL(19L, Sport.SOCCER),
    NWSL(20L, Sport.SOCCER),
    BUNDESLIGA(22L, Sport.SOCCER),
    SERIE_A(23L, Sport.SOCCER),
    LIGUE_1(24L, Sport.SOCCER),
    EFL(32L, Sport.SOCCER),
    SCOTTISH_PREMIERE(33L, Sport.SOCCER),
    FANTASY_PREMIERE(34L, Sport.SOCCER),
    UK_WOMANS_FOOTBALL(35L, Sport.SOCCER),
    NCAA_WB(36L, Sport.BASKETBALL),
    UEL(37L, Sport.SOCCER),
    INTERNATIONAL_FRIENDLIES(41L, Sport.SOCCER),
    WORLD_CUP(44L, Sport.SOCCER),
    FA_CUP(46L, Sport.SOCCER),
    LEAGUE_ONE(47L, Sport.SOCCER),
    LEAGUE_TWO(48L, Sport.SOCCER),
    CARABAO_CUP(49L, Sport.SOCCER),
    COPA_DEL_REY(52L, Sport.SOCCER),
    WOMENS_WORLD_CUP(55L, Sport.SOCCER),
    UNKNOWN(-1L, Sport.UNKNOWN);

    companion object {
        fun parseFromId(leagueId: Long?): League = try {
            values().first { it.leagueId == leagueId }
        } catch (ex: Exception) {
            UNKNOWN
        }
    }
}