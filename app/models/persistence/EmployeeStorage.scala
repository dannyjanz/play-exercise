package models.persistence

import javax.inject.{Inject, Singleton}
import slick.jdbc.JdbcProfile
import models.Employee
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class EmployeeStorage @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employees") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("first_name")

    def lastName = column[String]("last_name")

    def jobTitle = column[String]("job_title")

    override def * = (id.?, firstName, lastName, jobTitle) <> ((Employee.apply _).tupled, Employee.unapply)
  }

  private val employees = TableQuery[EmployeeTable]
  private val insert = employees returning employees.map(_.id) into ((employee, id) => employee.copy(id = Some(id)))

  def list(): Future[Seq[Employee]] = db.run {
    employees.result
  }

  def byId(id: Long): Future[Option[Employee]] = db.run {
    employees.filter(employee => employee.id === id).result.headOption
  }

  def store(employee: Employee): Future[Employee] = db.run {
    insert += employee
  }

}
