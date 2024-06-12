package william.miranda.rabobankassignment.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import william.miranda.rabobankassignment.domain.model.User

@Composable
fun UserCard(
    model: User
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp),
                model = model.avatar,
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            Spacer(modifier = Modifier.weight(.3f))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${model.firstName} ${model.surname}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall
                        .plus(
                            TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                )

                Text(
                    text = model.dateOfBirth
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${model.issueCount}"
            )
        }
    }
}

@Composable
fun ProgressIndicator() {
    Surface(
        modifier = Modifier.clickable(enabled = false) {},
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}