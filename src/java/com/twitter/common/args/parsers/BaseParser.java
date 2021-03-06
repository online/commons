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

package com.twitter.common.args.parsers;

import com.google.common.base.Preconditions;

import com.twitter.common.args.Parsers.Parser;

/**
 * Base parser class to relieve other parsers from implementing {@link Parser#getParsedClass()}.
 *
 * @author William Farner
 */
public abstract class BaseParser<T> implements Parser<T> {

  protected final Class<T> parsedClass;

  BaseParser(Class<T> parsedClass) {
    this.parsedClass = Preconditions.checkNotNull(parsedClass);
  }

  @Override
  public Class<T> getParsedClass() {
    return parsedClass;
  }
}
