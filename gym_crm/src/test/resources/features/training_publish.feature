Feature: Training creation publishes workload event

  Scenario: Trainee creates training and workload event is published
    Given trainee "trainee1" exists and trainer "trainer1" exists and training type "Yoga" exists
    When trainee "trainee1" creates a training with trainer "trainer1" type "Yoga" duration 60 on date "2026-01-15"
    Then response status should be 200
    And workload event should be published with trainer "trainer1" action "ADD" duration 60 and date "2026-01-15"

  Scenario: Trainee cannot create training for another trainee (403)
    Given trainee "trainee1" exists and trainer "trainer1" exists and training type "Yoga" exists
    When trainee "trainee1" tries to create a training for traineeUsername "otherTrainee" with trainer "trainer1" type "Yoga" duration 60 on date "2026-01-15"
    Then response status should be 403
    And workload event should not be published

  Scenario: Trainer creates training and workload event is published
    Given trainee "trainee1" exists and trainer "trainer1" exists and training type "Yoga" exists
    When trainer "trainer1" creates a training with trainee "trainee1" type "Yoga" duration 45 on date "2026-01-20"
    Then response status should be 200
    And workload event should be published with trainer "trainer1" action "ADD" duration 45 and date "2026-01-20"

  Scenario: Trainer cannot create training for another trainer (403)
    Given trainee "trainee1" exists and trainer "trainer1" exists and training type "Yoga" exists
    When trainer "trainer1" tries to create a training for trainerUsername "otherTrainer" with trainee "trainee1" type "Yoga" duration 45 on date "2026-01-20"
    Then response status should be 403
    And workload event should not be published
