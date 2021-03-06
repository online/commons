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

package com.twitter.common.util;

/**
 * An abstraction of the system clock.
 *
 * @author John Sirois
 */
public interface Clock {

  /**
   * A clock that returns the the actual time reported by the system.
   */
  Clock SYSTEM_CLOCK = new Clock() {
    @Override public long nowMillis() {
      return System.currentTimeMillis();
    }
    @Override public long nowNanos() {
      return System.nanoTime();
    }
    @Override public void waitFor(long millis) throws InterruptedException {
      Thread.sleep(millis);
    }
  };

  /**
   * Returns the current time in milliseconds since the epoch.
   *
   * @return The current time in milliseconds since the epoch.
   * @see System#currentTimeMillis()
   */
  long nowMillis();

  /**
   * Returns the current time in nanoseconds.  Should be used only for relative timing.
   * {@see System.nanoTime()} for tips on using the value returned here.
   *
   * @return A measure of the current time in nanoseconds.
   */
  long nowNanos();

  /**
   * Waits for the given amount of time to pass on this clock before returning.
   *
   * @param millis the amount of time to wait in milliseconds
   * @throws InterruptedException if this wait was interrupted
   */
  void waitFor(long millis) throws InterruptedException;
}
