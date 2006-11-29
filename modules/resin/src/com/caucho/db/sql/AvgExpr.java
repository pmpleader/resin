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

package com.caucho.db.sql;

import com.caucho.log.Log;
import com.caucho.util.L10N;

import java.sql.SQLException;
import java.util.logging.Logger;

public class AvgExpr extends FunExpr implements GroupExpr {
  protected static final L10N L = new L10N(SumExpr.class);
  private static final Logger log = Log.open(SumExpr.class);
  
  private Expr _expr;
  private int _groupField;

  protected void addArg(Expr expr)
    throws SQLException
  {
    if (_expr != null)
      throw new SQLException(L.l("sum requires a single argument"));

    _expr = expr;
  }

  /**
   * Returns the expected result type of the expression.
   */
  public Class getType()
  {
    return double.class;
  }

  protected Expr bind(Query query)
    throws SQLException
  {
    _groupField = query.getDataFields();

    query.setDataFields(_groupField + 2);
    query.setGroup(true);

    _expr = _expr.bind(query);
    
    return this;
  }

  /**
   * Initializes aggregate functions during the group phase.
   *
   * @param context the current database tuple
   */
  public void initGroup(QueryContext context)
    throws SQLException
  {
    context.setGroupDouble(_groupField, 0);
    context.setGroupDouble(_groupField + 1, 0);
  }

  /**
   * Evaluates aggregate functions during the group phase.
   *
   * @param context the current database tuple
   */
  public void evalGroup(QueryContext context)
    throws SQLException
  {
    if (_expr.isNull(context))
      return;

    double count = context.getGroupDouble(_groupField);
    
    context.setGroupDouble(_groupField, count + 1);
    
    double value = _expr.evalDouble(context);
    double sum = context.getGroupDouble(_groupField + 1);

    context.setGroupDouble(_groupField + 1, sum + value);
  }

  /**
   * Returns true for a null value.
   *
   * @param rows the current tuple being evaluated
   *
   * @return true if null
   */
  public boolean isNull(QueryContext context)
    throws SQLException
  {
    return context.isGroupNull(_groupField);
  }

  /**
   * Evaluates the expression as a double.
   *
   * @param rows the current tuple being evaluated
   *
   * @return the double value
   */
  public double evalDouble(QueryContext context)
    throws SQLException
  {
    double count = context.getGroupDouble(_groupField);

    if (count == 0)
      return 0;
    else
      return context.getGroupDouble(_groupField + 1) / count;
  }

  /**
   * Evaluates the expression as a long.
   *
   * @param rows the current tuple being evaluated
   *
   * @return the long value
   */
  public long evalLong(QueryContext context)
    throws SQLException
  {
    return (long) evalDouble(context);
  }

  /**
   * Evaluates the expression as a string.
   *
   * @param rows the current tuple being evaluated
   *
   * @return the string value
   */
  public String evalString(QueryContext context)
    throws SQLException
  {
    return String.valueOf(evalDouble(context));
  }
}
