package models

/**
 * Created by marcus on 03/11/16.
 */
trait Identity[E] {

  def name: String
  def set(entity: E): E
  //def clear(entity: E): E

}
