package model

trait SinkableType

trait Cassandraable {
  def toRestable : Restable
  def values: Array[AnyRef]
}

trait Restable {
  def toCassandraable : Cassandraable
}