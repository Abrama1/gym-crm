Feature: Workload events processing

  Scenario: ADD event creates trainer document and increments month summary
    Given a workload event payload for trainer "trainer1" with duration 60 on date "2025-01-10" and action "ADD"
    When I POST the workload event
    Then response status should be 200
    And month summary for "trainer1" year 2025 month 1 should be 60

  Scenario: DELETE event decrements month summary (clamped at 0)
    Given a workload event payload for trainer "trainer2" with duration 30 on date "2025-02-05" and action "ADD"
    When I POST the workload event
    Then response status should be 200
    Given a workload event payload for trainer "trainer2" with duration 50 on date "2025-02-05" and action "DELETE"
    When I POST the workload event
    Then response status should be 200
    And month summary for "trainer2" year 2025 month 2 should be 0

  Scenario: Invalid event missing trainer username returns 400
    Given an invalid workload event payload missing trainerUsername
    When I POST the workload event
    Then response status should be 400
