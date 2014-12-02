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

case class RecipeIngredientMap(id: Option[BSONObjectID], recipe: Recipe, ingredient: Ingredient)
case class NutrIngrMap(id: Option[BSONObjectID], ingredient: Ingredient, nutrient: Nutrient, quantity: Double)
case class RecipeRecommendation(id: Option[BSONObjectID], recipe: Recipe, user: User)

object RecipeIngredientMap {
  /** serialize/deserialize a RecipeIngredientMap into/from JSON value */
  implicit val RecipeIngredientMapFormat = Json.format[RecipeIngredientMap]

  implicit object RecipeIngredientMapBSONWriter extends BSONDocumentWriter[RecipeIngredientMap] {
    def write(rim: RecipeIngredientMap): BSONDocument =
      BSONDocument(
        "_id" -> rim.id.getOrElse(BSONObjectID.generate),
        "recipe" -> rim.recipe,
        "ingredient" -> rim.ingredient
      )
  }

  /** deserialize a RecipeIngredientMap from a BSON */
  implicit object RecipeIngredientMapBSONReader extends BSONDocumentReader[RecipeIngredientMap] {
    def read(doc: BSONDocument): RecipeIngredientMap =
      RecipeIngredientMap(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[Recipe]("recipe").get,
        doc.getAs[Ingredient]("ingredient").get
      )
  }
}

object NutrIngrMap {
  /** serialize/deserialize a NutrIngrMap into/from JSON value */
  implicit val NutrIngrMapFormat = Json.format[NutrIngrMap]

  implicit object NutrIngrMapBSONWriter extends BSONDocumentWriter[NutrIngrMap] {
    def write(nim: NutrIngrMap): BSONDocument =
      BSONDocument(
        "_id" -> nim.id.getOrElse(BSONObjectID.generate),
        "nutrient" -> nim.nutrient,
        "ingredient" -> nim.ingredient,
        "quantity" -> nim.quantity
      )
  }

  /** deserialize a NutrIngrMap from a BSON */
  implicit object NutrIngrMapBSONReader extends BSONDocumentReader[NutrIngrMap] {
    def read(doc: BSONDocument): NutrIngrMap =
      NutrIngrMap(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[Ingredient]("ingredient").get,
        doc.getAs[Nutrient]("nutrient").get,
        doc.getAs[Double]("quantity").get
      )
  }
}