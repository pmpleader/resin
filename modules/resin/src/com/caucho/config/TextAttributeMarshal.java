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
 *   Free SoftwareFoundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.config;

import com.caucho.config.types.RawString;
import com.caucho.util.L10N;
import com.caucho.xml.QName;

import org.w3c.dom.Node;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Attribute strategy for a setText or addXXX method.
 */
public class TextAttributeMarshal extends SetterAttributeStrategy {
  private static final Logger log
    = Logger.getLogger(TextAttributeMarshal.class.getName());
  private static final L10N L = new L10N(TextAttributeMarshal.class);

  private TextAttributeMarshal(Method setter)
    throws Exception
  {
    super(setter);
  }
  
  public static SetterAttributeStrategy create(Method setter)
    throws Exception
  {
    Class []param = setter.getParameterTypes();

    if (RawString.class.equals(param[0]))
      return new RawTextAttributeMarshal(setter);
    else
      return new TextAttributeMarshal(setter);
  }

  /**
   * Configures the primitive value.
   *
   * @param builder the owning node builder
   * @param bean the bean to be configured
   * @param name the attribute name
   * @param node the configuration node
   */
  public void configure(NodeBuilder builder,
                        Object bean,
                        QName name,
                        Node node)
    throws Exception
  {
    // server/13cq
    //setAttribute(bean, name, builder.configureRawStringNoTrim(node));
    setAttribute(bean, name, builder.configureString(node));
  }
}
