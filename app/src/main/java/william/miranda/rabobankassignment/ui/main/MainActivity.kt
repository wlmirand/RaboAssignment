package william.miranda.rabobankassignment.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import william.miranda.rabobankassignment.R
import william.miranda.rabobankassignment.domain.model.User
import william.miranda.rabobankassignment.ui.composable.ProgressIndicator
import william.miranda.rabobankassignment.ui.composable.UserCard
import william.miranda.rabobankassignment.ui.theme.RabobankAssignmentTheme

private const val DEFAULT_URL =
    "https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/main/issues.csv"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RabobankAssignmentTheme {
                ScaffoldStructure()
            }
        }
    }
}

@Composable
fun ScaffoldStructure(
    mainViewModel: MainViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() }
    ) { innerPadding ->
        AppContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            mainViewModel = mainViewModel
        )
    }
}

@Composable
fun AppContent(
    modifier: Modifier,
    mainViewModel: MainViewModel
) {
    val uiState = mainViewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        var url by remember { mutableStateOf(DEFAULT_URL) }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = url,
            onValueChange = {},
            maxLines = 1
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { mainViewModel.downloadAndParse(url) }
        ) {
            Text(text = stringResource(id = R.string.fetch_csv))
        }

        when (val stateValue = uiState.value) {
            is UiState.Success -> ListUsers(mainViewModel, stateValue.data)
            is UiState.Error -> DisplayError(stateValue.error)
            else -> {}
        }
    }

    if (uiState.value is UiState.Loading) {
        ProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = stringResource(id = R.string.app_name)) }
    )
}

@Composable
fun ListUsers(
    mainViewModel: MainViewModel,
    users: List<User>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState
    ) {
        items(
            items = users,
            key = { it.uuid }
        ) {
            UserCard(model = it)
        }
        item {
            LaunchedEffect(true) {
                val currentIndex = listState.firstVisibleItemIndex + 1
                mainViewModel.downloadMore()
                listState.scrollToItem(currentIndex)
            }
        }
    }
}

@Composable
fun DisplayError(
    message: String
) {
    Text(text = message)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RabobankAssignmentTheme {
        ScaffoldStructure()
    }
}