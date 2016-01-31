package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, Writes}

/**
 * Contains data related to a transaction
 * @param transaction_id   Transaction id
 * @param account_receive  Transaction receiver
 * @param account_send     Transaction sender
 * @param amount           amount of the transaction
 * @param description      Description of the transaction
 */
case class Transaction(transaction_id: Long, amount: BigDecimal, account_receive: Option[Long], account_send: Option[Long], description: Option[String]) {
  def this(amount: BigDecimal, account_receive: Option[Long], account_send: Option[Long], description: Option[String])
  = this(0, amount, account_receive = None, account_send = None, description)
}

object Transaction {
  implicit val jsonWrites: Writes[Transaction] = Json.writes[Transaction]

  val parser: RowParser[Transaction] = {
      get[Long]("ID") ~
      get[BigDecimal]("AMOUNT") ~
      get[Option[Long]]("ID_ACCOUNT_CREDITOR") ~
      get[Option[Long]]("ID_ACCOUNT_DEBTOR") ~
      get[Option[String]]("DESCRIPTION") map {
      case transaction_id ~  amount ~ account_receive ~ account_send ~ description =>
        Transaction(transaction_id, amount, account_receive, account_send, description)
    }
  }
}
