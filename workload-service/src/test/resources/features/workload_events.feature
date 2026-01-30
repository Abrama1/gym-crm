Feature: Workload events update trainer monthly summary

  Scenario: ADD event creates trainer document and increases month minutes
    Given workload repository is empty
    When I POST workload event:
      | trainerUsername | john.trainer |
      | trainerFirstName | John |
      | trainerLastName | Trainer |
      | active | true |
      | trainingDate | 2026-01-15 |
      | trainingDurationMinutes | 60 |
      | actionType | ADD |
    Then workload month minutes for "john.trainer" in 2026/1 should be 60

  Scenario: Multiple ADD events accumulate in the same month
    Given workload repository is empty
    When I POST workload event:
      | trainerUsername | john.trainer |
      | trainerFirstName | John |
      | trainerLastName | Trainer |
      | active | true |
      | trainingDate | 2026-01-01 |
      | trainingDurationMinutes | 30 |
      | actionType | ADD |
    And I POST workload event:
      | trainerUsername | john.trainer |
      | trainerFirstName | John |
      | trainerLastName | Trainer |
      | active | true |
      | trainingDate | 2026-01-20 |
      | trainingDurationMinutes | 45 |
      | actionType | ADD |
    Then workload month minutes for "john.trainer" in 2026/1 should be 75

  Scenario: DELETE event decreases minutes but never goes below zero
    Given workload repository is empty
    When I POST workload event:
      | trainerUsername | john.trainer |
      | trainerFirstName | John |
      | trainerLastName | Trainer |
      | active | true |
      | trainingDate | 2026-01-10 |
      | trainingDurationMinutes | 30 |
      | actionType | ADD |
    And I POST workload event:
      | trainerUsername | john.trainer |
      | trainerFirstName | John |
      | trainerLastName | Trainer |
      | active | true |
      | trainingDate | 2026-01-10 |
      | trainingDurationMinutes | 50 |
      | actionType | DELETE |
    Then workload month minutes for "john.trainer" in 2026/1 should be 0

  Scenario: Invalid message returns 400
    Given workload repository is empty
    When I POST workload event with missing trainerUsername
    Then response status should be 400
