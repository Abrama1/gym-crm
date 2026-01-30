Feature: Workload integration via messaging

  Scenario: ADD event updates trainer month workload
    Given microservices are running
    When a workload ADD event is published for trainer "trainer1" with 60 minutes on "2026-01-15"
    Then workload-service month summary for "trainer1" in 2026-01 should be 60 minutes

  Scenario: DELETE event decreases trainer month workload
    Given microservices are running
    When a workload ADD event is published for trainer "trainer2" with 90 minutes on "2026-01-20"
    And a workload DELETE event is published for trainer "trainer2" with 30 minutes on "2026-01-20"
    Then workload-service month summary for "trainer2" in 2026-01 should be 60 minutes

  Scenario: Invalid event should not update Mongo
    Given microservices are running
    When an invalid workload event is published missing trainer username
    Then workload-service month summary for "missing" in 2026-01 should be 0 minutes
