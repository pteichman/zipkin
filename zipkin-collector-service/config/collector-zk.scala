/*
 * Copyright 2012 Twitter Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.twitter.zipkin.builder.{Scribe, ZooKeeperClientBuilder}
import com.twitter.zipkin.cassandra
import com.twitter.zipkin.collector.builder.{Adjustable, CollectorServiceBuilder}
import com.twitter.zipkin.config.sampler.ZooKeeperAdjustableRateConfig
import com.twitter.zipkin.storage.Store

val keyspaceBuilder = cassandra.Keyspace.static(nodes = Set("localhost"))
val cassandraBuilder = Store.Builder(
  cassandra.StorageBuilder(keyspaceBuilder),
  cassandra.IndexBuilder(keyspaceBuilder),
  cassandra.AggregatesBuilder(keyspaceBuilder)
)

val zkBuilder = ZooKeeperClientBuilder(Seq("localhost"))
val sampleRateConfig = new ZooKeeperAdjustableRateConfig()

CollectorServiceBuilder(Scribe.Interface())
  .writeTo(cassandraBuilder)
  .sampleRate(Adjustable.zookeeper(zkBuilder, "/twitter/service/zipkin/config", "samplerate", 0.1))
  .use(ServerSets())
