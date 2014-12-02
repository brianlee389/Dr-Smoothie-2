package models

import play.api.libs.json.Json
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.Producer.nameValue2Producer
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat

/*
 * That Dev Guy: Brian Lee
 */

case class Nutrient(id: Option[BSONObjectID], name: String, group: Int)

object Nutrient {
  /** serialize/deserialize a Nutrient into/from JSON value */
  implicit val NutrientFormat = Json.format[Nutrient]

 implicit object NutrientBSONWriter extends BSONDocumentWriter[Nutrient] {
    def write(nutr: Nutrient): BSONDocument =
      BSONDocument(
        "_id" -> nutr.id.getOrElse(BSONObjectID.generate),
        "name" -> nutr.name,
        "group" -> nutr.group
      )
  }

  /** deserialize a Nutrient from a BSON */
  implicit object NutrientBSONReader extends BSONDocumentReader[Nutrient] {
    def read(doc: BSONDocument): Nutrient =
      Nutrient(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("name").get,
        doc.getAs[Int]("group").get
      )
  }
}