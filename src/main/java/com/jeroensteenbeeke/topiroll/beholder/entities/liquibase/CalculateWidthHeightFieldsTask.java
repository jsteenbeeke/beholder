/**
 * This file is part of Beholder
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
package com.jeroensteenbeeke.topiroll.beholder.entities.liquibase;

import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class CalculateWidthHeightFieldsTask implements CustomTaskChange {

	@Override
	public String getConfirmationMessage() {
		return "Width and height calculated";
	}

	@Override
	public void setUp() throws SetupException {

	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {

	}

	@Override
	public ValidationErrors validate(Database database) {
		return new ValidationErrors();
	}

	@Override
	public void execute(Database database) throws CustomChangeException {
		JdbcConnection db = (JdbcConnection) database.getConnection();

		List<String> updates = new ArrayList<>();

		try {
			Statement statement = db.createStatement();
			ResultSet r = statement
					.executeQuery("SELECT id, data FROM scaledmap");

			while (r.next()) {
				long id = r.getLong("id");
				byte[] image = r.getBytes("data");

				Dimension imageDimensions = ImageUtil.getImageDimensions(image);

				updates.add(String.format(
						"UPDATE scaledmap SET basicwidth = %d, basicheight = %d WHERE id=%d",
						(int) imageDimensions.getWidth(),
						(int) imageDimensions.getHeight(), id));
			}

			for (String sql : updates) {
				db.prepareCall(sql).execute();

			}

		} catch (DatabaseException | SQLException e) {
			throw new CustomChangeException(e);
		}

	}

}
