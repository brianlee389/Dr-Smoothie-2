package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import models.User
import models.User.UserFormat
import models.User.UserBSONReader
import models.User.UserBSONWriter
import models.Recipe
import models.Recipe.RecipeFormat
import models.Recipe.RecipeBSONReader
import models.Recipe.RecipeBSONWriter
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentIdentity
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.Producer.nameValue2Producer

/*
 * That dev guy Brian Lee
 */

object Recipes extends Controller with MongoController {
  // Recipe db collection
  val collection = db[BSONCollection]("recipes")

  /** list all Recipes */
  def index = Action { implicit request =>
    Async {
      val cursor = collection.find(
        BSONDocument()).cursor[Recipe] // get all the fields of all the Users
      val futureList = cursor.toList // convert it to a list of User
      futureList.map { recipes => Ok(Json.toJson(recipes)) } // convert it to a JSON and return it
    }
  }
  
  /** create a recipe from the given JSON */
  /*
  {"name":"sample recipe name", "userid": 123 }
  */
  def create() = Action(parse.json) { request =>
    Async {
      val name: String = request.body.\("name").as[String]
      val user: String = request.body.\("userid").as[String]
      val createdRecipe: Recipe = Recipe(Option(BSONObjectID.generate.stringify), name, user)
      collection.insert(createdRecipe).map(
        _ => Ok(Json.toJson(createdRecipe)))
    }
  }
/*  
  /** retrieve the user for the given id as JSON */
  def show(id: String) = Action(parse.empty) { request =>
    Async {
      // get the corresponding BSONObjectID
      val objectID = new BSONObjectID(id) 
      // get the user having this id (there will be 0 or 1 result)
      val futureuser = collection.find(BSONDocument("_id" -> objectID)).one[User]
      futureUser.map { ingr => Ok(Json.toJson(ingr)) }
    }
  }
  
  /** update the User for the given id from the JSON body */
  def update(id: String) = Action(parse.json) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      //val nameJSON = request.body.\("name")
      val name: String = request.body.\("name").toString()
      //nameFormat.reads(nameJSON).get
      val type: Int = request.body.\("type").toInt()
      val modifier = BSONDocument( // create the modifier User
        "$set" -> BSONDocument(
          "name" -> name,
          "type" -> type
          ))
      // return the modified User in a JSON
      collection.update(BSONDocument("_id" -> objectID), modifier).map(
        _ => Ok(Json.toJson(User(Option(objectID), name, type)))) 
    }
  }
  
  /** delete a User for the given id */
  def delete(id: String) = Action(parse.empty) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      collection.remove(BSONDocument("_id" -> objectID)).map( // remove the User
        _ => Ok(Json.obj())).recover { case _ => InternalServerError } // and return an empty JSON while recovering from errors if any
    }
  }*/
}
