package controllers

import javax.inject.{Inject, Singleton}

import models.persistence.EmployeeStorage
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import models.json.EmployeeJson._

import scala.concurrent.ExecutionContext


@Singleton
class EmployeeController @Inject()(cc: ControllerComponents, employeeStorage: EmployeeStorage)
                                  (implicit context: ExecutionContext) extends AbstractController(cc) {

  def list = Action.async { implicit request =>

    employeeStorage.list().map { employees =>
      render {
        case Accepts.Json => Ok(Json.toJson(employees))
        case _ => Ok(views.html.employees(employees))
      }
    }
  }

}