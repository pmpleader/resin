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

package com.caucho.transaction.enhancer;

import com.caucho.bytecode.JAnnotation;
import com.caucho.bytecode.JMethod;
import com.caucho.config.ConfigException;
import com.caucho.java.gen.BaseMethod;
import com.caucho.java.gen.CallChain;
import com.caucho.java.gen.GenClass;
import com.caucho.loader.enhancer.MethodEnhancer;
import com.caucho.util.L10N;

import javax.ejb.TransactionAttributeType;
import java.lang.reflect.Method;

/**
 * Enhancing a method objects.
 */
public class TransactionEnhancer implements MethodEnhancer {
  private static final L10N L = new L10N(TransactionEnhancer.class);

  private Class _annotationType;

  /**
   * Sets the annotation type.
   */
  public void setAnnotation(Class type)
    throws ConfigException
  {
    _annotationType = type;

    try {
      Method method = type.getMethod("value");

      if (method != null &&
	  method.getReturnType().equals(TransactionAttributeType.class))
	return;
    } catch (Throwable e) {
    }

    throw new ConfigException(L.l("'{0}' is an illegal annotation type for TransactionEnhancer.  The annotation must have a value() method returning a TransactionAttributeType.",
				  type.getName()));
  }
  
  /**
   * Enhances the method.
   *
   * @param genClass the generated class
   * @param jMethod the method to be enhanced
   * @param jAnn the annotation to be enhanced
   */
  public void enhance(GenClass genClass,
		      JMethod jMethod,
		      JAnnotation jAnn)
  {
    TransactionAttributeType type;
    type = (TransactionAttributeType) jAnn.get("value");

    BaseMethod genMethod = genClass.createMethod(jMethod);
    CallChain call = genMethod.getCall();

    switch (type) {
    case MANDATORY:
      call = new MandatoryCallChain(call);
      break;
    case REQUIRED:
      call = new RequiredCallChain(call);
      break;
    case REQUIRESNEW:
      call = new RequiresNewCallChain(call);
      break;
    case NEVER:
      call = new NeverCallChain(call);
      break;
    case SUPPORTS:
      call = new SupportsCallChain(call);
      break;
    case NOTSUPPORTED:
      call = new SuspendCallChain(call);
      break;
    default:
      break;
    }

    genMethod.setCall(call);
  }
}
