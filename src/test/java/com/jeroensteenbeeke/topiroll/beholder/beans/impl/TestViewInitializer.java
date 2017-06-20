/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeroensteenbeeke.topiroll.beholder.beans.IAccountInitializer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;

import javax.annotation.Nonnull;

@Component
public class TestViewInitializer implements IAccountInitializer {
	private static final Logger log = LoggerFactory
			.getLogger(TestViewInitializer.class);

	@Autowired
	private MapService mapService;

	@Autowired
	private MapViewDAO viewDAO;

	@Autowired
	private FogOfWarShapeDAO shapeDAO;
	
	@Autowired
	private TokenDefinitionDAO tokenDAO;
	
	@Autowired
	private FogOfWarGroupDAO groupDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onAccountCreated(@Nonnull BeholderUser user) {
		MapView view = new MapView();
		view.setHeight(768);
		view.setWidth(1360);
		view.setIdentifier("test");
		view.setScreenDiagonalInInches(32);
		view.setOwner(user);
		viewDAO.save(view);
		
		ScaledMap map = null;

		try (InputStream stream = TestViewInitializer.class
				.getResourceAsStream("temple.jpg")) {
			byte[] image = readImage(stream);

			map = mapService.createMap(user, "temple", 18, image)
					.getObject();
			
			FogOfWarGroup group = new FogOfWarGroup();
			group.setMap(map);
			group.setName("P3");
			groupDAO.save(group);

			FogOfWarRect rect = new FogOfWarRect();
			rect.setOffsetX(187);
			rect.setOffsetY(153);
			rect.setWidth(147);
			rect.setHeight(74);
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);
			
			
			rect = new FogOfWarRect();
			rect.setOffsetX(187);
			rect.setOffsetY(119);
			rect.setWidth(79);
			rect.setHeight(35);
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);	

			rect = new FogOfWarRect();
			rect.setOffsetX(228);
			rect.setOffsetY(219);
			rect.setWidth(104);
			rect.setHeight(39);
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);
			
			

			log.info("Test data created for user {}", user.getUsername());

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		try (InputStream stream = TestViewInitializer.class
				.getResourceAsStream("random_monster.png")) {
			byte[] imageData = readImage(stream);

			String[] names = {
					"Monster",
					"Big Monster",
					"Bigger Monster",
					"Biggest Monster"
			};

			for (int squares = 1; squares <= 4; squares++) {
				TokenDefinition token = new TokenDefinition();
				token.setDiameterInSquares(squares);
				token.setImageData(imageData);
				token.setName(names[squares-1]);
				token.setOwner(user);

				tokenDAO.save(token);
			}


		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

	}

	private byte[] readImage(InputStream stream) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			int in = -1;
			while ((in = stream.read()) != -1) {
				bos.write(in);
			}
			bos.flush();
			bos.close();

			return bos.toByteArray();
		}
	}

}
