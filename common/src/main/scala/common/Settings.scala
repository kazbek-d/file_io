package common

import com.typesafe.config.ConfigFactory


object Settings {

  private val config = ConfigFactory.load()

  def getConfig = config

  val actorBackendName: String = config.getString("akka.backend-actor-cluster-listener-name")
  val actorFrontendName: String = config.getString("akka.frontend-actor-cluster-listener-name")
  val actorKernelName: String = config.getString("akka.kernel-actor-cluster-listener-name")
  val actorUtilsName: String = config.getString("akka.utils-actor-cluster-listener-name")


  val cassandraAddress: Array[String] = scala.util.Properties.envOrElse("CASSANDRA_ADDRESS", "localhost").split(",")
  val cassandraPort = scala.util.Properties.envOrElse("CASSANDRA_PORT", "9042").toInt
  val cassandraKeyspace: String = scala.util.Properties.envOrElse("CASSANDRA_KEYSPACE", "")
  val cassandraLogin: String = scala.util.Properties.envOrElse("CASSANDRA_LOGIN", "")
  val cassandraPassword: String = scala.util.Properties.envOrElse("CASSANDRA_PASSWORD", "")
  val cassandraTableLoyalty: String = scala.util.Properties.envOrElse("CASSANDRA_TABLE_LOYALTY", "")


  val kafkaBrokers: Array[String] = scala.util.Properties.envOrElse("KAFKA_BROKERS", "").split(",")
  val kafkaTopics: Array[String] = scala.util.Properties.envOrElse("KAFKA_TOPICS", "").split(",")
  val zkQuorum: String = scala.util.Properties.envOrElse("KAFKA_ZOOKEEPER_QUORUM", "")
  val zkGroup: String = scala.util.Properties.envOrElse("KAFKA_ZOOKEEPER_GROUP", "")
  val zkNumThreads: Int = scala.util.Properties.envOrElse("KAFKA_ZOOKEEPER_NUM_THREADS", "1").toInt


  val msSqlMaster: String = scala.util.Properties.envOrElse("MSSQLSERVER_MASTER", "")
  val msSqlDatabaseName: String = scala.util.Properties.envOrElse("MSSQLSERVER_DATABASE", "")
  val msSqlUser: String = scala.util.Properties.envOrElse("MSSQLSERVER_USER", "")
  val msSqlPassword: String = scala.util.Properties.envOrElse("MSSQLSERVER_PASSWORD", "")


  val webserverAddress = scala.util.Properties.envOrElse("WEBSERVER_ADDRESS", "")
  val webserverPort = scala.util.Properties.envOrElse("WEBSERVER_PORT", "8102").toInt

  val kamonStatsdHostname: String = scala.util.Properties.envOrElse("KAMON_STATSD_HOSTNAME", "127.0.0.1")
  val kamonStatsdPort: String = scala.util.Properties.envOrElse("KAMON_STATSD_PORT", "8125")

  val externalApi: String =  scala.util.Properties.envOrElse("EXTERNAL_API", "")

  val filesystemPath = scala.util.Properties.envOrElse("FILESYSTEM_PATH", "/home")

  val fileIOprefix = scala.util.Properties.envOrElse("FILE_IO_PREFIX", "http://localhost:8102")

}
