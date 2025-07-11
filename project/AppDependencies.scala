import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.13.0"
  private val hmrcMongoVersion = "2.6.0"

  val compile: Seq[ModuleID] = Seq(
  // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % hmrcMongoVersion
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
  // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoVersion
  // format: ON
  ).map(_ % Test)

  // only add additional dependencies here - it test inherit test dependencies above already
  val itDependencies: Seq[ModuleID] = Seq()

}
