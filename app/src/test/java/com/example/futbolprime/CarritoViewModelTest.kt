package com.example.futbolprime

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.futbolprime.viewmodel.CarritoViewModel
import com.example.futbolprime.network.ApiService
import com.example.futbolprime.repository.CarritoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class CarritoViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: CarritoViewModel

    @Before
    fun setup() {
        val apiService = mock(ApiService::class.java)
        val repo = mock(CarritoRepository::class.java)

        viewModel = CarritoViewModel(apiService, repo)
    }

    @Test
    fun `validarCampos retorna false si los campos están vacíos`() {
        viewModel.nombre.value = ""
        viewModel.email.value = ""
        viewModel.direccion.value = ""
        viewModel.tarjeta.value = ""

        val result = viewModel.validarCampos()

        assertFalse(result)
        assertNotNull(viewModel.nombreError.value)
        assertNotNull(viewModel.emailError.value)
        assertNotNull(viewModel.direccionError.value)
        assertNotNull(viewModel.tarjetaError.value)
    }

    @Test
    fun `validarCampos retorna true si los campos son correctos`() {
        viewModel.nombre.value = "Nicolás"
        viewModel.email.value = "test@example.com"
        viewModel.direccion.value = "Casa 123"
        viewModel.tarjeta.value = "123456789012"

        val result = viewModel.validarCampos()

        assertTrue(result)
        assertNull(viewModel.nombreError.value)
        assertNull(viewModel.emailError.value)
        assertNull(viewModel.direccionError.value)
        assertNull(viewModel.tarjetaError.value)
    }

    @Test
    fun `limpiarCampos resetea todos los campos`() = runTest {
        viewModel.nombre.value = "Nicolás"
        viewModel.email.value = "mail@mail.com"
        viewModel.direccion.value = "Dirección"
        viewModel.tarjeta.value = "123456789012"

        viewModel.limpiarCampos()

        assertEquals("", viewModel.nombre.value)
        assertEquals("", viewModel.email.value)
        assertEquals("", viewModel.direccion.value)
        assertEquals("", viewModel.tarjeta.value)
        assertNull(viewModel.nombreError.value)
        assertNull(viewModel.emailError.value)
        assertNull(viewModel.direccionError.value)
        assertNull(viewModel.tarjetaError.value)
    }
}
