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
 * Database Mappings for NoSQL MongoDB

 There seemed a lot of room for using abstraction and inheritance
 but this was the first time we used Scala and we didn't want to 
 mess things up trying to implement better design patterns and abstractions.
 */

case class RecipeIngredientMap(id: Option[String], recipeId: String, ingredientId: String)
case class IngrNutrMap(id: Option[String], ingredientId: String, nutrientId: String)
case class Recommendation(id: Option[String], recipeId: String, userId: String)

object RecipeIngredientMap {
  /** serialize/deserialize a RecipeIngredientMap into/from JSON value */
  implicit val RecipeIngredientMapFormat = Json.format[RecipeIngredientMap]

  implicit object RecipeIngredientMapBSONWriter extends BSONDocumentWriter[RecipeIngredientMap] {
    def write(rim: RecipeIngredientMap): BSONDocument =
      BSONDocument(
        "_id" -> rim.id.getOrElse(BSONObjectID.generate.stringify),
        "recipeid" -> rim.recipeId,
        "ingredientid" -> rim.ingredientId
      )
  }

  /** deserialize a RecipeIngredientMap from a BSON */
  implicit object RecipeIngredientMapBSONReader extends BSONDocumentReader[RecipeIngredientMap] {
    def read(doc: BSONDocument): RecipeIngredientMap =
      RecipeIngredientMap(
        doc.getAs[String]("_id"),
        doc.getAs[String]("recipeid").get,
        doc.getAs[String]("ingredientid").get
      )
  }
}

object IngrNutrMap {
  /** serialize/deserialize a IngrNutrMap into/from JSON value */
  implicit val IngrNutrMapFormat = Json.format[IngrNutrMap]

  implicit object IngrNutrMapBSONWriter extends BSONDocumentWriter[IngrNutrMap] {
    def write(nim: IngrNutrMap): BSONDocument =
      BSONDocument(
        "_id" -> nim.id.getOrElse(BSONObjectID.generate.stringify),
        "ingredientid" -> nim.ingredientId,
        "nutrientid" -> nim.nutrientId
      )
  }

  /** deserialize a IngrNutrMap from a BSON */
  implicit object IngrNutrMapBSONReader extends BSONDocumentReader[IngrNutrMap] {
    def read(doc: BSONDocument): IngrNutrMap =
      IngrNutrMap(
        doc.getAs[String]("_id"),
        doc.getAs[String]("ingredientid").get,
        doc.getAs[String]("nutrientid").get
      )
  }
}


object Recommendation {
  /** serialize/deserialize a Recommendation into/from JSON value */
  implicit val RecommendationFormat = Json.format[Recommendation]

  implicit object RecommendationBSONWriter extends BSONDocumentWriter[Recommendation] {
    def write(r: Recommendation): BSONDocument =
      BSONDocument(
        "_id" -> r.id.getOrElse(BSONObjectID.generate.stringify),
        "recipeid" -> r.recipeId,
        "userid" -> r.userId
      )
  }

  /** deserialize a Recommendation from a BSON */
  implicit object RecommendationBSONReader extends BSONDocumentReader[Recommendation] {
    def read(doc: BSONDocument): Recommendation =
      Recommendation(
        doc.getAs[String]("_id"),
        doc.getAs[String]("recipeid").get,
        doc.getAs[String]("userid").get
      )
  }
}