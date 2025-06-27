package me.dio.copa.catar.states

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dio.copa.catar.core.BaseViewModel
import me.dio.copa.catar.domain.model.MatchDomain
import me.dio.copa.catar.domain.usecase.DisableNotificationUseCase
import me.dio.copa.catar.domain.usecase.EnableNotificationUseCase
import me.dio.copa.catar.domain.usecase.GetMatchesUseCase
import me.dio.copa.catar.remote.NotFoundException
import me.dio.copa.catar.remote.UnexpectedException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMatchesUseCase: GetMatchesUseCase,
    private val enableNotificationUseCase: EnableNotificationUseCase,
    private val disableNotificationUseCase: DisableNotificationUseCase
): BaseViewModel<MainState,MainAction>(MainState.Loading) {
    init {
        getMatches()
    }

    private fun getMatches() = viewModelScope.launch {
        getMatchesUseCase().flowOn(Dispatchers.Main)
            .catch {
                when(it) {
                    is NotFoundException -> sendAction(MainAction.MatchesNotFound(it.message?:""))
                    is UnexpectedException -> sendAction(MainAction.Unexpected)
                }
            }
            .collect {
                if(it.isEmpty()) {
                    setState { MainState.Empty }
                } else {
                    setState { MainState.Success(it) }
                }
            }
    }

    fun toggleNotificacao(match: MatchDomain) {
        viewModelScope.launch {
            val action = if(match.notificationEnabled) {
                disableNotificationUseCase(match.id)
                MainAction.DisableNotification(match)
            } else {
                enableNotificationUseCase(match.id)
                MainAction.EnableNotification(match)
            }

            sendAction(action)
        }
    }
}