Feature: Simulador de préstamo

  Scenario: Simular un préstamo de $20,000 a 12 meses
    Given Acceder a la página de Bancoppel
    When Navegar a la opción Préstamo Personal Bancoppel
    And Seleccionar la opción Simulador e ingresar "monto1" en monto y "plazo1" en plazo, seleccionar Calcular
    Then muestra la cotización del préstamo

  Scenario: Simular un préstamo de $10,000 a 12 meses
    Given Acceder a la página de Bancoppel
    When Navegar a la opción Préstamo Digital Bancoppel
    And PD, Seleccionar la opción Simulador e ingresar "monto2" en monto y "plazo1" en plazo, seleccionar Calcular
    Then PD, muestra la cotización del préstamo

  Scenario: Simular un préstamo con $1 menos del límite inferior
    Given Acceder a la página de Bancoppel
    When LI, Navegar a la opción Préstamo Personal Bancoppel
    And LI, Seleccionar la opción Simulador e ingresar "monto3" en monto y "plazo1" en plazo, seleccionar Calcular
    Then LI, muestra mensaje de error: "min_error"
    
  Scenario: Simular un préstamo con $1 más del límite superior
    Given Acceder a la página de Bancoppel
    When LS,Navegar a la opción Préstamo Personal Bancoppel
    And LS, Seleccionar la opción Simulador e ingresar "monto4" en monto y "plazo1" en plazo, seleccionar Calcular
    Then LS, muestra mensaje de error: "max_error"