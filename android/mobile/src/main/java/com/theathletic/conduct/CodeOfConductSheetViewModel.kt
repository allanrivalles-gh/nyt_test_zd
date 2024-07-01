package com.theathletic.conduct

import androidx.annotation.StringRes
import com.theathletic.R
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.codeofconduct.ui.CodeOfConductUi
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.user.data.UserRepository

class CodeOfConductSheetViewModel @AutoKoin constructor(
    @Assisted private val screenNavigator: ScreenNavigator,
    private val userRepository: UserRepository
) : AthleticViewModel<CodeOfConductState, CodeOfConductContract.ViewState>(),
    CodeOfConductContract.Interaction {

    override val initialState: CodeOfConductState = CodeOfConductState()

    override fun transform(data: CodeOfConductState): CodeOfConductContract.ViewState {
        with(data) {
            return CodeOfConductContract.ViewState(
                codeOfConductUi = CodeOfConductUi(
                    titleRes = titleRes,
                    introRes = introRes,
                    firstSubtitleRes = firstSubtitleRes,
                    firstTextRes = firstTextRes,
                    secondSubtitleRes = secondSubtitleRes,
                    secondTextRes = secondTextRes,
                    thirdSubtitleRes = thirdSubtitleRes,
                    thirdTextRes = thirdTextRes,
                    fourthSubtitleRes = fourthSubtitleRes,
                    fourthTextRes = fourthTextRes,
                    epilogueRes = epilogueRes,
                    agreeRes = agreeRes,
                    disagreeRes = disagreeRes,
                )
            )
        }
    }

    override fun onFAQClicked() {
        screenNavigator.startFaqActivity()
    }

    override fun onContactSupportClicked() {
        screenNavigator.startContactSupport()
    }

    override fun onAgreeClicked() {
        userRepository.acceptChatCodeOfConduct()
        screenNavigator.finishActivity()
    }

    override fun onDisagreeClicked() {
        screenNavigator.finishActivity()
    }
}

data class CodeOfConductState(
    @StringRes
    val titleRes: Int = R.string.comments_check_conduct_title,
    @StringRes
    val introRes: Int = R.string.comments_check_conduct_introduction,
    @StringRes
    val firstSubtitleRes: Int = R.string.comments_check_conduct_subtitle_1,
    @StringRes
    val firstTextRes: Int = R.string.comments_check_conduct_text_1,
    @StringRes
    val secondSubtitleRes: Int = R.string.comments_check_conduct_subtitle_2,
    @StringRes
    val secondTextRes: Int = R.string.comments_check_conduct_text_2,
    @StringRes
    val thirdSubtitleRes: Int = R.string.comments_check_conduct_subtitle_3,
    @StringRes
    val thirdTextRes: Int = R.string.comments_check_conduct_text_3,
    @StringRes
    val fourthSubtitleRes: Int = R.string.comments_check_conduct_subtitle_4,
    @StringRes
    val fourthTextRes: Int = R.string.comments_check_conduct_text_4,
    @StringRes
    val epilogueRes: Int = R.string.comments_check_conduct_epilogue,
    @StringRes
    val agreeRes: Int = R.string.comments_check_conduct_button_yes,
    @StringRes
    val disagreeRes: Int = R.string.comments_check_conduct_button_no,
) : DataState