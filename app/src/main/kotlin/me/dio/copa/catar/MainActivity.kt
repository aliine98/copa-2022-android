package me.dio.copa.catar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.dio.copa.catar.domain.model.MatchDomain
import me.dio.copa.catar.domain.model.Stadium
import me.dio.copa.catar.domain.model.TeamDomain
import me.dio.copa.catar.extensions.observe
import me.dio.copa.catar.notification.scheduler.extensions.NotificationWorker
import me.dio.copa.catar.states.MainAction
import me.dio.copa.catar.states.MainState
import me.dio.copa.catar.states.MainViewModel
import me.dio.copa.catar.ui.theme.Copa2022Theme
import java.time.LocalDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeActions()
        setContent {
            Copa2022Theme {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val state by viewModel.state.collectAsState()
                    when(state) {
                        MainState.Empty -> {
                            MainScreen(isEmpty = true, isLoading = false){}
                        }
                        MainState.Loading -> {
                            MainScreen(isEmpty = false, isLoading = true){}
                        }
                        is MainState.Success -> {
                            MainScreen(
                                (state as MainState.Success).list,
                                isEmpty = false,
                                isLoading = false,
                                viewModel::toggleNotificacao
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeActions() {
        viewModel.action.observe(this) {action ->
            when(action) {
                is MainAction.DisableNotification -> {
                    NotificationWorker.cancel(applicationContext,action.match)
                }
                is MainAction.EnableNotification -> {
                    NotificationWorker.start(applicationContext,action.match)
                }
                is MainAction.MatchesNotFound -> Toast.makeText(this,action.message,Toast.LENGTH_LONG).show()
                MainAction.Unexpected -> Toast.makeText(this,"Um erro inesperado aconteceu! Tente novamente mais " +
                        "tarde!",Toast.LENGTH_LONG).show()
            }
        }
    }
}