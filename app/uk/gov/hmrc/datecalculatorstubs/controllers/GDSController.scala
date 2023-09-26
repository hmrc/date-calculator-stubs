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

import play.api.Logger
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.datecalculatorstubs.models.PredefinedResponse
import uk.gov.hmrc.datecalculatorstubs.services.BankHolidayService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class GDSController @Inject() (
    bankHolidayService: BankHolidayService,
    cc:                 ControllerComponents
)(implicit ec: ExecutionContext)
  extends BackendController(cc) {

  private val logger: Logger = Logger(this.getClass)

  val putBankHolidays: Action[PredefinedResponse] = Action.async(parse.json[PredefinedResponse]) { request =>
    bankHolidayService.insertPredefinedResponse(request.body).map(_ => NoContent)
  }

  val deleteBankHolidays: Action[AnyContent] = Action.async{ _ =>
    bankHolidayService.deletePredefinedResponse().map(_ => NoContent)
  }

  val getBankHolidays: Action[AnyContent] = Action.async { request =>
    request.headers.get("From").filter(_.nonEmpty) match {
      case None =>
        logger.warn("Could not find a non-empty value in a 'From' header")
        Future.successful(BadRequest("No valid 'From' header in request"))

      case Some(_) =>
        bankHolidayService.getPredefinedResponse().map { response =>
          response.body.fold[Result](Status(response.status))(body =>
            Status(response.status)(body))
        }
    }
  }

}
