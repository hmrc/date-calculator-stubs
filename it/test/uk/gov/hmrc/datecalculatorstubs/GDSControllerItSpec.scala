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

package uk.gov.hmrc.datecalculatorstubs

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

class GDSControllerItSpec
  extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with GuiceOneServerPerSuite {

  val wsClient = app.injector.instanceOf[WSClient]
  val baseUrl = s"http://localhost:$port"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .build()

  val bankHolidayUrl = s"$baseUrl/bank-holidays"

  "PUT /bank-holidays must respond with 400 status if the JSON in the request body cannot be parsed" in {
    val response =
      wsClient
        .url(bankHolidayUrl)
        .withHttpHeaders("Content-Type" -> "application/json")
        .put("{ }")
        .futureValue

    response.status shouldBe 400
  }

  "The insert/get bank holidays mechanism should work" in {
    // start with fresh state
    val deleteRequest1 =
      wsClient
        .url(bankHolidayUrl)
        .delete()
        .futureValue

    deleteRequest1.status shouldBe 204

    // test default response is returned when we haven't inserted a predefined response
    val getRequest1 =
      wsClient
        .url(bankHolidayUrl)
        .withHttpHeaders("From" -> "Dunno")
        .get()
        .futureValue

    getRequest1.status shouldBe 200
    getRequest1.json shouldBe expectedDefaultPredefinedResponseJson

    // test we get back what we insert
    val insert1Response =
      wsClient
        .url(bankHolidayUrl)
        .withHttpHeaders("Content-Type" -> "application/json")
        .put("""{ "status": 218, "body": { "key1": "value1" }  }""")
        .futureValue

    insert1Response.status shouldBe 204

    val getRequest2 =
      wsClient
        .url(bankHolidayUrl)
        .withHttpHeaders("From" -> "Dunno")
        .get()
        .futureValue

    getRequest2.status shouldBe 218
    getRequest2.json shouldBe Json.parse("""{ "key1": "value1" }""")

    // test another insert overwrites the previous one
    val insert2Response =
      wsClient
        .url(bankHolidayUrl)
        .withHttpHeaders("Content-Type" -> "application/json")
        .put("""{ "status": 219, "body": { "key2": "value2" }  }""")
        .futureValue

    insert2Response.status shouldBe 204

    val getRequest3 =
      wsClient
        .url(bankHolidayUrl)
        .withHttpHeaders("From" -> "Dunno")
        .get()
        .futureValue

    getRequest3.status shouldBe 219
    getRequest3.json shouldBe Json.parse("""{ "key2": "value2" }""")

    // test deletes causes a revert to the default response
    val deleteRequest2 =
      wsClient
        .url(bankHolidayUrl)
        .delete()
        .futureValue

    deleteRequest2.status shouldBe 204

    val getRequest4 =
      wsClient
        .url(bankHolidayUrl)
        .withHttpHeaders("From" -> "Dunno")
        .get()
        .futureValue

    getRequest4.status shouldBe 200
    getRequest4.json shouldBe expectedDefaultPredefinedResponseJson
  }

  val expectedDefaultPredefinedResponseJson =
    Json.parse(
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
