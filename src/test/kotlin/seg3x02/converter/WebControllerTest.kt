package seg3x02.converter

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(WebController::class)
class WebControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun request_to_home() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(view().name("home"))
            .andExpect(content().string(containsString("Calculatrice")))
            .andExpect(content().string(containsString("action=\"/calculate\"")))
            .andExpect(model().attributeDoesNotExist("result"))
    }

    @ParameterizedTest
    @CsvSource("5,3,+,8.0", "5,3,-,2.0", "5,3,*,15.0", "6,3,/,2.0", "1.5,2.5,+,4.0")
    fun calculates(first: String, second: String, operation: String, expected: Double) {
        mockMvc.perform(post("/calculate")
            .param("firstNumber", first)
            .param("secondNumber", second)
            .param("operation", operation))
            .andExpect(status().isOk)
            .andExpect(view().name("home"))
            .andExpect(model().attribute("result", expected))
            .andExpect(model().attribute("error", ""))
            .andExpect(model().attribute("firstNumber", first))
            .andExpect(model().attribute("secondNumber", second))
            .andExpect(model().attribute("operation", operation))
            .andExpect(content().string(containsString("<output>$expected</output>")))
    }

    @Test
    fun division_by_zero_shows_error() {
        mockMvc.perform(post("/calculate")
            .param("firstNumber", "6")
            .param("secondNumber", "0")
            .param("operation", "/"))
            .andExpect(status().isOk)
            .andExpect(view().name("home"))
            .andExpect(model().attribute("error", "La division par zéro est impossible."))
            .andExpect(model().attributeDoesNotExist("result"))
            .andExpect(content().string(containsString("La division par zéro est impossible.")))
    }

    @Test
    fun invalid_number_shows_error() {
        mockMvc.perform(post("/calculate")
            .param("firstNumber", "abc")
            .param("secondNumber", "3")
            .param("operation", "+"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("error", "Veuillez saisir deux nombres valides."))
            .andExpect(model().attributeDoesNotExist("result"))
    }

    @Test
    fun invalid_operation_shows_error() {
        mockMvc.perform(post("/calculate")
            .param("firstNumber", "5")
            .param("secondNumber", "3")
            .param("operation", "unknown"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("error", "Veuillez choisir une opération valide."))
            .andExpect(model().attributeDoesNotExist("result"))
    }
}
