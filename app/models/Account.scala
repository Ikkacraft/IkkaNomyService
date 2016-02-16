package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, Writes}

/**
 * Contains data related to an account
 *
 * @param account_id      Account id
 * @param account_balance Account Balance
 * @param description     Description of the account
 */
case class Account(account_id: Long, var account_balance: BigDecimal, description: Option[String]) {
  def this(account_balance: BigDecimal, description: Option[String]) = this(0, account_balance, description)

  def toXml = {
    <account>
      <account_id>{account_id}</account_id>
      <account_balance>{account_balance}</account_balance>
      {if (description.isDefined) <description>{description.get}</description> else <description />}
    </account>
  }

  def setBalance(p: BigDecimal) { account_balance = p }
}

object Account {
  implicit val jsonWrites: Writes[Account] = Json.writes[Account]

  val parser: RowParser[Account] = {
      get[Long]("ID") ~
      get[BigDecimal]("ACCOUNT_BALANCE") ~
      get[Option[String]]("DESCRIPTION") map {
      case account_id ~ account_balance ~ description =>
        Account(account_id, account_balance, description)
    }
  }
}
