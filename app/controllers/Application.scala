package controllers

import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Application extends Controller {
	implicit val rds = (
		(__ \ 'name).read[String] and
			(__ \ 'score).read[Long]
		) tupled

	def index = Action {
		Ok(views.html.index())
	}

	def create = Action(parse.json) { request =>
		request.body.validate[(String, Long)].map{
			case (name, score) => Ok(Json.obj("status" -> "OK", "message" -> (name + " scored " + score)))
		}.recoverTotal{
			e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
		}
	}
}
