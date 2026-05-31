Feature: Validation

  Scenario: Invalid Search Validation
    Given User launches Booking.com
    And User closes popup if displayed
    And User reads invalid data from Excel
    When User performs invalid search
    Then Warning message should be displayed
    And Screenshot should be captured
