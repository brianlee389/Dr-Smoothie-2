package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import concurrent.Future
import concurrent.Await
import scala.util.{Try, Success, Failure}
import scala.concurrent.duration._

import models.Ingredient
import models.Ingredient.IngredientFormat
import models.Ingredient.IngredientBSONReader
import models.Ingredient.IngredientBSONWriter
import models.Recipe
import models.Recipe.RecipeFormat
import models.Recipe.RecipeBSONReader
import models.Recipe.RecipeBSONWriter
import models.User
import models.User.UserFormat
import models.User.UserBSONReader
import models.User.UserBSONWriter
import models.RecipeIngredientMap
import models.RecipeIngredientMap.RecipeIngredientMapFormat
import models.RecipeIngredientMap.RecipeIngredientMapBSONReader
import models.RecipeIngredientMap.RecipeIngredientMapBSONWriter
import models.Recommendation
import models.Recommendation.RecommendationFormat
import models.Recommendation.RecommendationBSONReader
import models.Recommendation.RecommendationBSONWriter

import play.api._
import play.api.libs.json._
//import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api
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
  val IngrCollection = db[BSONCollection]("ingredients")
  val RecipeCollection = db[BSONCollection]("recipes")
  val RecipeIngrCollection = db[BSONCollection]("rec-ingr")
  val RecommendationCollection = db[BSONCollection]("recommendations")
  val SIXTYSECONDS = Duration(60000, "millis")
  
  /** list all Recipes 
    Returns the json in the form of:
    [{ "_id": "sample user id", 
      "name": "sample name", 
      "user": "sample userid"
    }, ...]

  */
  def index = Action { implicit request =>
    Async {
      val cursor = RecipeCollection.find(
        BSONDocument()).cursor[Recipe] // get all the fields of all the Users
      
      val futureList = cursor.toList // convert it to a list of User
      // recipes is the list of recipes contained in the future
      futureList.map { recipes => Ok(Json.toJson(recipes)) } // convert it to a JSON and return it
    }
  }

  def optionsRequestWithParam(id: String) = Action {
    Ok("...").withHeaders(
      ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
      ACCESS_CONTROL_ALLOW_METHODS -> "POST",
      ACCESS_CONTROL_MAX_AGE -> "300",
      ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent")
  }

  /** list all RecipeIngredient Mappings 
    returns json in the format:
    [{"recipeid": "ad12", ingredientid:"ss22"}, ...]
  */
  def mappings = Action { implicit request =>
    Async {
      val cursor = RecipeIngrCollection.find(
        BSONDocument()).cursor[RecipeIngredientMap]
      val futureList = cursor.toList
      futureList.map { mapping => Ok(Json.toJson(mapping)) }
    }
  }

  /** list all RecipeIngredient Mappings with the recipe with it's id equal to the parameter
    returns json in the format:
    [{"recipeid": "ad12", ingredientid:"ss22"}, ...]
  */
  def recipeMappings(id: String) = Action { implicit request =>
    Async {
      val cursor = RecipeIngrCollection.find(
        BSONDocument("recipeid" -> id)).cursor[RecipeIngredientMap]
      
      val futureList = cursor.toList
      futureList.map { mapping => Ok(Json.toJson(mapping)) }
    }
  }

  /** create a recipe from the given JSON */
  /*
  This isn't being used
  {"name":"sample recipe name", "userid": "abs123" }
  */
/*  def create() = Action(parse.json) { request =>
    Async {
      val name: String = request.body.\("name").as[String]
      val user: String = request.body.\("userid").as[String]
      val createdRecipe: Recipe = 
          Recipe(Option(BSONObjectID.generate.stringify), name, user)
      
      RecipeCollection.insert(createdRecipe).map(
        _ => Ok(Json.toJson(createdRecipe)))
    }
  }
*/
  /* create a recipe 
  Takes in the json value
    {"name":"sample recipe name", "userid": "abc123", 
    "ingredients" : ["ab12", "qw111"] }
  */
  def createRecipe() = Action(parse.json) { request =>
    Async {
      val name: String = request.body.\("name").as[String]
      val user: String = request.body.\("userid").as[String] 
      val ingrs:List[String] = request.body.\("ingredients").validate[List[String]] match {
        case JsSuccess(ingridlist, _) => ingridlist 
        case JsError(_) => List[String]()
      }
        
      // add recipe and return the json
      if(!ingrs.isEmpty)
      {
        // create and add recipe
        val newRecipeId: String = BSONObjectID.generate.stringify; 
        val createdRecipe: Recipe = 
            Recipe(Option(newRecipeId), name, user)
        RecipeCollection.insert(createdRecipe)
        
        // retrieve ingredients
        val ingrListQuery = BSONDocument("_id" -> BSONDocument("$in" -> ingrs))
        val futureIngrList = IngrCollection.find(ingrListQuery)
                            .cursor[Ingredient].toList

        // Await for the FUTURE
        val ingrList:List[Ingredient] = Await.result(futureIngrList, SIXTYSECONDS) 
        
        // insert the created mappings
        val createdMappings = ingrList.foreach(ingr => {
          val rim = RecipeIngredientMap(Option(BSONObjectID.generate.stringify), newRecipeId, ingr.id.get)
          RecipeIngrCollection.insert(rim)
        })
        Future { Ok(Json.obj("success" -> 1)) }
      }
      else {
        Future {
          Ok(Json.obj("success" -> 0, "message" -> "Recipe was not created"))
        }
      }
    } //Async is done
  }

  /*
    Returns a recipe and a list of ingredients in the recipe
    The Json Format it is returned in is:
    { "recipe": {"_id": "sample user id", 
      "name": "sample name", 
      "user": "sample userid"
      },
      "ingredients": [{"name":"sample name", "foodgroup": 123 }, ...]
    }
  */
  def getRecipe(id: String) = Action { implicit request =>
    Async {
      val recipeIdQuery = BSONDocument("recipeid" -> id)
      val ingrsOfRecipe = RecipeIngrCollection.find(recipeIdQuery)
        .cursor[RecipeIngredientMap]

      val recipeQuery = BSONDocument("_id" -> id)
      val futureRecipe = RecipeCollection.find(recipeQuery).cursor[Recipe].toList
      val foundRecipe = Await.result(futureRecipe, SIXTYSECONDS)
      // retrieving the ingredients 
      val futureList:Future[List[RecipeIngredientMap]] = 
        ingrsOfRecipe.toList()

      // The futureList contains a list of RecipeIngredientMap
      futureList.map(mappingsList => {
        val ingrs = mappingsList.map(rim => rim.ingredientId)
        val ingrListQuery = BSONDocument("_id" -> BSONDocument("$in" -> ingrs))   
        val futureIngrs = IngrCollection.find(ingrListQuery).cursor[Ingredient].toList
        val ingrsJson = Await.result(futureIngrs, SIXTYSECONDS)
                        .map(ing => Json.toJson(ing))

        // return future of recipe and it's list of ingredients
        Ok(Json.toJson(
          Map("recipe" -> Json.toJson(foundRecipe),
              "ingredients" -> Json.toJson(ingrsJson))
        ))//Finish Ok
      })
    }
  }

  // id has to be a recipe id
  // add ingredient to a recipe
  def AddIngredient() = Action(parse.json) { request =>
    Async {
      val recipeid: String = request.body.\("recipeid").as[String]
      val ingrId: String = request.body.\("ingredientid").as[String]
      
      val ingrQuery = BSONDocument("_id" -> ingrId)
      val retrievedIngr = IngrCollection.find(ingrQuery)
        .cursor[Ingredient]
      
      if(!retrievedIngr.hasNext) {
          val returnMessage = "The Ingredient for the given ingredient Id doesn''t exist.";
          Future {
            Ok(Json.toJson(Map("message" -> returnMessage)))
          }          
      }
      else {
        retrievedIngr.toList(1).map(ingr => {
          val addRecIngrMap = 
          RecipeIngredientMap(Option(BSONObjectID.generate.stringify), recipeid, ingr.head.id.get)
          RecipeIngrCollection.insert(addRecIngrMap);
          Ok(Json.toJson(addRecIngrMap))
        })
      }
    }
  }

  // retrieves the number of rccommendation for a recipe
  def recommendCount(id: String) = Action {
    Async {
      val recipeIdQuery = BSONDocument("recipeid" -> id)
      val foundRecommendations = RecipeIngrCollection.find(recipeIdQuery)
          .cursor[RecipeIngredientMap].toList
      foundRecommendations.map(recs => {
        // // retrieve the count
        // val numRecommendations = Await.result(foundRecommendations, SIXTYSECONDS).count()
        
        // return the number of recommendations
        Ok(Json.toJson(
          Map("count" -> recs.count(_ => true))
        ))//Ok
      }) 
    }
  }

  /** delete a Recipe for the given id */
  def delete(id: String) = Action(parse.empty) { request =>
    Async {
      val recipeBSON = BSONDocument("_id" -> id)
      RecipeIngrCollection.remove(BSONDocument("recipeid" -> id))
      
      RecipeCollection.remove(recipeBSON).map(
        _ => Ok(Json.obj())).recover { case _ => InternalServerError } 
        // and return an empty JSON while recovering from errors if any
    }
  }

  /** delete a RecipeIngredientMapping for the given id */
  def removeIngredient(id: String, ingredientId: String) = Action(parse.empty) { request =>
    Async {
      RecipeIngrCollection.remove(BSONDocument("recipeid" -> id, "ingredientid" ->ingredientId))
        .map(
        _ => Ok(Json.obj())).recover { case _ => InternalServerError } 
    }
  }

  /* Adds a recommendation of a recipe by a user, at most one recommendation per user*/
  def recommendRecipe(rid:String, userid: String) = Action(parse.empty) { request =>
    Async {
      val r = RecommendationCollection.find(BSONDocument("recipeid" -> rid, "userid" -> userid))
        .cursor[Recommendation].toList
      val reclist:List[Recommendation] = Await.result(r, SIXTYSECONDS)
      if(reclist.isEmpty) {
        val rec = Recommendation(Option(BSONObjectID.generate.stringify), rid, userid)
        RecommendationCollection.insert(rec)
         Future { Ok(Json.obj("success" -> 1, "existed" -> "false")) }
      } else {
        Future { Ok(Json.obj("success" -> 1, "existed" -> "true")) }
      }
    }
  }
}
