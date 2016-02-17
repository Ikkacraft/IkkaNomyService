package controllers

import javax.inject.Inject
import io.swagger.annotations.{ApiResponse, ApiResponses, ApiOperation, Api}
import models.Transaction
import play.api.libs.json.Json
import play.api.mvc._
import services.{AccountService, TransactionService}

@Api(value = "/transactions", description = "Operations about transactions between accounts", produces="application/json, application/xml")
class Transactions @Inject()(transactionService: TransactionService, accountService: AccountService) extends Controller {

  @ApiOperation(
    nickname = "getTransactions",
    value = "Get all transactions",
    notes = "Return a list of transactions",
    response = classOf[models.Account], httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Transactions not found"), new ApiResponse(code = 200, message = "Transactions found")))
  def getTransactions() = Action { request =>
    request.accepts("application/json") || request.accepts("text/json") match {
      case true => {
        val transactions: List[Transaction] = transactionService.getAll()
        if (transactions.isEmpty) NotFound("Transactions not found") else Ok(Json.toJson(transactions))
      }
      case false => {
        val transactions: List[Transaction] = transactionService.getAll()
        if (transactions.isEmpty) NotFound("Transactions not found") else Ok(<transactions>{transactionService.getAll().map(a => a.toXml)}</transactions>)
      }
    }
  }

  @ApiOperation(
    nickname = "getTransaction",
    value = "Get one transaction",
    notes = "Return a transaction",
    response = classOf[models.Account], httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Transaction not found"), new ApiResponse(code = 200, message = "Transaction found")))
  def getTransaction(transaction_id: Long) = Action { request =>
    request.accepts("application/json") || request.accepts("text/json") match {
      case true => {
        transactionService.get(transaction_id) match {
          case Some(transaction) => Ok(Json.toJson(transaction))
          case None => NotFound("Transaction not found")
        }
      }
      case false => {
        transactionService.get(transaction_id) match {
          case Some(transaction) => Ok(transaction.toXml)
          case None => NotFound("Transaction not found")
        }
      }
    }
  }

  // TODO: cancel transaction if account cant be credit or debtored
  // TODO: handle negative balance
  @ApiOperation(
    nickname = "createTransaction",
    value = "create one transaction",
    response = classOf[models.Account], httpMethod = "POST")
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Bad Request"), new ApiResponse(code = 200, message = "Transaction has been successfully created")))
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
            accountService.get(account_creditor_id) match {
              case Some(account) => {
                val balance = account.account_balance + amount
                account.setBalance(balance)
                accountService.update(account)
              }
              case None => None
            }
          }
          case None => None
        }

        account_debtor_id match {
          case Some(account_debtor_id) => {
            accountService.get(account_debtor_id) match {
              case Some(account) => {
                val balance = account.account_balance - amount
                account.setBalance(balance)
                accountService.update(account)
              }
              case None => None
            }
          }
          case None => None
        }
        Created("Transaction " + id + " has been successfully created")
      }
    }
  }
}