package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import models.Nutrient
import models.Nutrient.NutrientFormat
import models.Nutrient.NutrientBSONReader
import models.Nutrient.NutrientBSONWriter
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

object Nutrients extends Controller with MongoController {
  val collection = db[BSONCollection]("nutrients")

  /** list all nutrients */
  def index = Action { implicit request =>
    Async {
      val cursor = collection.find(
        BSONDocument()).cursor[Nutrient] // get all the fields of all the nutrients
      val futureList = cursor.toList // convert it to a list of nutrient
      futureList.map { nutrients => Ok(Json.toJson(nutrients)) } // convert it to a JSON and return it
    }
  }
  
  /** create a nutrient from the given JSON */
  /*
  sample json
  
  { "name": "sample name", "group": 123 }

  */
  def create() = Action(parse.json) { request =>
    Async {
      val name: String = request.body.\("name").as[String]
      val group: Int = request.body.\("group").as[Int]
      // create the nutrient
      val createdIngr: Nutrient = 
      Nutrient(Option(BSONObjectID.generate.stringify), name, group) 

      // add to database
      collection.insert(createdIngr).map(
        _ => Ok(Json.toJson(createdIngr))) 
    }
  }
/*  
  /** retrieve the nutrient for the given id as JSON */
  def show(id: String) = Action(parse.empty) { request =>
    Async {
      // get the corresponding BSONObjectID
      val objectID = new BSONObjectID(id) 
      // get the nutrient having this id (there will be 0 or 1 result)
      val futurenutrient = collection.find(BSONDocument("_id" -> objectID)).one[nutrient]
      futurenutrient.map { ingr => Ok(Json.toJson(ingr)) }
    }
  }
  
  /** update the nutrient for the given id from the JSON body */
  def update(id: String) = Action(parse.json) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      //val nameJSON = request.body.\("name")
      val name: String = request.body.\("name").toString()
      //nameFormat.reads(nameJSON).get
      val type: Int = request.body.\("type").toInt()
      val modifier = BSONDocument( // create the modifier nutrient
        "$set" -> BSONDocument(
          "name" -> name,
          "type" -> type
          ))
      // return the modified nutrient in a JSON
      collection.update(BSONDocument("_id" -> objectID), modifier).map(
        _ => Ok(Json.toJson(Nutrient(Option(objectID), name, type)))) 
    }
  }
  
  /** delete a nutrient for the given id */
  def delete(id: String) = Action(parse.empty) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      collection.remove(BSONDocument("_id" -> objectID)).map( // remove the nutrient
        _ => Ok(Json.obj())).recover { case _ => InternalServerError } // and return an empty JSON while recovering from errors if any
    }
  }*/
}
