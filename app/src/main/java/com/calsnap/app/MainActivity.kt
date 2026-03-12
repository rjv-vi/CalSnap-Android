package com.calsnap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calsnap.app.ui.home.HomeScreen
import com.calsnap.app.ui.onboarding.OnboardingScreen
import com.calsnap.app.ui.theme.CalSnapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalSnapTheme {
                val vm: AppViewModel = viewModel()
                val onboardingDone by vm.onboardingDone.collectAsStateWithLifecycle()

                when (onboardingDone) {
                    null -> { /* загрузка */ }
                    false -> {
                        OnboardingScreen(
                            onFinished = { vm.markOnboardingDone() }
                        )
                    }
                    else -> {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
