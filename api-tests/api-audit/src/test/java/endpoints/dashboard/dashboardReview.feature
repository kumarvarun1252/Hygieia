Feature: dashboardReview

  Background:
    * header apiUser = 'auditapi'
    * header Authorization = call read('classpath:basic-auth.js')
    * url baseUrl
    * configure readTimeout = 60000

  Scenario: Verify configuration for a team dashboard
    Given path 'dashboardReview'
    And params read('dashboardReview-params.json')
    When method get
    Then status 200
    And match $.review.CODE_QUALITY[*].auditStatuses == [["CODE_QUALITY_DETAIL_MISSING"]]
    And match $.review.CODE_REVIEW[*].url == ["https://github.kdc.capitalone.com/coaf-cs/AutoIL"]