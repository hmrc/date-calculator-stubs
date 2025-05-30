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

package uk.gov.hmrc.datecalculatorstubs.controllers.testsupport

import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultTestServerFactory, TestServerFactory}
import play.api.{Application, Mode}
import play.core.server.ServerConfig
import uk.gov.hmrc.http.test.WireMockSupport
import uk.gov.hmrc.mongo.test.CleanMongoCollectionSupport

trait ItSpec extends AnyFreeSpecLike, Matchers, GuiceOneServerPerSuite, WireMockSupport, CleanMongoCollectionSupport {
  self =>

  val testServerPort: Int = 19001

  def conf: Map[String, Any] = Map(
    "mongodb.uri"            -> mongoUri,
    "auditing.enabled"       -> false,
    "auditing.traceRequests" -> false
  )

  // in tests use `app`
  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .configure(conf)
    .build()

  object CustomTestServerFactory extends DefaultTestServerFactory {
    override protected def serverConfig(app: Application): ServerConfig = {
      val sc = ServerConfig(port = Some(testServerPort), sslPort = None, mode = Mode.Test, rootDir = app.path)
      sc.copy(configuration = sc.configuration.withFallback(overrideServerConfiguration(app)))
    }
  }

  override protected def testServerFactory: TestServerFactory = CustomTestServerFactory

}
