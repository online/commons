// =================================================================================================
// Copyright 2011 Twitter, Inc.
// -------------------------------------------------------------------------------------------------
// Licensed to the Apache Software Foundation (ASF) under one or more contributor license
// agreements.  See the NOTICE file distributed with this work for additional information regarding
// copyright ownership.  The ASF licenses this file to you under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with the License.  You may
// obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under the
// License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// express or implied.  See the License for the specific language governing permissions and
// limitations under the License.
// =================================================================================================

package com.twitter.common.zookeeper;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;

import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.zookeeper.ZooKeeperClient.ZooKeeperConnectionException;

/**
 * Utilities for dealing with zoo keeper.
 *
 * @author John Sirois
 */
public final class ZooKeeperUtils {

  private static final Logger LOG = Logger.getLogger(ZooKeeperUtils.class.getName());

  /**
   * An appropriate default session timeout for Twitter ZooKeeper clusters.
   */
  public static final Amount<Integer,Time> DEFAULT_ZK_SESSION_TIMEOUT = Amount.of(3, Time.SECONDS);

  /**
   * The magic version number that allows any mutation to always succeed regardless of actual
   * version number.
   */
  public static final int ANY_VERSION = -1;

  /**
   * Returns true if the given exception indicates an error that can be resolved by retrying the
   * operation without modification.
   *
   * @param e the exception to check
   * @return true if the causing operation is strictly retryable
   */
  public static boolean isRetryable(KeeperException e) {
    Preconditions.checkNotNull(e);

    switch (e.code()) {
      case CONNECTIONLOSS:
      case SESSIONEXPIRED:
      case SESSIONMOVED:
      case OPERATIONTIMEOUT:
        return true;

      case RUNTIMEINCONSISTENCY:
      case DATAINCONSISTENCY:
      case MARSHALLINGERROR:
      case BADARGUMENTS:
      case NONODE:
      case NOAUTH:
      case BADVERSION:
      case NOCHILDRENFOREPHEMERALS:
      case NODEEXISTS:
      case NOTEMPTY:
      case INVALIDCALLBACK:
      case INVALIDACL:
      case AUTHFAILED:
      case UNIMPLEMENTED:

      // These two should not be encountered - they are used internally by ZK to specify ranges
      case SYSTEMERROR:
      case APIERROR:

      case OK: // This is actually an invalid ZK exception code

      default:
        return false;
    }
  }

  /**
   * Ensures the given {@code path} exists in the ZK cluster accessed by {@code zkClient}.  If the
   * path already exists, nothing is done; however if any portion of the path is missing, it will be
   * created with the given {@code acl} as a persistent zookeeper node.  The given {@code path} must
   * be a valid zookeeper absolute path.
   *
   * @param zkClient the client to use to access the ZK cluster
   * @param acl the acl to use if creating path nodes
   * @param path the path to ensure exists
   * @throws ZooKeeperConnectionException if there was a problem accessing the ZK cluster
   * @throws InterruptedException if we were interrupted attempting to connect to the ZK cluster
   * @throws KeeperException if there was a problem in ZK
   */
  public static void ensurePath(ZooKeeperClient zkClient, List<ACL> acl, String path)
      throws ZooKeeperConnectionException, InterruptedException, KeeperException {
    Preconditions.checkNotNull(zkClient);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(path.startsWith("/"));

    ensurePathInternal(zkClient, acl, path);
  }

  private static void ensurePathInternal(ZooKeeperClient zkClient, List<ACL> acl, String path)
      throws ZooKeeperConnectionException, InterruptedException, KeeperException {
    if (zkClient.get().exists(path, false) == null) {
      // The current path does not exist; so back up a level and ensure the parent path exists
      // unless we're already a root-level path.
      int lastPathIndex = path.lastIndexOf('/');
      if (lastPathIndex > 0) {
        ensurePathInternal(zkClient, acl, path.substring(0, lastPathIndex));
      }

      // We've ensured our parent path (if any) exists so we can proceed to create our path.
      try {
        zkClient.get().create(path, null, acl, CreateMode.PERSISTENT);
      } catch (KeeperException.NodeExistsException e) {
        // This ensures we don't die if a race condition was met between checking existence and
        // trying to create the node.
        LOG.info("Node existed when trying to ensure path " + path + ", somebody beat us to it?");
      }
    }
  }

  private ZooKeeperUtils() {
    // utility
  }
}
