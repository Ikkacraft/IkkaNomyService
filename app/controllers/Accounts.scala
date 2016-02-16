package controllers

import javax.inject.Inject

import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import models.{Transaction, Account}
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
  def getAccounts() = Action { request =>
    request.accepts("application/json") || request.accepts("text/json") match {
      case true =>{
        val accounts: List[Account] = accountService.getAll()
        if (accounts.isEmpty) NoContent else Ok(Json.toJson(accounts))
      }
      case false => {
        val accounts: List[Account] = accountService.getAll()
        if (accounts.isEmpty) NoContent else Ok(<accounts>{accountService.getAll().map(a => a.toXml)}</accounts>)
      }
    }
  }

  @ApiOperation(
    nickname = "getAccount",
    value = "Get one specific account",
    notes = "Return a specific account",
    response = classOf[models.Account], httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "User not found"), new ApiResponse(code = 200, message = "User found")))
  def getAccount(account_id: Long) = Action { request =>
    request.accepts("application/json") || request.accepts("text/json") match {
      case true => {
        accountService.get(account_id) match {
          case Some(account) => Ok(Json.toJson(account))
          case None => NoContent
        }
      }
      case false => {
        accountService.get(account_id) match {
          case Some(account) => Ok(account.toXml)
          case None => NoContent
        }
      }
    }
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
      case _  => Created("Account " + id + " has been successfully")
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
      case _ => Ok("The account " + account_id + " has been successfully updated")
    }
  }

  def deleteAccount(account_id: Long) = Action {
    accountService.delete(account_id)
    Ok("Account " + account_id + "has been successfully deleted")
  }

  def getTransactionsByAccount(account_id: Long) = Action { request =>
    request.accepts("application/json") || request.accepts("text/json") match {
      case true => {
        val transactions: List[Transaction] = accountService.getTransactionsByAccount(account_id)
        if (transactions.isEmpty) NoContent else Ok(Json.toJson(transactions))
      }
      case false => {
        val transactions: List[Transaction] = accountService.getTransactionsByAccount(account_id)
        if (transactions.isEmpty) NoContent else Ok(<transactions>{accountService.getTransactionsByAccount(account_id).map(a => a.toXml)}</transactions>)
      }
    }
  }
}