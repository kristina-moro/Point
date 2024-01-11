package org.point.service

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.point.exceptions.CustomException
import org.point.exceptions.ValidationException

class ValueValidationServiceTest {

    @ParameterizedTest
    @ValueSource(strings = ["some value"])
    fun `should validate required value`(value: String?) {
        // when
        ValueValidationService.validateValueRequired("Property name", value)

        // then
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "  "])
    fun `should not validate required value`(value: String?) {
        // when
        val error = Assertions.catchThrowable { ValueValidationService.validateValueRequired("Property name", value) }

        // then
        assertThat(error is ValidationException)
        error as CustomException
        assertThat(error.error.code).isEqualTo("VALIDATION_ERROR")
        assertThat(error.error.message).isEqualTo("Required field Property name is missing")
    }

    @Test
    fun `should not validate required null value`() {
        // when
        val error = Assertions.catchThrowable { ValueValidationService.validateValueRequired("Property name", null) }

        // then
        assertThat(error is ValidationException)
        error as CustomException
        assertThat(error.error.code).isEqualTo("VALIDATION_ERROR")
        assertThat(error.error.message).isEqualTo("Required field Property name is missing")
    }

    @ParameterizedTest
    @CsvSource("7")
    fun `should validate existing table field case-sensitive`(value: Int?) {
        // when
        val error = Assertions.catchThrowable { ValueValidationService.validatePositive("SomeField", value) }

        // then
        assertThat(error).isNull()
    }

    @ParameterizedTest
    @CsvSource(
        "0, SomeField value should be positive",
        "-1, SomeField value should be positive"
    )
    fun `should not validate existing table field case-sensitive`(value: Int?, expectedError: String?) {
        // when
        val error = Assertions.catchThrowable { ValueValidationService.validatePositive("SomeField", value) }

        // then
        assertThat(error is ValidationException)
        error as CustomException
        assertThat(error.error.code).isEqualTo("VALIDATION_ERROR")
        assertThat(error.error.message).isEqualTo(expectedError)
    }
}
