package com.calsnap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.calsnap.app.domain.repository.UserRepository
import com.calsnap.app.ui.navigation.Screen
import com.calsnap.app.ui.onboarding.OnboardingScreen
import com.calsnap.app.ui.theme.CalSnapTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appVm: AppViewModel = hiltViewModel()
            val startDest by appVm.startDestination.collectAsStateWithLifecycle()

            // Держим сплэш пока не определили стартовый экран
            splashScreen.setKeepOnScreenCondition { startDest == null }

            CalSnapTheme {
                startDest?.let { dest ->
                    val navController = rememberNavController()

                    NavHost(
                        navController    = navController,
                        startDestination = dest
                    ) {
                        composable(Screen.Onboarding.route) {
                            OnboardingScreen(
                                onFinished = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.Home.route) {
                            // TODO: HomeScreen — следующий этап
                            PlaceholderScreen("🏠 Главный экран\n\nОнбординг завершён! Следующий этап разработки.")
                        }
                    }
                }
            }
        }
    }
}

// Временный плейсхолдер пока не написан полноценный экран
@Composable
fun PlaceholderScreen(text: String) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text      = text,
            style     = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color     = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val dest = if (userRepository.isOnboardingDone()) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }
            _startDestination.value = dest
        }
    }
}

// Extension — нужен для collectAsStateWithLifecycle в Activity
@Composable
fun <T> kotlinx.coroutines.flow.StateFlow<T>.collectAsStateWithLifecycle(): State<T> {
    return androidx.lifecycle.compose.collectAsStateWithLifecycle()
}
