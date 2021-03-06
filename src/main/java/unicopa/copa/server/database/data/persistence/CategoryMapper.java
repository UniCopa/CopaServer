/*
 * Copyright (C) 2013 UniCoPA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package unicopa.copa.server.database.data.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.server.database.data.db.DBCategoryNode;

public interface CategoryMapper {

    public List<Integer> getChildNodeIDs(@Param("categoryID") int categoryID);

    public DBCategoryNode getDBCategoryNode(@Param("categoryID") int categoryID);

    public DBCategoryNode getDBCategoryNodeLeaf(
	    @Param("categoryID") int categoryID);

    public Integer categoryExsists(@Param("categoryID") int categoryID);

    public void insertCategory(@Param("category") CategoryNodeImpl category);

    public void insertCategoryConnection(@Param("parentID") int parentID,
	    @Param("childID") int childID);

    public void deleteCategory();

    public void deleteCategoryConnection();

    public void resetAutoGeneratedKey();
}
