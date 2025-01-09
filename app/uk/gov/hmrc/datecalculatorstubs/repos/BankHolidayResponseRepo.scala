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

package uk.gov.hmrc.datecalculatorstubs.repos

import com.google.inject.{Inject, Singleton}
import org.mongodb.scala.model.{Filters, ReplaceOptions}
import org.mongodb.scala.SingleObservableFuture
import play.api.libs.json.*
import uk.gov.hmrc.datecalculatorstubs.models.PredefinedResponse
import uk.gov.hmrc.datecalculatorstubs.repos.BankHolidayResponseRepo.BankHolidayResponseRepoDocument
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.{ExecutionContext, Future}

@Singleton @SuppressWarnings(Array("org.wartremover.warts.Any"))
class BankHolidayResponseRepo @Inject() (mongoComponent: MongoComponent)(using ExecutionContext)
    extends PlayMongoRepository[BankHolidayResponseRepoDocument](
      mongoComponent = mongoComponent,
      collectionName = "bank-holiday-responses",
      domainFormat = summon[OFormat[BankHolidayResponseRepoDocument]],
      indexes = Seq.empty,
      replaceIndexes = false
    ) {

  // keep replacing the same document in mongo whenever we insert by using a static id
  private val staticId = "static-id"

  def insert(predefinedResponse: PredefinedResponse): Future[Unit] =
    collection
      .replaceOne(
        filter = Filters.eq("_id", staticId),
        replacement = BankHolidayResponseRepoDocument(staticId, predefinedResponse),
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => ())

  def find(): Future[Option[PredefinedResponse]] =
    collection
      .find(filter = Filters.eq("_id", staticId))
      .headOption()
      .map(_.map(_.predefinedResponse))

  def drop(): Future[Unit] = collection.drop().toFuture().map(_ => ())

}

object BankHolidayResponseRepo {

  final case class BankHolidayResponseRepoDocument(_id: String, predefinedResponse: PredefinedResponse)

  object BankHolidayResponseRepoDocument {

    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    given OFormat[BankHolidayResponseRepoDocument] = Json.format

  }

}
