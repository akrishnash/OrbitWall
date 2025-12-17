package com.orbitwall.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    fun authenticate(onSuccess: () -> Unit) {
        if (uiState.email.isBlank() || uiState.password.length < 6) {
            uiState = uiState.copy(error = "Enter a valid email and 6+ char password")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            delay(1200)
            uiState = uiState.copy(isLoading = false)
            onSuccess()
        }
    }
}

