package controllers

import javax.inject.Inject

import io.swagger.annotations.{ApiResponse, ApiResponses, ApiOperation, Api}
import models.Account
import play.api.libs.json.Json
import play.api.mvc._
import services.AccountService


@Api(value = "/accounts", description = "Operations about accounts")
class Accounts @Inject()(accountService: AccountService) extends Controller {

  @ApiOperation(
    nickname = "getAccounts",
    value = "Get all accounts",
    notes = "Return a list of accounts",
    response = classOf[models.Account], httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Users not found"), new ApiResponse(code = 200, message = "Users found")))
  def getAccounts() = Action {
    Ok(Json.toJson(accountService.getAll()))
  }

  @ApiOperation(
    nickname = "getAccount",
    value = "Get one specific account",
    notes = "Return a specific account",
    response = classOf[models.Account], httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "User not found"), new ApiResponse(code = 200, message = "User found")))
  def getAccount(account_id: Long) = Action {
    Ok(Json.toJson(accountService.get(account_id)))
  }

  @ApiOperation(
    nickname = "createAccount",
    value = "Create an account",
    response = classOf[models.Account], httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "The account could not be created"),
    new ApiResponse(code = 200, message = "The account has been successfully created")
  ))
  def createAccount() = Action(parse.json) { implicit request =>
    val balance: BigDecimal = (request.body \ "account_balance").asOpt[BigDecimal].getOrElse(0)
    val description: Option[String] = (request.body \ "description").asOpt[String]
    val account = new Account(balance, description)
    val id = accountService.create(account)
    id match {
      case -1 => BadRequest("The account could not be created")
      case _ => {
        Created("The account " + Json.toJson(id) + " has been successfully created")
      }
    }
  }

  @ApiOperation(
    nickname = "updateAccount",
    value = "Update an account",
    response = classOf[models.Account], httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "The account could not be updated"),
    new ApiResponse(code = 200, message = "The account has been successfully updated")
  ))
  def updateAccount(account_id: Long) = Action(parse.json) { implicit request =>
    val balance: BigDecimal = (request.body \ "account_balance").as[BigDecimal]
    val description: Option[String] = (request.body \ "description").asOpt[String]

    val account = new Account(account_id, balance, description)
    val id = accountService.update(account)
    id match {
      case 0 => BadRequest("The account " + account_id + " could not be updated")
      case _ => {
        Ok("The account " + Json.toJson(id) + " has been successfully updated")
      }
    }
  }

  def deleteAccount(account_id: Long) = Action {
    Ok(Json.toJson(accountService.delete(account_id)))
  }

  def getTransactionsByAccount(account_id: Long) = Action {
    Ok(Json.toJson(accountService.getTransactionsByAccount(account_id)))
  }
}