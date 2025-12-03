package com.example.futbolprime

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.futbolprime.data.SessionManager
import com.example.futbolprime.viewmodel.UserViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class UserViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val sessionManager = mock<SessionManager>()
    private val viewModel = UserViewModel(sessionManager)

    @Test
    fun `login actualiza token y nombre`() {
        viewModel.login("TOKEN123", "Nicolas")

        verify(sessionManager).saveSession("TOKEN123", "Nicolas")
        assertEquals("TOKEN123", viewModel.token.value)
        assertEquals("Nicolas", viewModel.nombre.value)
    }

    @Test
    fun `logout limpia la sesion`() {
        viewModel.logout()

        verify(sessionManager).clearSession()
        assertNull(viewModel.token.value)
        assertNull(viewModel.nombre.value)
    }

    @Test
    fun `isLoggedIn llama al sessionManager`() {
        whenever(sessionManager.isLoggedIn()).thenReturn(true)

        val result = viewModel.isLoggedIn()

        assertTrue(result)
    }
}
