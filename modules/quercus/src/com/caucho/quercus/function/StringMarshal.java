/*
 * Copyright (c) 1998-2006 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.quercus.function;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringValueImpl;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;

/**
 * Code for marshaling (PHP to Java) and unmarshaling (Java to PHP) arguments.
 */
public class StringMarshal extends Marshal {
  public static final StringMarshal MARSHAL = new StringMarshal();
  
  public boolean isString()
  {
    return true;
  }

  public boolean isReadOnly()
  {
    return true;
  }

  public Object marshal(Env env, Expr expr, Class expectedClass)
  {
    return expr.evalString(env);
  }

  public Object marshal(Env env, Value value, Class expectedClass)
  {
    return value.toString();
  }

  public Value unmarshal(Env env, Object value)
  {
    if (value == null)
      return NullValue.NULL;
    else
      return new StringValueImpl((String) value);
  }
}
