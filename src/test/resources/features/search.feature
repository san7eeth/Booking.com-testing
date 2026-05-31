Feature: Holiday Home Search

  Scenario: Search Holiday Homes Using Excel Data
    Given User launches Booking.com
    And User closes popup if displayed
    When User reads search data from Excel
    And User enters destination
    And User selects check-in date
    And User selects check-out date
    And User selects adults count
    And User selects children count
    And User selects room count
    And User performs search
    And User sorts by highest traveler rating
    Then Display first 3 properties
    And Display total amount
    And Display charges per night
    And Display traveler rating

  Scenario: Verify Amenity On First Property
    Given User launches Booking.com
    And User closes popup if displayed
    When User reads search data from Excel
    And User enters destination
    And User selects check-in date
    And User selects check-out date
    And User selects adults count
    And User selects children count
    And User selects room count
    And User performs search
    And User sorts by highest traveler rating
    And User opens first property
    Then Elevator or Lift should be verified
    And Display amenity verification result
