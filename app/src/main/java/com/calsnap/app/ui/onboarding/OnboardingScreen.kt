package com.calsnap.app.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calsnap.app.domain.model.*
import com.calsnap.app.ui.theme.Streak
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    vm: OnboardingViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        LinearProgressIndicator(
            progress = { (state.step + 1) / 6f },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = Streak,
            trackColor = MaterialTheme.colorScheme.outline,
        )

        Column(modifier = Modifier.fillMaxSize().padding(top = 3.dp)) {
            if (state.step > 0) {
                IconButton(
                    onClick = { vm.prevStep() },
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(Modifier.height(56.dp))
            }

            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                label = "step"
            ) { step ->
                when (step) {
                    0 -> StepName(state, vm, onNext = { vm.nextStep() })
                    1 -> StepGenderDob(state, vm, onNext = { vm.nextStep() })
                    2 -> StepHeightWeight(state, vm, onNext = { vm.nextStep() })
                    3 -> StepActivity(state, vm, onNext = { vm.nextStep() })
                    4 -> StepGoal(state, vm, onNext = { vm.nextStep() })
                    5 -> StepPreferences(state, vm, onFinish = { vm.finish(onFinished) })
                    else -> StepName(state, vm, onNext = { vm.nextStep() })
                }
            }
        }

        if (state.isFinishing) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Streak)
            }
        }
    }
}

@Composable
private fun StepName(state: OnboardingState, vm: OnboardingViewModel, onNext: () -> Unit) {
    val focus = LocalFocusManager.current
    StepScaffold(emoji = "👋", title = "Как тебя зовут?", subtitle = "Это поможет персонализировать CalSnap под тебя", onNext = onNext) {
        OutlinedTextField(
            value = state.name,
            onValueChange = { vm.setName(it) },
            placeholder = { Text("Твоё имя") },
            singleLine = true,
            isError = state.error != null,
            supportingText = state.error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus(); onNext() }),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Streak, cursorColor = Streak),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun StepGenderDob(state: OnboardingState, vm: OnboardingViewModel, onNext: () -> Unit) {
    StepScaffold(emoji = "🧬", title = "Расскажи о себе", subtitle = "Нужно для точного расчёта калорий", onNext = onNext) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Пол", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(Gender.MALE, Gender.FEMALE).forEach { gender ->
                    SelectCard(
                        label = if (gender == Gender.MALE) "👨 Мужской" else "👩 Женский",
                        selected = state.gender == gender,
                        modifier = Modifier.weight(1f),
                        onClick = { vm.setGender(gender) }
                    )
                }
            }

            Text("Дата рождения", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            var year by remember { mutableStateOf("") }
            var month by remember { mutableStateOf("") }
            var day by remember { mutableStateOf("") }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = day,
                    onValueChange = { v -> if (v.length <= 2) day = v.filter { it.isDigit() } },
                    placeholder = { Text("ДД") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Streak),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = month,
                    onValueChange = { v -> if (v.length <= 2) month = v.filter { it.isDigit() } },
                    placeholder = { Text("ММ") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Streak),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { v -> if (v.length <= 4) year = v.filter { it.isDigit() } },
                    placeholder = { Text("ГГГГ") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Streak),
                    modifier = Modifier.weight(2f)
                )
            }

            LaunchedEffect(day, month, year) {
                if (day.length == 2 && month.length == 2 && year.length == 4) {
                    runCatching {
                        val d = LocalDate.of(year.toInt(), month.toInt(), day.toInt())
                        vm.setDateOfBirth(d.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    }
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun StepHeightWeight(state: OnboardingState, vm: OnboardingViewModel, onNext: () -> Unit) {
    StepScaffold(emoji = "📏", title = "Рост и вес", subtitle = "Используется для расчёта BMR по формуле Миффлина-Сент-Жеора", onNext = onNext) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            NumberSlider(label = "Рост", value = state.heightCm, unit = "см", range = 140f..220f, steps = 79, onValueChange = { vm.setHeight(it) })
            NumberSlider(label = "Вес", value = state.weightKg, unit = "кг", range = 40f..200f, steps = 159, onValueChange = { vm.setWeight(it) })
        }
    }
}

@Composable
private fun StepActivity(state: OnboardingState, vm: OnboardingViewModel, onNext: () -> Unit) {
    StepScaffold(emoji = "🏃", title = "Уровень активности", subtitle = "Выбери тот, который лучше всего описывает твой обычный день", onNext = onNext) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val items = listOf(
                Triple(ActivityLevel.SEDENTARY, "🪑", "Работа за столом, почти нет прогулок"),
                Triple(ActivityLevel.LIGHT, "🚶", "Лёгкие упражнения 1-3 раза в неделю"),
                Triple(ActivityLevel.MODERATE, "🏋️", "Умеренные нагрузки 3-5 раз в неделю"),
                Triple(ActivityLevel.ACTIVE, "🚴", "Интенсивные нагрузки 6-7 раз в неделю"),
                Triple(ActivityLevel.VERY_ACTIVE, "🔥", "Физическая работа или 2 тренировки в день")
            )
            items.forEach { (level, emoji, desc) ->
                SelectCard(label = "$emoji ${level.label}", description = desc, selected = state.activityLevel == level, onClick = { vm.setActivityLevel(level) })
            }
        }
    }
}

