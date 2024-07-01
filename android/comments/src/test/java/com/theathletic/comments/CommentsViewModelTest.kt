package com.theathletic.comments

import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.theathletic.analytics.impressions.ImpressionCalculator
import com.theathletic.comments.analytics.CommentsAnalyticsV2
import com.theathletic.comments.data.Comment
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.data.commentFixture
import com.theathletic.comments.data.commentInputFixture
import com.theathletic.comments.data.commentsFeedFixture
import com.theathletic.comments.data.commentsInputUiStateFixture
import com.theathletic.comments.data.commentsParamFixture
import com.theathletic.comments.data.commentsUiModelFixture
import com.theathletic.comments.data.headerFixture
import com.theathletic.comments.game.ObserveTeamThreadsUseCase
import com.theathletic.comments.game.SwitchTeamThreadUseCase
import com.theathletic.comments.game.data.teamThreadsFixture
import com.theathletic.comments.ui.CommentsDrawerState
import com.theathletic.comments.ui.CommentsUiMapper
import com.theathletic.comments.ui.CommentsUiModel
import com.theathletic.comments.ui.CommentsViewEvent
import com.theathletic.comments.ui.CommentsViewModel
import com.theathletic.comments.ui.components.InputHeaderData
import com.theathletic.comments.utility.CommentsDateFormatter
import com.theathletic.comments.utility.CommentsViewVisibilityManager
import com.theathletic.comments.utility.DwellEventsEmitter
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.entity.user.SortType
import com.theathletic.featureswitch.Features
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.ui.LoadingState
import com.theathletic.ui.ResourceString
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("LongParameterList")
@RunWith(JUnit4::class)
class CommentsViewModelTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock lateinit var userManager: IUserManager
    @Mock lateinit var userDataRepository: IUserDataRepository
    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var commentsRepository: CommentsRepository
    @Mock lateinit var analytics: CommentsAnalyticsV2
    @Mock lateinit var observeCommentsUseCase: ObserveCommentsUseCase
    @Mock lateinit var likeCommentsUseCase: LikeCommentsUseCase
    @Mock lateinit var refreshCommentsUseCase: FetchCommentsUseCase
    @Mock lateinit var observeTeamThreadsUseCase: ObserveTeamThreadsUseCase
    @Mock lateinit var switchTeamThreadUseCase: SwitchTeamThreadUseCase
    @Mock lateinit var analyticsCommentTeamIdUseCase: AnalyticsCommentTeamIdUseCase
    @Mock lateinit var dwellEventsEmitter: DwellEventsEmitter
    @Mock lateinit var visibilityManager: CommentsViewVisibilityManager
    @Mock lateinit var impressionCalculator: ImpressionCalculator
    @Mock lateinit var commentsDateFormatter: CommentsDateFormatter
    @Mock lateinit var dateUtility: DateUtility
    @Mock lateinit var features: Features

    private lateinit var viewModel: CommentsViewModel
    private lateinit var commentsUiMapper: CommentsUiMapper
    private lateinit var addCommentUseCase: AddCommentUseCase

    private val commentsParameters = commentsParamFixture()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        commentsUiMapper = CommentsUiMapper(commentsDateFormatter, dateUtility)
        addCommentUseCase = AddCommentUseCase(commentsRepository)
        prepareViewModel()
    }

    private fun prepareViewModel() {
        val commentsParameters = commentsParamFixture()
        stubInitialMocks()
        viewModel = CommentsViewModel(
            commentsParams = commentsParameters,
            userManager = userManager,
            userDataRepository = userDataRepository,
            commentsRepository = commentsRepository,
            commentsAnalytics = analytics,
            userRepository = userRepository,
            observeCommentsUseCase = observeCommentsUseCase,
            fetchCommentsUseCase = refreshCommentsUseCase,
            addCommentUseCase = addCommentUseCase,
            observeTeamThreadsUseCase = observeTeamThreadsUseCase,
            switchTeamThreadUseCase = switchTeamThreadUseCase,
            analyticsCommentTeamIdUseCase = analyticsCommentTeamIdUseCase,
            likeCommentsUseCase = likeCommentsUseCase,
            dwellEventsEmitter = dwellEventsEmitter,
            visibilityManager = visibilityManager,
            impressionCalculator = impressionCalculator,
            commentsUiMapper = commentsUiMapper,
            features = features
        )
    }

    @Test
    fun `users are able to send comment when their are logged in, subscribed and the code of conduct is accepted`() = runTest {
        val inputComment = "The comment can't be empty too"
        val addedComment = commentFixture(comment = inputComment, isAuthor = true, parentId = "")
        val commentList = listOf(addedComment)

        stubInitialState()
        stubUserAbleToComment(addedComment)

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState.value

        viewModel.onTextChanged(inputComment)
        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.copy(
                inputUiState = commentsInputUiStateFixture(
                    isCommentEnabled = true,
                    inputText = inputComment,
                )
            )
        )
        viewModel.onSendClick {}
        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.copy(
                commentsUiModel = CommentsUiModel(
                    header = headerFixture(),
                    comments = commentsUiModelFixture(commentList, commentsUiMapper),
                    commentsCount = commentList.size,
                ),
                inputUiState = commentsInputUiStateFixture(),
                scrollToIndex = 0,
                loadingState = LoadingState.FINISHED
            )
        )
        testFlow.finish()
    }

    @Test
    fun `users are not able to send comment when they are not logged in`() = runTest {
        val inputComment = "The comment can't be empty too"
        val newComment = commentFixture(comment = inputComment)

        stubInitialState()
        stubUserAbleToComment(comment = newComment, isLoggedIn = false)

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState.value

        viewModel.onTextChanged(inputComment)
        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.copy(
                commentsUiModel = CommentsUiModel(header = headerFixture()),
                inputUiState = initialState.inputUiState.copy(
                    inputText = inputComment,
                    enableSend = true,
                    inputHeaderData = InputHeaderData.EmptyHeaderData
                ),
                loadingState = LoadingState.FINISHED
            )
        )
        viewModel.onSendClick {}
        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.copy(
                commentsUiModel = CommentsUiModel(
                    header = headerFixture(),
                    comments = commentsUiModelFixture(commentsList = emptyList(), commentsUiMapper = commentsUiMapper),
                    commentsCount = 0
                ),
                inputUiState = initialState.inputUiState.copy(
                    inputText = "",
                    enableSend = true,
                    inputHeaderData = InputHeaderData.EmptyHeaderData
                ),
                loadingState = LoadingState.FINISHED
            )
        )
        testFlow.finish()
    }

    @Test
    fun `users are not able to send comments when they are not subscribed`() = runTest {
        val inputComment = "The comment can't be empty too"
        stubUserAbleToComment(comment = commentFixture(comment = inputComment), isSubscribed = false)
        stubInitialState()

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState

        viewModel.onTextChanged(inputComment)
        viewModel.onSendClick {}

        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.value.copy(
                inputUiState = initialState.value.inputUiState.copy(
                    inputText = "",
                    enableSend = true,
                    inputHeaderData = InputHeaderData.EmptyHeaderData
                )
            )
        )
        testFlow.finish()
    }

    @Test
    fun `update the state with the added comment on a successful comment send`() = runTest {
        val newText = "I'm creating a new cool comment"
        val newComment = commentFixture(
            comment = newText,
            id = "commentId-2",
            likesCount = 1,
            parentId = "commentId-2",
            isAuthor = true
        )
        val commentsList = listOf(commentFixture(), newComment)

        stubInitialState(listOf(commentFixture()))
        stubUserAbleToComment(newComment)

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState.value

        viewModel.onReplyClick(newComment.parentId, newComment.id)
        viewModel.onTextChanged(newText)
        viewModel.onSendClick { viewModel.onKeyboardOpenChanged(false) }

        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.copy(
                commentsUiModel = CommentsUiModel(
                    header = headerFixture(),
                    comments = commentsUiModelFixture(commentsList, commentsUiMapper),
                    commentsCount = commentsList.size
                ),
                inputUiState = commentsInputUiStateFixture(),
                scrollToIndex = commentsList.indexOf(newComment),
                loadingState = LoadingState.FINISHED
            )
        )
        testFlow.finish()
    }

    @Test
    fun `remove deleted comment from state#comments on a successful comment deletion`() = runTest {
        stubInitialState()

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState
        val commentId = "commentId-1"

        whenever(commentsRepository.deleteComment(commentId))
            .thenReturn(ResponseStatus.Success(true))

        viewModel.onDeleteClick(commentId = commentId)

        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.value.copy(
                commentsUiModel = initialState.value.commentsUiModel.copy(
                    comments = listOf()
                )
            )
        )
        testFlow.finish()
    }

    @Test
    fun `expose edit header data, input text and comment id on edit comment`() = runTest {
        stubInitialState()

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState

        viewModel.onEditClick("commentId-1", "The developers of this app deserves all the best")

        assertThat(viewModel.viewState.value).isEqualTo(
            initialState.value.copy(
                inputUiState = initialState.value.inputUiState.copy(
                    drawerState = CommentsDrawerState.OPEN,
                    inputHeaderData = InputHeaderData.EditHeaderData(false),
                    inputText = "The developers of this app deserves all the best",
                    editOrReplyId = "commentId-1"
                )
            )
        )
        testFlow.finish()
    }

    @Test
    fun `state has empty header data, empty input text and send disabled when edit or reply is canceled`() = runTest {
        stubInitialState()

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState.value

        viewModel.onEditClick("commentId-1", "Some comment input")
        viewModel.onCancelInput(InputHeaderData.EmptyHeaderData)

        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.copy(
                commentsUiModel = CommentsUiModel(headerFixture()),
                inputUiState = initialState.inputUiState.copy(
                    drawerState = CommentsDrawerState.CLOSED,
                    inputHeaderData = InputHeaderData.EmptyHeaderData,
                    inputText = "",
                    enableSend = false,
                    editOrReplyId = null
                ),
                loadingState = LoadingState.FINISHED
            )
        )
        testFlow.finish()
    }

    @Test
    fun `expose input header data, scroll to index and edit or reply id on reply comment`() = runTest {
        stubInitialState(listOf(commentFixture()))
        whenever(userManager.isUserTempBanned()).thenReturn(false)

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState.value

        viewModel.onReplyClick("commentId-1", "commentId-1")

        assertStream(testFlow).lastEvent().isEqualTo(
            initialState.copy(
                commentsUiModel = CommentsUiModel(
                    header = headerFixture(),
                    comments = commentsUiModelFixture(listOf(commentFixture()), commentsUiMapper)
                ),
                inputUiState = initialState.inputUiState.copy(
                    drawerState = CommentsDrawerState.OPEN,
                    inputHeaderData = InputHeaderData.ReplyHeaderData("Michael Bryant"),
                    editOrReplyId = "commentId-1",
                ),
                scrollToIndex = 0,
                loadingState = LoadingState.FINISHED
            )
        )
        testFlow.finish()
    }

    @Test
    fun `does not proceed with comment reply when the user is banned`() = runTest {
        stubInitialState()
        whenever(userManager.isUserTempBanned()).thenReturn(true)

        val testFlow = testFlowOf(viewModel.viewState)
        val initialState = viewModel.viewState.value

        viewModel.onReplyClick("commentId-1", "commentId-1")

        assertStream(testFlow).lastEvent().isEqualTo(initialState)
        testFlow.finish()
    }

    @Test
    fun `show banned comments message for banned users on comment reply`() = runTest {
        stubInitialState()
        whenever(userManager.isUserTempBanned()).thenReturn(true)
        whenever(userManager.getBannedDaysLeft()).thenReturn(2)

        val testFlow = testFlowOf(viewModel.viewEvents)

        viewModel.onReplyClick("commentId-1", "commentId-1")

        assertStream(testFlow).lastEvent().isEqualTo(
            CommentsViewEvent.ShowTempBanMessage(2)
        )

        testFlow.finish()
    }

    @Test
    fun `show banned comments message for banned users on comment input click`() = runTest {
        whenever(features.isCommentDrawerEnabled).thenReturn(false)
        prepareViewModel()

        stubInitialState()
        whenever(userManager.isUserTempBanned()).thenReturn(true)
        whenever(userManager.getBannedDaysLeft()).thenReturn(2)

        val testFlow = testFlowOf(viewModel.viewEvents)

        viewModel.onCommentInputClick()

        assert(features.isCommentDrawerEnabled.not()) {
            "When comment drawer is enabled we don't expect ShowTempBanMessage event"
        }
        assertStream(testFlow).lastEvent().isEqualTo(
            CommentsViewEvent.ShowTempBanMessage(2)
        )

        testFlow.finish()
    }

    @Test
    fun `show banned comment-input header for banned users on comment input click`() = runTest {
        whenever(features.isCommentDrawerEnabled).thenReturn(true)
        prepareViewModel()

        stubInitialState()
        whenever(userManager.isUserTempBanned()).thenReturn(true)
        whenever(userManager.getBannedDaysLeft()).thenReturn(2)

        assertThat(viewModel.viewState.value.inputUiState.isCommentDrawerFeatureEnabled).isTrue()
        assertThat(viewModel.viewState.value.inputUiState.drawerState).isEqualTo(CommentsDrawerState.CLOSED)

        viewModel.onCommentInputClick()

        assertThat(viewModel.viewState.value.inputUiState.inputHeaderData).isInstanceOf(InputHeaderData.TempBannedHeaderData::class.java)
    }

    @Test
    fun `drawer collapses when user closes keyboard only while editing comment`() = runTest {
        whenever(features.isCommentDrawerEnabled).thenReturn(true)
        prepareViewModel()

        assertThat(viewModel.viewState.value.inputUiState.isCommentDrawerFeatureEnabled).isTrue()
        assertThat(viewModel.viewState.value.inputUiState.drawerState).isEqualTo(CommentsDrawerState.CLOSED)

        viewModel.onKeyboardOpenChanged(true)
        assertThat(viewModel.viewState.value.inputUiState.drawerState).isEqualTo(CommentsDrawerState.OPEN)

        viewModel.onKeyboardOpenChanged(false)
        assertWithMessage("We expect drawer to be closed when keyboard is closed while there is no text")
            .that(viewModel.viewState.value.inputUiState.drawerState)
            .isEqualTo(CommentsDrawerState.CLOSED)

        viewModel.onKeyboardOpenChanged(true)
        viewModel.onTextChanged("Here's what I think...")
        assertThat(viewModel.viewState.value.inputUiState.drawerState).isEqualTo(CommentsDrawerState.OPEN)

        viewModel.onKeyboardOpenChanged(false)
        assertThat(viewModel.viewState.value.inputUiState.drawerState).isEqualTo(CommentsDrawerState.COLLAPSED)
    }

    @Test
    fun `state is restored after user taps undo`() = runTest {
        whenever(features.isCommentDrawerEnabled).thenReturn(true)
        prepareViewModel()

        assertThat(viewModel.viewState.value.inputUiState.isCommentDrawerFeatureEnabled).isTrue()
        val testFlow = testFlowOf(viewModel.viewEvents)

        viewModel.onReplyClick("commentId-1", "commentId-1")
        viewModel.onKeyboardOpenChanged(true)
        viewModel.onTextChanged("A long, involved reply that I don't want to lose")
        val statePriorToUndo = viewModel.viewState.value.inputUiState

        viewModel.onCancelInput(viewModel.viewState.value.inputUiState.inputHeaderData)
        val undoAction = viewModel.viewState.value.inputUiState.availableUndo
        assertThat(undoAction).isNotNull()
        undoAction?.also {
            viewModel.onUndoCancel(it.priorState)
            val newState = viewModel.viewState.value.inputUiState
            assertThat(newState.inputText).isEqualTo(statePriorToUndo.inputText)
            assertThat(newState.drawerState).isEqualTo(CommentsDrawerState.OPEN)
            assertThat(newState.isCommentDrawerFeatureEnabled).isEqualTo(statePriorToUndo.isCommentDrawerFeatureEnabled)
            assertThat(newState.inputHeaderData).isEqualTo(statePriorToUndo.inputHeaderData)
            assertThat(newState.enableSend).isEqualTo(statePriorToUndo.enableSend)
            assertThat(newState.editOrReplyId).isEqualTo(statePriorToUndo.editOrReplyId)
            assertThat(newState.availableUndo).isNull()
        }
        testFlow.finish()
    }

    /**
     * The comment list is initialized with one item (using commentFixture() to create the comment)
     * By default comments are sorted by most liked
     */
    private fun stubInitialState(
        commentsList: List<Comment>? = null,
    ) {
        val commentFeedFlow = flowOf(commentsFeedFixture(comments = commentsList ?: emptyList()))
        val teamThreadsFlow = flowOf(teamThreadsFixture())

        whenever(
            observeCommentsUseCase(commentsParameters.sourceDescriptor.id, commentsParameters.sourceType, viewModel.viewModelScope)
        ).thenReturn(commentFeedFlow)
        whenever(
            observeTeamThreadsUseCase(commentsParameters.sourceDescriptor.id)
        ).thenReturn(teamThreadsFlow)

        viewModel.onCreate(TestLifecycleOwner())
    }

    private fun stubInitialMocks() {
        whenever(visibilityManager.isVisible).thenReturn(MutableStateFlow(true))
        whenever(userManager.getCommentsSortType(commentsParameters.sourceType)).thenReturn(SortType.MOST_LIKED)
        whenever(commentsDateFormatter.format(Datetime(1674067554000)))
            .thenReturn(ResourceString.StringWrapper("Jan, 18 2023"))
    }

    private suspend fun stubUserAbleToComment(
        comment: Comment,
        isLoggedIn: Boolean = true,
        isSubscribed: Boolean = true,
        acceptedCodeOfConduct: Boolean = true,
    ) {
        whenever(
            commentsRepository.addComment(commentInputFixture(content = comment.comment, parentId = comment.parentId))
        ).thenReturn(comment)
        whenever(userManager.isUserLoggedIn()).thenReturn(isLoggedIn)
        whenever(userManager.isUserSubscribed()).thenReturn(isSubscribed)
        whenever(userManager.isCodeOfConductAccepted()).thenReturn(acceptedCodeOfConduct)
    }
}