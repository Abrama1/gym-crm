Feature: Training creation publishes workload event

  Scenario: Trainee creates training and workload event is published
    Given trainee "trainee1" exists and trainer "trainer1" exists and training type "Yoga" exists
    When trainee "trainee1" creates a training with trainer "trainer1" type "Yoga" duration 60 on date "2026-01-15"
    Then response status should be 200
    And workload event should be published with trainer "trainer1" action "ADD" duration 60 and date "2026-01-15"
