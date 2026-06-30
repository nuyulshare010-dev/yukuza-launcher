package com.yukuza.launcher.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yukuza.launcher.domain.model.AppInfo
import com.yukuza.launcher.data.repository.AppRepository
import com.yukuza.launcher.domain.usecase.GetVisibleAppsUseCase
import com.yukuza.launcher.domain.usecase.IncrementLaunchCountUseCase
import com.yukuza.launcher.domain.usecase.ReorderAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Immutable
data class HomeUiState(
    val apps: ImmutableList<AppInfo> = persistentListOf(),
    val focusedAppIndex: Int = 0,
    val isEditMode: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getApps: GetVisibleAppsUseCase,
    private val appRepository: AppRepository,
    private val reorderApps: ReorderAppsUseCase,
    private val incrementLaunchCount: IncrementLaunchCountUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getApps().collect { apps ->
                _uiState.update { it.copy(apps = apps) }
            }
        }
    }

    fun onAppFocused(index: Int) = _uiState.update { it.copy(focusedAppIndex = index) }
    fun onAppLaunched(packageName: String) = viewModelScope.launch { incrementLaunchCount(packageName) }
    fun enterEditMode() = _uiState.update { it.copy(isEditMode = true) }
    fun exitEditMode() = _uiState.update { it.copy(isEditMode = false) }
    fun reorder(packages: List<String>) = viewModelScope.launch { reorderApps(packages) }

    fun refresh() { appRepository.refresh() }

    fun hideApp(packageName: String) {
        viewModelScope.launch { appRepository.hideApp(packageName) }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch { appRepository.unhideApp(packageName) }
    }
}
