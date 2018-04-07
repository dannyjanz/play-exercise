package models.json

import models.Employee
import play.api.libs.json.{Format, Json}

object EmployeeJson {

  implicit val employeeTransform: Format[Employee] = Json.format[Employee]

}
