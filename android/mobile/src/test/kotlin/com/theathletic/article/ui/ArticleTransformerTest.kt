package com.theathletic.article.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.article.ArticleContentModel
import com.theathletic.datetime.DateUtility
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.RelatedContent
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.UiModel
import com.theathletic.ui.formatter.CountFormatter
import com.theathletic.user.IUserManager
import com.theathletic.utility.BillingPreferences
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever

internal class ArticleTransformerTest {
    private lateinit var transformer: ArticleTransformer
    @Mock private lateinit var userDataRepository: IUserDataRepository
    @Mock private lateinit var userManager: IUserManager
    @Mock private lateinit var billingPreferences: BillingPreferences
    @Mock private lateinit var dateUtility: DateUtility
    @Mock private lateinit var simpleDataState: ArticleDataState
    @Mock private lateinit var countFormatter: CountFormatter

    private var closeable: AutoCloseable? = null

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
        // Set up some basic prerequisites for the transformer to run
        whenever(dateUtility.formatGMTTimeAgo(anyString(), any(), any())).thenReturn("formatted date")
        whenever(simpleDataState.contentTextSize).doReturn(ContentTextSize.DEFAULT)
        whenever(simpleDataState.htmlIsLoaded).doReturn(true)
        whenever(countFormatter.formatCommentCount(any())).thenReturn("0")

        transformer = ArticleTransformer(
            userDataRepository,
            userManager,
            billingPreferences,
            dateUtility,
            countFormatter
        )
    }

    @After
    fun teardown() {
        closeable?.close()
    }

    @Test
    fun `transform produces one ArticleContentModel`() {
        whenever(simpleDataState.articleEntity).thenReturn(createTestArticle())

        val models = transformer.transform(simpleDataState).uiModels.filterIsInstance<ArticleContentModel>()

        assertThat(models.size).isEqualTo(1)
    }

    @Test
    fun `transform an article without related content does not show any related models`() {
        whenever(simpleDataState.articleEntity).thenReturn(createTestArticle())

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        assertThat(models).isEmpty()
    }

    @Test
    fun `article related content produces one related content item if only one related content`() {
        val testArticle = createTestArticle()
        testArticle.relatedContent = mutableListOf(
            createRelatedContent("1", RelatedContent.ContentType.ARTICLE)
        )
        whenever(simpleDataState.articleEntity).thenReturn(testArticle)

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        assertThat(models.size).isEqualTo(2)
        assertThat(models[0]).isInstanceOf(RelatedContentSectionTitle::class.java)
        assertThat(models[1]).isInstanceOf(RelatedContentItem::class.java)
    }

    @Test
    fun `excess article related content produces four related content items`() {
        val testArticle = createTestArticle()
        testArticle.relatedContent = mutableListOf<RelatedContent>().apply {
            for (i in 1..7) {
                add(createRelatedContent("$i", RelatedContent.ContentType.ARTICLE))
            }
        }

        whenever(simpleDataState.articleEntity).thenReturn(testArticle)

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        assertThat(models.size).isEqualTo(5)
        assertThat(models[0]).isInstanceOf(RelatedContentSectionTitle::class.java)
        assertThat(models[1]).isInstanceOf(RelatedContentItem::class.java)
        assertThat(models[2]).isInstanceOf(RelatedContentItem::class.java)
        assertThat(models[3]).isInstanceOf(RelatedContentItem::class.java)
        assertThat(models[4]).isInstanceOf(RelatedContentItem::class.java)
    }

    @Test
    fun `read article related content is filtered out of models`() {
        val testArticle = createTestArticle()
        whenever(userDataRepository.isItemRead(2)).thenReturn(true)
        testArticle.relatedContent = mutableListOf<RelatedContent>().apply {
            add(createRelatedContent("1", RelatedContent.ContentType.ARTICLE))
            add(createRelatedContent("2", RelatedContent.ContentType.ARTICLE))
        }

        whenever(simpleDataState.articleEntity).thenReturn(testArticle)

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        assertThat(models.size).isEqualTo(2)
        assertThat(models[0]).isInstanceOf(RelatedContentSectionTitle::class.java)
        assertThat(models[1]).isInstanceOf(RelatedContentItem::class.java)
    }

    @Test
    fun `article related content does not show time ago or live status`() {
        val testArticle = createTestArticle()
        testArticle.relatedContent = mutableListOf(createRelatedContent("1", RelatedContent.ContentType.ARTICLE))

        whenever(simpleDataState.articleEntity).thenReturn(testArticle)

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        val articleItem = models.last() as? RelatedContentItem

        assertThat(articleItem?.showLiveStatus).isFalse()
        assertThat(articleItem?.showTimeAgo).isFalse()
    }

    @Test
    fun `article related content does not show comments if empty`() {
        val testArticle = createTestArticle()
        testArticle.relatedContent = mutableListOf(createRelatedContent("1", RelatedContent.ContentType.ARTICLE))

        whenever(simpleDataState.articleEntity).thenReturn(testArticle)

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        val articleItem = models.last() as? RelatedContentItem

        assertThat(articleItem?.showComments).isFalse()
    }

    @Test
    fun `liveblog related content does not show comments ever`() {
        val testArticle = createTestArticle()
        testArticle.relatedContent = mutableListOf(
            createRelatedContent(
                id = "1",
                contentType = RelatedContent.ContentType.LIVEBLOG,
                commentCount = 5
            )
        )

        whenever(simpleDataState.articleEntity).thenReturn(testArticle)

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        val articleItem = models.last() as? RelatedContentItem

        assertThat(articleItem?.showComments).isFalse()
    }

    @Test
    fun `liveblog related content shows timeAgo`() {
        val testArticle = createTestArticle()
        testArticle.relatedContent = mutableListOf(createRelatedContent("1", RelatedContent.ContentType.LIVEBLOG))

        whenever(simpleDataState.articleEntity).thenReturn(testArticle)

        val models = transformer.transform(simpleDataState).uiModels.filter { it.isRelatedContentModel }

        val articleItem = models.last() as? RelatedContentItem

        assertThat(articleItem?.showTimeAgo).isTrue()
    }

    private fun createTestArticle(): ArticleEntity {
        val article = ArticleEntity().apply {
            articleId = 13L
        }
        return article
    }

    private fun createRelatedContent(
        id: String,
        contentType: RelatedContent.ContentType,
        commentCount: Int = 0
    ) =
        RelatedContent(
            id = id,
            timestampGmt = "sometime",
            title = "title for $id",
            excerpt = "",
            imageUrl = "",
            contentType = contentType,
            byline = "",
            commentCount = commentCount
        )

    private val UiModel.isRelatedContentModel: Boolean
        get() = this is RelatedContentItem || this is RelatedContentSectionTitle
}