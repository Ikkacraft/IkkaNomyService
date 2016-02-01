package controllers

import javax.inject.Inject

import io.swagger.annotations.Api
import models.Transaction
import play.api.libs.json.Json
import play.api.mvc._
import services.{AccountService, TransactionService}

@Api(value = "/transactions", description = "Operations about transactions between accounts")
class Transactions @Inject()(transactionService: TransactionService, accountService: AccountService) extends Controller {
  def getTransactions() = Action {
    Ok(Json.toJson(transactionService.getAll()))
  }

  def getTransaction(transaction_id: Long) = Action {
    Ok(Json.toJson(transactionService.get(transaction_id)))
  }

  def createTransaction() = Action(parse.json) { implicit request =>
    val amount: BigDecimal = (request.body \ "amount").as[BigDecimal]
    val account_creditor_id: Option[Long] = (request.body \ "account_creditor").asOpt[Long]
    val account_debtor_id: Option[Long] = (request.body \ "account_debtor").asOpt[Long]
    val description: Option[String] = (request.body \ "description").asOpt[String]

    val transaction = new Transaction(amount, account_creditor_id, account_debtor_id, description)

    transactionService.create(transaction) match {
      case -1 => BadRequest
      case id => {
        account_creditor_id match {
          case Some(account_creditor_id) => {
            val account = accountService.get(account_creditor_id)
            val balance = account.account_balance + amount
            account.setBalance(balance)
            accountService.update(account)
          }
          case None => None
        }

        account_debtor_id match {
          case Some(account_debtor_id) => {
            val account = accountService.get(account_debtor_id)
            val balance = account.account_balance - amount
            account.setBalance(balance)
            accountService.update(account)
          }
          case None => None
        }

        Created(Json.toJson(id))
      }
    }
  }
}