Feature: Simulador de Ahorro e Inversiones

  Scenario: Simular una Inversión Creciente de $50,000
    Given IC, Acceder a la página de Bancoppel
    When Navegar a la opción Inversión Creciente
    And Seleccionar la opción Simulador e ingresar "monto1", seleccionar Calcular
    Then muestra la cotización de la inversión