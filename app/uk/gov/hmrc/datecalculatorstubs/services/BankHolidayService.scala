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

package uk.gov.hmrc.datecalculatorstubs.services

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import uk.gov.hmrc.datecalculatorstubs.models.PredefinedResponse
import uk.gov.hmrc.datecalculatorstubs.repos.BankHolidayResponseRepo

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BankHolidayService @Inject() (bankHolidayResponseRepo: BankHolidayResponseRepo)(implicit ec: ExecutionContext) {

  def insertPredefinedResponse(predefinedResponse: PredefinedResponse): Future[Unit] =
    bankHolidayResponseRepo.insert(predefinedResponse)

  def getPredefinedResponse(): Future[PredefinedResponse] =
    bankHolidayResponseRepo.find().map(_.getOrElse(BankHolidayService.defaultPredefinedResponse))

  def deletePredefinedResponse(): Future[Unit] =
    bankHolidayResponseRepo.drop()

}

object BankHolidayService {

  private val defaultPredefinedResponse = PredefinedResponse(
    200,
    Some(Json.parse(
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
        |        "date": "999999999-12-31",
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
        |         "date": "999999999-12-31",
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
        |        "date": "999999999-01-01",
        |        "notes": "",
        |        "bunting": true
        |      }
        |    ]
        |  }
        |}
        |""".stripMargin
    ))
  )

}
