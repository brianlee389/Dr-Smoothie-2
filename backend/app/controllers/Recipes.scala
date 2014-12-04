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
import play.api._
import play.api.libs.json._
import play.api.libs.json.Json
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
  val ingrCollection = db[BSONCollection]("ingredients")
  val recipeCollection = db[BSONCollection]("recipes")
  val recipeIngrCollection = db[BSONCollection]("recipeingredientmappings")
  val SIXTYSECONDS = Duration(60000, "millis")
  
  /** list all Recipes */
  def index = Action { implicit request =>
    Async {
      val cursor = recipeCollection.find(
        BSONDocument()).cursor[Recipe] // get all the fields of all the Users
      
      val futureList = cursor.toList // convert it to a list of User
      // recipes is the list of recipes contained in the future
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
      val createdRecipe: Recipe = 
          Recipe(Option(BSONObjectID.generate.stringify), name, user)
      
      recipeCollection.insert(createdRecipe).map(
        _ => Ok(Json.toJson(createdRecipe)))
    }
  }

  def createRecipe() = Action(parse.json) { request =>
    Async {
      val name: String = request.body.\("name").as[String]
      val user: String = request.body.\("userid").as[String] 
      val ingrs:List[String] = request.body.\("ingredients").validate[List[String]] match {
        case JsSuccess(ingridlist, p) => ingridlist 
        case JsError(_) => List[String]()
      }
        
      // add recipe and return the json
      if(!ingrs.isEmpty)
      {
        // create and add recipe
        val newRecipeId: String = BSONObjectID.generate.stringify; 
        val createdRecipe: Recipe = 
            Recipe(Option(newRecipeId), name, user)
        recipeCollection.insert(createdRecipe)
        
        // retrieve ingredients
        val ingrListQuery = BSONDocument("_id" -> BSONDocument("$in" -> ingrs))
        val futureIngrList = ingrCollection.find(ingrListQuery)
                            .cursor[Ingredient].toList

        // Await for the FUTURE
        val ingrList:List[Ingredient] = Await.result(futureIngrList, SIXTYSECONDS) 
        
        // insert the created mappings
        val createdMappings = ingrList.foreach(ingr => {
          val rim = RecipeIngredientMap(Option(BSONObjectID.generate.stringify), newRecipeId, ingr)
          recipeIngrCollection.insert(rim)
        })
        Future { Ok(Json.obj("success" -> 1)) }
      }
      else {
        Future {
          Ok(Json.obj("success" -> 0, "message" -> "Recipe was not created"))
        }
      }
    } //Async
  }

  def getRecipe(id: String) = Action { implicit request =>
    Async {
      val recipeIdQuery = BSONDocument("recipeid" -> id)
      val ingrsOfRecipe = recipeIngrCollection.find(recipeIdQuery)
        .cursor[RecipeIngredientMap]

      val recipeQuery = BSONDocument("_id" -> id)
      val futureRecipe = recipeCollection.find(recipeQuery).cursor[Recipe].toList
      val foundRecipe = Await.result(futureRecipe, SIXTYSECONDS)
      // retrieving the ingredients 
      val futureList:Future[List[RecipeIngredientMap]] = 
        ingrsOfRecipe.toList()

      // The futureList contains a list of RecipeIngredientMap
      futureList.map(mappingsList => {
        val ingrs = mappingsList.map(rim => rim.ingredient)

        // return future of recipe and it's list of ingredients
        Ok(Json.toJson(
          Map("recipe" -> Json.toJson(foundRecipe),
              "ingredients" -> Json.toJson(ingrs.map(ing => Json.toJson(ing))))
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
      val retrievedIngr = ingrCollection.find(ingrQuery)
        .cursor[Ingredient]//.toList(1)
      
      if(!retrievedIngr.hasNext) {
          val returnMessage = "The Ingredient for the given ingredient Id doesn''t exist.";
          Future {
            Ok(Json.toJson(Map("message" -> returnMessage)))
          }          
      }
      else {
        retrievedIngr.toList(1).map(ingr => {
          val addRecIngrMap = 
          RecipeIngredientMap(Option(BSONObjectID.generate.stringify), recipeid, ingr.head)
          recipeIngrCollection.insert(addRecIngrMap);
          Ok(Json.toJson(addRecIngrMap))
        })
      }
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
