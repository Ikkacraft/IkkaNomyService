package services

import anorm._
import models.Transaction
import play.api.Play._
import play.api.db._


class TransactionService {

  def getAll() = {
    val results: List[Transaction] = DB.withConnection { implicit c =>
      SQL( """SELECT * FROM TRANSACTION """).as(Transaction.parser.*)
    }
    results
  }

  def get(transaction_id: Long): Transaction = {
    val result: Transaction = DB.withConnection { implicit c =>
      SQL( """SELECT * FROM TRANSACTION WHERE  ID = {transaction_id}""")
        .on('transaction_id -> transaction_id).as(Transaction.parser.single)
    }
    result
  }

  def create(transaction: Transaction): Long = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("INSERT INTO TRANSACTION(ID_ACCOUNT_CREDITOR, ID_ACCOUNT_DEBTOR, AMOUNT, DESCRIPTION) VALUES({account_receive}, {account_send}, {amount}, {description})")
        .on('account_receive -> transaction.account_receive, 'account_send -> transaction.account_send, 'amount -> transaction.amount, 'description -> transaction.description).executeInsert()
    }
    id.getOrElse(-1)
  }
}
