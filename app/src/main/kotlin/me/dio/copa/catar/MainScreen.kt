package me.dio.copa.catar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.dio.copa.catar.domain.extensions.getDate
import me.dio.copa.catar.domain.model.MatchDomain
import me.dio.copa.catar.domain.model.TeamDomain
import me.dio.copa.catar.ui.theme.Shapes

@Composable
fun MainScreen(list: List<MatchDomain> = emptyList(), isEmpty:Boolean, isLoading:Boolean, toggleNotification: (MatchDomain) -> Unit) {
    Title()
    if(isEmpty) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Nenhum jogo encontrado!")
        }
    } else if(isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.width(80.dp))
        }
    } else {
        LazyColumn {
            items(list) { match ->
                MatchCard(match,toggleNotification)
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun Title() {
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(R.drawable.ic_star), contentDescription = null, contentScale = ContentScale
            .Fit, modifier = Modifier.fillMaxWidth().size(80.dp).padding(end = 26.dp))
        Text("Copa do Mundo",
            style = MaterialTheme.typography.h4,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun MatchCard(match: MatchDomain, toggleNotification: (MatchDomain) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().border(1.3.dp,Color.Green, Shapes.large), shape = Shapes.large) {
        Box(modifier = Modifier.background(Color.Black)) {
            AsyncImage(model = match.stadium.image, contentDescription = null,contentScale = ContentScale.Crop,
                modifier = Modifier.height(120.dp), alpha = 0.5f)
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${match.date.getDate()} - ${match.name}", style = MaterialTheme.typography.h6.copy(Color.White))
                    NotificationIcon(match,toggleNotification)
                }
                Spacer(modifier = Modifier.size(16.dp))
                Teams(match)
            }
        }
    }
}

@Composable
fun NotificationIcon(match: MatchDomain, toggleNotification: (MatchDomain) -> Unit) {
    val icon = if(match.notificationEnabled) R.drawable.ic_notifications_active else R.drawable.ic_notifications

    Image(painter = painterResource(icon), modifier = Modifier.clickable { toggleNotification(match) },
        contentDescription = "Ligar/Desligar notifição da partida")
}

@Composable
fun Teams(match: MatchDomain) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement
        .Center) {
        Team(match.team1)
        Text(text = "X",
            modifier = Modifier.padding(end = 16.dp, start = 16.dp),
            style = MaterialTheme.typography.h6.copy(color = Color.White)
        )
        Team(match.team2)
    }
}

@Composable
fun Team(team:TeamDomain) {
    Text(
        text = "${team.flag}  ${team.displayName}",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.h5.copy(color = Color.White)
    )
}