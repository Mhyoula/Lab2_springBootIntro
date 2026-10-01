package seg3x02.converter

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WebController {
    @ModelAttribute
    fun addAttributes(model: Model) {
        model.addAttribute("firstNumber", "")
        model.addAttribute("secondNumber", "")
        model.addAttribute("operation", "+")
        model.addAttribute("error", "")
    }

    @GetMapping("/")
    fun home(): String = "home"

    @PostMapping("/calculate")
    fun calculate(
        @RequestParam(defaultValue = "") firstNumber: String,
        @RequestParam(defaultValue = "") secondNumber: String,
        @RequestParam(defaultValue = "") operation: String,
        model: Model
    ): String {
        model.addAttribute("firstNumber", firstNumber)
        model.addAttribute("secondNumber", secondNumber)
        model.addAttribute("operation", operation)

        val first = firstNumber.toDoubleOrNull()
        val second = secondNumber.toDoubleOrNull()
        if (first == null || second == null || !first.isFinite() || !second.isFinite()) {
            model.addAttribute("error", "Veuillez saisir deux nombres valides.")
            return "home"
        }

        val result = when (operation) {
            "+" -> first + second
            "-" -> first - second
            "*" -> first * second
            "/" -> {
                if (second == 0.0) {
                    model.addAttribute("error", "La division par zéro est impossible.")
                    return "home"
                }
                first / second
            }
            else -> {
                model.addAttribute("error", "Veuillez choisir une opération valide.")
                return "home"
            }
        }
        if (!result.isFinite()) {
            model.addAttribute("error", "Le résultat dépasse les limites de la calculatrice.")
            return "home"
        }
        model.addAttribute("result", result)
        return "home"
    }
}
