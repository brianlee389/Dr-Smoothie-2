package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import concurrent.Future
import concurrent.Future
import concurrent.Await
import scala.util.{Try, Success, Failure}
import scala.concurrent.duration._


import models.Ingredient
import models.Ingredient.IngredientFormat
import models.Ingredient.IngredientBSONReader
import models.Ingredient.IngredientBSONWriter
import models.Nutrient
import models.Nutrient.NutrientFormat
import models.Nutrient.NutrientBSONReader
import models.Nutrient.NutrientBSONWriter
import models.IngrNutrMap
import models.IngrNutrMap.IngrNutrMapFormat
import models.IngrNutrMap.IngrNutrMapBSONReader
import models.IngrNutrMap.IngrNutrMapBSONWriter

import play.api._
import play.api.libs.json._
//import play.api.libs.json.Json
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
  val IngrCollection = db[BSONCollection]("ingredients")
  val NutrCollection = db[BSONCollection]("nutrients")
  val IngrNutrMapCollection = db[BSONCollection]("IngrNutrMapping")
  val SIXTYSECONDS = Duration(60000, "millis")
  
  /** list all ingredients in json
  format is:
  [{"name": "samp name","foodgroup": 123 }, ...]
   */
  def index = Action { implicit request =>
    Async {
      val cursor = IngrCollection.find(
        BSONDocument()).cursor[Ingredient] // get all the fields of all the ingredients
      val futureList = cursor.toList // convert it to a list of Ingredient
      futureList.map { ingredients => Ok(Json.toJson(ingredients)) } // convert it to a JSON and return it
    }
  }
  
  /* retrieve single ingredient with id parameter
    jsonform: 
    {
      "ingredient": { //ingredient json},
      "nutrients": [nutrientobject, ...]
    }
  */
  def getIngredient(id: String) = Action {
    Async {
      val ingrCursor = IngrCollection.find(
        BSONDocument("_id" -> id)).cursor[Ingredient]
      val futureList = ingrCursor.toList
      val futureMapping = IngrNutrMapCollection.find(BSONDocument("ingredientid" -> id)).cursor[IngrNutrMap].toList
      
      val nutrIdList = Await.result(futureMapping, SIXTYSECONDS)
                        .map(nim => nim.ingredientId)
      val nutrQuery = BSONDocument("_id" -> BSONDocument("$in" -> nutrIdList))
      val nutrsFuture = IngrNutrMapCollection.find(BSONDocument("ingredientid" -> id)).cursor[IngrNutrMap].toList
      val nutrients = Await.result(nutrsFuture, SIXTYSECONDS)
      
      futureList.map { ingredients => {
        if(!ingredients.isEmpty) {
          Ok(Json.obj("message" -> "Ingredient could not be found")) 
        } else {
          Ok(Json.toJson(Map("ingredient" -> Json.toJson(ingredients.head),
                          "nutrients" -> Json.toJson(nutrients)))
          )
        }
      }}
    }
  }

  /** create a ingredient from the given JSON 
  
  Jsonform :
    {
      "name": "sample name",
      "foodgroup": 123,
      "nutrients": ["nutrientIDs", ...] 
    }
  */
  def create() = Action(parse.json) { request =>
    Async {
      val name: String = request.body.\("name").as[String]
      val foodgroup:Int = request.body.\("foodgroup").as[Int]
      val nutrients:List[String] = request.body.\("nutrients").validate[List[String]] match {
        case JsSuccess(idlist, _) => idlist 
        case JsError(_) => List[String]()
      }
      // create and add ingredient
      // ingredient can also have no nutrients for this application
      val ingredientId = BSONObjectID.generate.stringify;
      val createdIngredient = Ingredient(Option(ingredientId), name, foodgroup)    
      IngrCollection.insert(createdIngredient)

      // add recipe and return the json
      if(!nutrients.isEmpty)
      {
        // retrieve nutrients
        val listQuery = BSONDocument("_id" -> BSONDocument("$in" -> nutrients))
        val futureNutrList = NutrCollection.find(listQuery)
                            .cursor[Nutrient].toList

        // Await for the FUTURE
        val nutrList:List[Nutrient] = Await.result(futureNutrList, SIXTYSECONDS) 
        
        // insert the created mappings
        val createdMappings = nutrList.foreach(nutr => {
          val nim = IngrNutrMap(Option(BSONObjectID.generate.stringify), ingredientId, nutr.id.get)
          IngrNutrMapCollection.insert(nim)
        })
        Future { Ok(Json.obj("success" -> 1)) }
      }
      else {
        Future {
          Ok(Json.obj("success" -> 0, "message" -> "Recipe was not created"))
        }
      }
    }
  }

  // This function adds a ingrnutr mapping for an ingredient
  // id has to be a ingredient id
  def addNutrient() = Action(parse.json) { request =>
    Async {
      val ingredientId: String = request.body.\("ingredientid").as[String]
      val nutrId: String = request.body.\("nutrientid").as[String]
      
      val nutrQuery = BSONDocument("_id" -> nutrId)
      val retrievedNutr = NutrCollection.find(nutrQuery)
        .cursor[Nutrient]//.toList(1)
      
      if(!retrievedNutr.hasNext) {
          val returnMessage = "The Nutrient for the given ingredient Id doesn''t exist.";
          Future {
            Ok(Json.toJson(Map("message" -> returnMessage)))
          }          
      }
      else {
        retrievedNutr.toList(1).map(nutr => {
          val addIngrNutrMap = 
            IngrNutrMap(Option(BSONObjectID.generate.stringify), ingredientId, nutr.head.id.get);
          IngrNutrMapCollection.insert(addIngrNutrMap);
          Ok(Json.toJson(addIngrNutrMap))
        })
      }
    }
  }

  // return all the IngrNutrMappings with its id parameter
  def getIngrNutrMappings(id: String) = Action {
    Async {
      val cursor = IngrNutrMapCollection.find(
        BSONDocument()).cursor[IngrNutrMap] 
      val futureList = cursor.toList

      futureList.map { nim => 
        {
          Ok(Json.toJson(nim)) 
        }
      } // convert it to a JSON and return it
    }
  }

}
