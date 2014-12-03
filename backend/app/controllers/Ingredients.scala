package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import models.Ingredient
import models.Ingredient.IngredientFormat
import models.Ingredient.IngredientBSONReader
import models.Ingredient.IngredientBSONWriter
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

object Ingredients extends Controller with MongoController {
  val collection = db[BSONCollection]("ingredients")

  /** list all ingredients */
  def index = Action { implicit request =>
    Async {
      val cursor = collection.find(
        BSONDocument()).cursor[Ingredient] // get all the fields of all the ingredients
      val futureList = cursor.toList // convert it to a list of Ingredient
      futureList.map { ingredients => Ok(Json.toJson(ingredients)) } // convert it to a JSON and return it
    }
  }
  
  /** create a ingredient from the given JSON */
  def create() = Action(parse.json) { request =>
    Async {
      //val nameJSON = request.body.\("name")
      val name: String = request.body.\("name").as[String]
      val foodgroup:Int = request.body.\("foodgroup").as[Int]

      // create the Ingredient
      val createdIngr = Ingredient(Option(BSONObjectID.generate), name, foodgroup)
      collection.insert(createdIngr).map(
        _ => Ok(Json.toJson(createdIngr))) 
        // return the created ingredient in a JSON
    }
  }
/*  
  /** retrieve the ingredient for the given id as JSON */
  def show(id: String) = Action(parse.empty) { request =>
    Async {
      // get the corresponding BSONObjectID
      val objectID = new BSONObjectID(id) 
      // get the ingredient having this id (there will be 0 or 1 result)
      val futureIngredient = collection.find(BSONDocument("_id" -> objectID)).one[Ingredient]
      futureIngredient.map { ingr => Ok(Json.toJson(ingr)) }
    }
  }
  
  /** update the ingredient for the given id from the JSON body */
  def update(id: String) = Action(parse.json) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      //val nameJSON = request.body.\("name")
      val name: String = request.body.\("name").toString()
      //nameFormat.reads(nameJSON).get
      val type: Int = request.body.\("type").toInt()
      val modifier = BSONDocument( // create the modifier ingredient
        "$set" -> BSONDocument(
          "name" -> name,
          "type" -> type
          ))
      // return the modified Ingredient in a JSON
      collection.update(BSONDocument("_id" -> objectID), modifier).map(
        _ => Ok(Json.toJson(Ingredient(Option(objectID), name, type)))) 
    }
  }
  
  /** delete a ingredient for the given id */
  def delete(id: String) = Action(parse.empty) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      collection.remove(BSONDocument("_id" -> objectID)).map( // remove the ingredient
        _ => Ok(Json.obj())).recover { case _ => InternalServerError } // and return an empty JSON while recovering from errors if any
    }
  }*/
}