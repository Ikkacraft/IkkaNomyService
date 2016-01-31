package controllers

import javax.inject.Inject

import models.Account
import play.api.libs.json.Json
import play.api.mvc._
import services.AccountService


//@Api(value = "/accounts", description = "Operations about accounts")
class Accounts @Inject()(accountService: AccountService) extends Controller {

  /*  @ApiOperation(
      nickname = "getAccounts",
      value = "Get all accounts",
      notes = "Returns a list of accounts",
      response = classOf[models.Accounts], httpMethod = "GET")
    @ApiResponses(Array(new ApiResponse(code = 404, message = "Users not found")))*/
  def getAccounts() = Action {
    Ok(Json.toJson(accountService.getAll()))
  }

  def getAccount(account_id: Long) = Action {
    Ok(Json.toJson(accountService.get(account_id)))
  }

  def createAccount() = Action(parse.json) { implicit request =>
    val balance: BigDecimal = (request.body \ "account_balance").asOpt[BigDecimal].getOrElse(0)
    val description: Option[String] = (request.body \ "description").asOpt[String]
    val account = new Account(balance, description)
    val id = accountService.create(account)
    id match {
      case -1 => BadRequest
      case _ => {
        Created(Json.toJson(id))
      }
    }
  }

  def updateAccount(account_id: Long) = Action(parse.json) { implicit request =>
    val balance: BigDecimal = (request.body \ "account_balance").as[BigDecimal]
    val description: Option[String] = (request.body \ "description").asOpt[String]

    val account = new Account(account_id, balance, description)
    val id = accountService.update(account)
    id match {
      case 0 => BadRequest
      case _ => {
        Ok(Json.toJson(id))
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