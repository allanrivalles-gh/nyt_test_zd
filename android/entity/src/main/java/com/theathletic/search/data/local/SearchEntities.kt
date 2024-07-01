package com.theathletic.search.data.local

import com.google.gson.annotations.SerializedName
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.ui.binding.ParameterizedString
import java.io.Serializable

data class SearchArticleResponse(@SerializedName("entries") var entries: List<SearchArticleItem>)

open class SearchBaseItem : Serializable {
    open var id: Long = 0L
    open var adapterId: Long = -1
    open var name: String = ""
    open var shortname: String = ""
    open var imageUrl: String = ""
}

open class SearchTitleItem(
    var title: ParameterizedString,
) : SearchBaseItem() {
    override var adapterId: Long = 1
}

open class SearchPopularItem(newVal: ArticleEntity) : SearchBaseItem() {
    override var id = newVal.articleId
    var title = newVal.articleTitle
    var imgUrl = newVal.articleHeaderImg
    var author = newVal.authorName
    var date = newVal.articlePublishDate
}

open class SearchArticleItem : SearchBaseItem() {
    @SerializedName("post_id")
    override var id: Long = 0L

    @SerializedName("post_title")
    var title: String = ""

    @SerializedName("post_date_gmt")
    var date: String = ""

    @SerializedName("author_name")
    var authorName: String = ""

    @SerializedName("post_author_id")
    var authorId: String = ""

    @SerializedName("article_img")
    var imgUrl: String = ""

    @SerializedName("post_type_id")
    private var postTypeId: String? = ""

    val isDiscussion: Boolean
        get() = postTypeId == "29"

    val isLiveDiscussion: Boolean
        get() = postTypeId == "31"
}

open class SearchTeamItem(
    override var id: Long,
    override var name: String,
    override var imageUrl: String
) : SearchBaseItem()

class SearchLeagueItem(
    override var id: Long,
    override var name: String,
    override var imageUrl: String
) : SearchBaseItem()

open class SearchAuthorItem(
    override var id: Long,
    override var name: String,
    override var imageUrl: String
) : SearchBaseItem()