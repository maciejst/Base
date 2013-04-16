package models

case class Something(
  somethingname: String, 
  password: String,
  email: String,
  ip: String,
  profile: SomethingProfile
)

case class SomethingProfile(
  country: String,
  address: Option[String],
  age: Option[Int]
)