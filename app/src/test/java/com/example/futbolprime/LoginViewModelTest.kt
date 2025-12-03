package com.example.futbolprime

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.futbolprime.viewmodel.LoginViewModel
import com.example.futbolprime.viewmodel.LoginState
import com.example.futbolprime.data.SessionManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class LoginViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        sessionManager = mock(SessionManager::class.java)
        viewModel = LoginViewModel(sessionManager)
    }

    @Test
    fun `validarCampos retorna false si los campos están vacíos`() {
        viewModel.username.value = ""
        viewModel.password.value = "123"

        val result = viewModel.validarCampos()

        assertFalse(result)
        assertNotNull(viewModel.usernameError.value)
        assertNotNull(viewModel.passwordError.value)
    }

    @Test
    fun `validarCampos retorna true si los campos son válidos`() {
        viewModel.username.value = "test@mail.com"
        viewModel.password.value = "123456"

        val result = viewModel.validarCampos()

        assertTrue(result)
        assertNull(viewModel.usernameError.value)
        assertNull(viewModel.passwordError.value)
    }

    @Test
    fun `resetLoginState vuelve a Idle`() {
        // Cambiamos el estado manualmente
        viewModel.resetLoginState()

        assertTrue(viewModel.loginState.value is LoginState.Idle)
    }
}
