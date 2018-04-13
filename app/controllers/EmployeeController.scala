package controllers

import javax.inject.{Inject, Singleton}

import models.Employee
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

  def single(id: Long) = Action.async { implicit request =>

    employeeStorage.byId(id).map {
      case Some(employee) => render {
        case Accepts.Json => Ok(Json.toJson(employee))
        case _ => Ok(views.html.employees(Seq(employee)))
      }

      case None => NotFound(s"No Employee with Id $id")
    }

  }

  def addForm = Action {
    Ok(views.html.newemployee())
  }

  def add = Action(parse.json[Employee]).async { implicit request =>

    employeeStorage.store(request.body).map { storedEmployee =>
      Ok(Json.toJson(storedEmployee))
    }

  }

}