@Composable
private fun StepGoal(state: OnboardingState, vm: OnboardingViewModel, onNext: () -> Unit) {
    StepScaffold(emoji = "🎯", title = "Твоя цель", subtitle = "Мы скорректируем дневную норму калорий", onNext = onNext) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val items = listOf(
                Triple(Goal.LOSE, "📉", "Дефицит 500 ккал в день"),
                Triple(Goal.MAINTAIN, "⚖️", "Равен твоему TDEE"),
                Triple(Goal.GAIN, "📈", "Профицит 300 ккал в день")
            )
            items.forEach { (goal, emoji, desc) ->
                SelectCard(label = "$emoji ${goal.label}", description = desc, selected = state.goal == goal, onClick = { vm.setGoal(goal) })
            }
        }
    }
}

@Composable
private fun StepPreferences(state: OnboardingState, vm: OnboardingViewModel, onFinish: () -> Unit) {
    StepScaffold(emoji = "✅", title = "Пищевые предпочтения", subtitle = "Необязательно — AI будет учитывать их при анализе", nextLabel = "Начать! 🚀", onNext = onFinish) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(DietPref.VEGETARIAN, DietPref.VEGAN, DietPref.GLUTEN_FREE, DietPref.DAIRY_FREE, DietPref.LOW_CARB, DietPref.HALAL)
                .chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { pref ->
                            FilterChip(
                                selected = pref in state.preferences,
                                onClick = { vm.togglePref(pref) },
                                label = { Text(pref.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Streak.copy(alpha = 0.15f),
                                    selectedLabelColor = Streak
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
        }
    }
}

// ══════════════════════════════════════════════════════════════
// ПЕРЕИСПОЛЬЗУЕМЫЕ КОМПОНЕНТЫ
// ══════════════════════════════════════════════════════════════

@Composable
private fun StepScaffold(
    emoji: String, title: String, subtitle: String,
    nextLabel: String = "Далее →", onNext: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(emoji, fontSize = 48.sp)
                Text(title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
            }
            content()
        }
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Streak)
        ) {
            Text(nextLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SelectCard(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, description: String? = null) {
    val borderColor = if (selected) Streak else MaterialTheme.colorScheme.outline
    val bgColor = if (selected) Streak.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = bgColor,
        border = BorderStroke(width = if (selected) 2.dp else 1.dp, color = borderColor),
        tonalElevation = if (selected) 0.dp else 1.dp
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleMedium, color = if (selected) Streak else MaterialTheme.colorScheme.onSurface, fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold)
                if (description != null) {
                    Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
            }
            if (selected) {
                Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(Streak), contentAlignment = Alignment.Center) {
                    Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NumberSlider(label: String, value: Float, unit: String, range: ClosedFloatingPointRange<Float>, steps: Int, onValueChange: (Float) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Baseline) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.Baseline, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(value.toInt().toString(), style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Black)
                Text(unit, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Slider(
            value = value, onValueChange = onValueChange, valueRange = range, steps = steps,
            colors = SliderDefaults.colors(thumbColor = Streak, activeTrackColor = Streak)
        )
    }
}
