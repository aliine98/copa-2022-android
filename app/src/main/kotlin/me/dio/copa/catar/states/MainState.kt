package me.dio.copa.catar.states

import me.dio.copa.catar.domain.model.MatchDomain

sealed interface MainState {
    object Empty: MainState
    data class Success(val list:List<MatchDomain>): MainState
    object Loading: MainState
}
