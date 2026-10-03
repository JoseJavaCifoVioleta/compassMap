// Test inicial abajo está el test 'avanzado'
/*
package com.example.demo.utils;


import org.junit.jupiter.api.Test;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionnaireRunnerTest {

    @Test
    public void testArchitectProfile() {
        // 1. Preparamos el escenario (GIVEN)
        // Simulamos que el usuario escribe 'y' en las 5 preguntas
        String simulatedInput = "y\ny\ny\ny\ny\n";
        Scanner fakeScanner = new Scanner(simulatedInput);
        QuestionnaireRunner runner = new QuestionnaireRunner();

        // 2. Ejecutamos el código que queremos probar (WHEN)
        String result = runner.runQuestionnaire(fakeScanner);

        // 3. Verificamos si el resultado es el esperado (THEN)
        // Esperamos que sea "Architect" porque respondió 'y' a todo
        assertEquals("Architect", result);
    }

    @Test
    public void testNonDeveloperProfile() {
        // Simulamos que el usuario responde 'n' a todo
        String simulatedInput = "n\nn\nn\nn\nn\n";
        Scanner fakeScanner = new Scanner(simulatedInput);
        QuestionnaireRunner runner = new QuestionnaireRunner();

        String result = runner.runQuestionnaire(fakeScanner);

        // Esperamos que sea "Non-Developer"
        assertEquals("Non-Developer", result);
    }
}

*/

package com.example.demo.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionnaireRunnerTest {

    @ParameterizedTest
    @CsvSource({
            "'y\ny\ny\ny\ny\n', Architect",
            "'y\ny\nn\nn\nn\n', 'AI Practitioner'",
            "'n\nn\ny\ny\ny\n', Beginner",
            "'n\nn\nn\nn\nn\n', 'Non-Developer'"
    })
    public void testAllProfiles(String simulatedInput, String expectedProfile) {
        // 1. Configurar el Scanner simulado con los datos de la lista
        Scanner fakeScanner = new Scanner(simulatedInput);
        QuestionnaireRunner runner = new QuestionnaireRunner();

        // 2. Ejecutar la lógica del cuestionario
        String actualProfile = runner.runQuestionnaire(fakeScanner);

        // 3. Verificar si coincide con el perfil esperado
        assertEquals(expectedProfile, actualProfile);
    }
}
