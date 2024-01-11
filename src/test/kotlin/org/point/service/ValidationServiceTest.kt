package org.point.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.point.domain.rest.AddUserRequest
import org.point.exceptions.CustomException
import org.point.exceptions.Errors
import org.point.exceptions.ValidationException

@ExtendWith(MockKExtension::class)
class ValidationServiceTest {
    @InjectMockKs
    lateinit var validationService: ValidationService

    @BeforeEach
    fun setUp() {
        mockkObject(ValueValidationService.Companion)
    }

    @Test
    fun `should validate CreatePaymentSystemRequest`() {
        // given
        val request = AddUserRequest("some name", "mylogin@mail.ru", "pass")

        // when & then
        validationService.validate(request)
    }

    @Test
    fun `should throw validation error on malformed AddUserRequest`() {
        // given
        val request = AddUserRequest("some name", "mylogin-mail.ru", "pass")

        // when
        val error = catchThrowable { validationService.validate(request) }

        // then
        assertThat(error is ValidationException)
        error as CustomException
        assertThat(error.error.code).isEqualTo("VALIDATION_ERROR")
        assertThat(error.error.message).isEqualTo("Invalid parameter value: mylogin-mail.ru")
    }

}
