package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import views._

import models._

object NewForm extends Controller {
  
  /**
   * Sign Up Form definition.
   *
   * Once defined it handle automatically, ,
   * validation, submission, errors, redisplaying, ...
   */
  val newForm: Form[Something] = Form(
    
    // Define a mapping that will handle Something values
    mapping(
      "somethingname" -> text(minLength = 4),
      "email" -> email,
      "ip" -> text.verifying(pattern(
    		  			"""\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b""".r,
    		  			"constraint.ip",
    		  			"error.ip")),
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text 
      ).verifying(
        "Passwords don't match", passwords => passwords._1 == passwords._2
      ),
      
      "profile" -> mapping(
        "country" -> nonEmptyText,
        "address" -> optional(text),
        "age" -> optional(number(min = 18, max = 100))
      )
      (SomethingProfile.apply)(SomethingProfile.unapply),
      
      "accept" -> checked("You must accept the conditions")
      
    )
    // The mapping signature doesn't match the Something case class signature,
    // so we have to define custom binding/unbinding functions
    {
      // Binding: Create a Something from the mapping result (ignore the second password and the accept field)
      (somethingname, email, ip, passwords, profile, _) => Something(somethingname, passwords._1, email, ip, profile) 
    } 
    {
      // Unbinding: Create the mapping values from an existing Something value
      something => Some(something.somethingname, something.email, something.ip, (something.password, ""), something.profile, false)
    }.verifying(
      // Add an additional constraint: The somethingname must not be taken (you could do an SQL request here)
      "This somethingname is not available",
      something => !Seq("admin", "guest").contains(something.somethingname)
    )
  )
  
  /**
   * Display an empty form.
   */
  def form = Action {
    Ok(html.form(newForm));
  }
  
  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    newForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      errors => BadRequest(html.form(errors)),
      
      // We got a valid Something value, display the summary
      something => Ok(html.summary(something))
    )
  }
  
}