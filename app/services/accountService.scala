package services

import anorm._
import models.{Account, Transaction}
import play.api.Play.current
import play.api.db._


class AccountService {

  def getAll() = {
    val results: List[Account] = DB.withConnection { implicit c =>
      SQL( """SELECT * FROM ACCOUNT """).as(Account.parser.*)
    }
    results
  }

  def get(account_id: Long): Option[Account] = {
    val result: Option[Account] = DB.withConnection { implicit c =>
      SQL( """SELECT * FROM ACCOUNT WHERE ID = {account_id}""").on('account_id -> account_id).as(Account.parser.singleOpt)
    }
    result
  }

  def getTransactionsByAccount(account_id: Long) = {
    val results: List[Transaction] = DB.withConnection { implicit c =>
      SQL( """SELECT * FROM TRANSACTION WHERE ID_ACCOUNT_CREDITOR = {account_id} OR ID_ACCOUNT_DEBTOR = {account_id}""")
        .on('account_id -> account_id).as(Transaction.parser.*)
    }
    results
  }

  def create(account: Account): Long = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("INSERT INTO ACCOUNT(account_balance, description) VALUES({account_balance}, {description})")
        .on('account_balance -> account.account_balance, 'description -> account.description).executeInsert()
    }
    id.getOrElse(-1)
  }

  def update(account: Account): Int = {
    val id: Int = DB.withConnection { implicit c =>
      SQL("UPDATE ACCOUNT SET account_balance = {account_balance}, description = {description} WHERE ID = {account_id}")
        .on('account_balance -> account.account_balance, 'description -> account.description, 'account_id -> account.account_id).executeUpdate()
    }
    id
  }

  def delete(account_id: Long): Int = {
    val id: Int = DB.withConnection { implicit c =>
      SQL("DELETE FROM ACCOUNT WHERE ID = {account_id}").on('account_id -> account_id).executeUpdate()
    }
    id
  }
}
