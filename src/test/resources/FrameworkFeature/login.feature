@loginFunc
Feature: Login functionality
  Background: Prerequisites
    Given User is on login page

  @ValidLogin
  Scenario: User login with valid credential
    When User fill valid credentials and click on login button
    Then Verify user is logged in

  @InvalidLogin
  Scenario Outline: User login with invalid credential or blank credential
    When User fill username as "<username>" and password as "<password>" and click on login button
    Then Verify the error message "<error_message>" for this "<test>"

    Examples:
      |test    |username              |password|error_message                                   |
      |email   |wrong                 |123456  |Please enter valid email address                |
      |email   |                      |123456  |Please enter a email address                    |
      |password|superadmin@yopmail.com|wron    |Your password must be at least 5 characters long|
      |invalid |wrong@yopmail.com     |123456  |You are not authorized person                   |
      |password|superadmin@yopmail.com|        |Please provide a password                       |

