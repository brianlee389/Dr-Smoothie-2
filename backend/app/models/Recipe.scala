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

case class Recipe(id: Option[BSONObjectID], name: String, user: User)

object Recipe {
  /** serialize/deserialize a Recipe into/from JSON value */
 implicit val RecipeFormat = Json.format[Recipe]

 implicit object RecipeBSONWriter extends BSONDocumentWriter[Recipe] {
    def write(recipe: Recipe): BSONDocument =
      BSONDocument(
        "_id" -> recipe.id.getOrElse(BSONObjectID.generate),
        "name" -> recipe.name,
        "user" -> recipe.user
      )
  }

  /** deserialize a Recipe from a BSON */
  implicit object RecipeBSONReader extends BSONDocumentReader[Recipe] {
    def read(doc: BSONDocument): Recipe =
      Recipe(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("name").get,
        doc.getAs[User]("user").get
      )
  }
}