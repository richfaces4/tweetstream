/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.examples.tweetstream.model.view;

import java.util.List;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */

public class View
{

   private int id;

   private List<Page> pages;

   private String viewType;

   public static enum ViewType {PHONE, TABLET, DESKTOP}

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public List<Page> getPages()
   {
      return pages;
   }

   public void setPages(List<Page> pages)
   {
      this.pages = pages;
   }

   public String getViewType()
   {
      return viewType;
   }

   public void setViewType(String viewType)
   {
      this.viewType = viewType;
   }
}
