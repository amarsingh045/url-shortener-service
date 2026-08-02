Feature: URL shortener API behavior
  As a product stakeholder
  I want clear, executable API examples
  So that I can validate behavior without reading Java code

  Scenario: Create a short URL for a valid long URL
    Given the URL shortener API is running
    When I shorten URL "https://example.com/products"
    Then the response status should be 201
    And the response field "longUrl" should equal "https://example.com/products"
    And the response field "shortCode" should not be empty

  Scenario: Redirect by an existing short code
    Given a shortened URL exists for "https://example.com/redirect"
    When I resolve the created short code
    Then the response status should be 301
    And the response header "Location" should equal "https://example.com/redirect"

  Scenario: Reject invalid URL payload
    Given the URL shortener API is running
    When I shorten URL "not-a-valid-url"
    Then the response status should be 400
    And the error code should equal "INVALID_URL"
    And the error message should contain "Invalid URL"

  Scenario: Return not found for missing short code
    Given the URL shortener API is running
    When I resolve short code "missing123"
    Then the response status should be 404
    And the error code should equal "SHORT_CODE_NOT_FOUND"

  Scenario: Return analytics for created links
    Given I create a link for "https://example.com/analytics"
    When I request analytics
    Then the response status should be 200
    And the response field "totalLinks" should equal number 1
    And the response field "totalRedirects" should equal number 0
