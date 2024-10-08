/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.compute.loaders;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;

import java.util.NoSuchElementException;

@Singleton
public class LoadAllocationIdForInstanceOrNull extends CacheLoader<RegionAndName, String> {
   private final EC2Api client;

   @Inject
   public LoadAllocationIdForInstanceOrNull(EC2Api client) {
      this.client = client;
   }

   @Override
   public String load(final RegionAndName key) throws Exception {
      try {
         return Iterables.find(client.getElasticIPAddressApi().get().describeAddressesInRegion(key.getRegion()),
                  new Predicate<PublicIpInstanceIdPair>() {

                     @Override
                     public boolean apply(PublicIpInstanceIdPair input) {
                        return key.getName().equals(input.getInstanceId());
                     }

                  }).getAllocationId();
      } catch (NoSuchElementException e) {
         return null;
      }
   }
}
