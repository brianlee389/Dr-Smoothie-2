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

case class Ingredient(id: Option[BSONObjectID], name: String, foodgroup: Int)

object Ingredient {
  /** serialize/deserialize a Ingredient into/from JSON value */
  implicit val IngredientFormat = Json.format[Ingredient]

  implicit object IngredientBSONWriter extends BSONDocumentWriter[Ingredient] {
    def write(ingr: Ingredient): BSONDocument =
      BSONDocument(
        "_id" -> ingr.id.getOrElse(BSONObjectID.generate),
        "name" -> ingr.name,
        "foodgroup" -> ingr.foodgroup
      )
  }

  /** deserialize a Ingredient from a BSON */
  implicit object IngredientBSONReader extends BSONDocumentReader[Ingredient] {
    def read(doc: BSONDocument): Ingredient =
      
      Ingredient(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("name").get,
        doc.getAs[Double]("foodgroup").get.toInt
      )
  }
}