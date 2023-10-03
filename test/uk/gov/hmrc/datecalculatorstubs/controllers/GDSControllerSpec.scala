/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.datecalculatorstubs.controllers

import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.datecalculatorstubs.controllers.testsupport.ItSpec
import uk.gov.hmrc.datecalculatorstubs.models.PredefinedResponse
import uk.gov.hmrc.datecalculatorstubs.repos.BankHolidayResponseRepo

class GDSControllerSpec extends ItSpec {

  lazy val controller = fakeApplication().injector.instanceOf[GDSController]

  lazy val repo = fakeApplication().injector.instanceOf[BankHolidayResponseRepo]

  "PUT /bank-holidays must" - {

    "insert documents into mongo" in {
      val predefinedResponse = PredefinedResponse(1, Some(JsString("Hi!")))

      repo.find().futureValue shouldBe None

      val result = controller.putBankHolidays(FakeRequest().withBody(predefinedResponse))
      status(result) shouldBe NO_CONTENT

      repo.find().futureValue shouldBe Some(predefinedResponse)
    }

  }

  "DELETE /bank-holidays must" - {

    "remove any predefined responses in mongo" in {
      val predefinedResponse = PredefinedResponse(2, None)

      repo.insert(predefinedResponse).futureValue shouldBe ()
      repo.find().futureValue shouldBe Some(predefinedResponse)

      val result = controller.deleteBankHolidays(FakeRequest())
      status(result) shouldBe NO_CONTENT

      repo.find().futureValue shouldBe None
    }

  }

  "GET /bank-holidays must" - {

    "return a 400 if no 'From' header can be found in the request" in {
      val result = controller.getBankHolidays(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe ("No valid 'From' header in request")
    }

    "return a 400 if an empty value is found in the 'From' header in the request" in {
      val result = controller.getBankHolidays(FakeRequest().withHeaders("From" -> ""))

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe ("No valid 'From' header in request")
    }

    "return a predefined response if one has been inserted" in {
      val predefinedResponse = PredefinedResponse(3, Some(JsString("Bye!")))

      repo.insert(predefinedResponse).futureValue shouldBe ()

      val result = controller.getBankHolidays(FakeRequest().withHeaders("From" -> "Me"))
      status(result) shouldBe 3

      contentAsJson(result) shouldBe JsString("Bye!")

    }

    "return the default response if no predefined response has been inserted yet" in {
      repo.find().futureValue shouldBe None

      val result = controller.getBankHolidays(FakeRequest().withHeaders("From" -> "You"))
      status(result) shouldBe OK

      contentAsJson(result) shouldBe Json.parse(
        """
          |{
          |  "england-and-wales": {
          |    "division": "england-and-wales",
          |    "events": [
          |      {
          |        "title": "Earliest bank holiday",
          |        "date": "0000-01-01",
          |        "notes": "",
          |        "bunting": true
          |      },
          |      {
          |        "title": "Latest bank holiday",
          |        "date": "9999-12-31",
          |        "notes": "",
          |        "bunting": true
          |      }
          |    ]
          |  },
          |  "scotland": {
          |    "division": "scotland",
          |    "events": [
          |      {
          |        "title": "Earliest bank holiday",
          |        "date": "0000-01-01",
          |        "notes": "",
          |        "bunting": true
          |      },
          |      {
          |        "title": "Latest bank holiday",
          |         "date": "9999-12-31",
          |         "notes": "",
          |         "bunting": true
          |      }
          |    ]
          |  },
          |  "northern-ireland": {
          |    "division": "northern-ireland",
          |    "events": [
          |      {
          |        "title": "Earliest bank holiday",
          |        "date": "0000-01-01",
          |        "notes": "",
          |        "bunting": true
          |      },
          |      {
          |        "title": "Latest bank holiday",
          |        "date": "9999-01-01",
          |        "notes": "",
          |        "bunting": true
          |      }
          |    ]
          |  }
          |}
          |""".stripMargin
      )
    }

  }

}
