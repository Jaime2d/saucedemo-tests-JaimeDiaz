Feature: Login functionality

  Scenario: Login without username
    Given I am on the Login page
    When I try to login without entering a username
    Then I should see "Username is required"

  Scenario: Login without password
    Given I am on the Login page
    When I enter a username but no password
    Then I should see "Password is required"

  Scenario Outline: Successful login with valid users
    Given I am on the Login page
    When I login with username "<username>" and password "secret_sauce"
    Then I should see the Swag Labs dashboard

    Examples:
      | username               |
      | standard_user          |
      | problem_user           |
      | performance_glitch_user|
