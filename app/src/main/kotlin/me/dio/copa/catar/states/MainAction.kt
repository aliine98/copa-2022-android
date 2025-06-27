package me.dio.copa.catar.states

import me.dio.copa.catar.domain.model.MatchDomain

sealed interface MainAction {
    data class MatchesNotFound(val message: String): MainAction
    object Unexpected: MainAction
    data class EnableNotification(val match: MatchDomain) : MainAction
    data class DisableNotification(val match: MatchDomain) : MainAction
}