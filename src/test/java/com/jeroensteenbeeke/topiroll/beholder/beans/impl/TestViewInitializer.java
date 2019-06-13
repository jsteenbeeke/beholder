/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
/**
 * This file is part of Beholder (C) 2016 Jeroen Steenbeeke <p> This program is free software: you
 * can redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details. <p> You should
 * have received a copy of the GNU Affero General Public License along with this program.  If not,
 * see <http://www.gnu.org/licenses/>.
 */


package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.IAccountInitializer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
	private FogOfWarGroupDAO groupDAO;

	@Autowired
	private YouTubePlaylistDAO playlistDAO;

	@Autowired
	private AreaMarkerDAO areaMarkerDAO;

	@Autowired
	private InitiativeParticipantDAO participantDAO;

	@Autowired
	private MapLinkDAO mapLinkDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onAccountCreated(@Nonnull BeholderUser user) {
		MapView view = new MapView();
		view.setHeight(768);
		view.setWidth(1360);
		view.setIdentifier("720p");
		view.setScreenDiagonalInInches(32);
		view.setOwner(user);
		viewDAO.save(view);


		File image = ImageResource.importImage("temple.jpg");

		byte[] portrait = ImageResource.getImageAsByteArray("random_monster.png");

		for (int i = 0; i < 6; i++) {
			mapService.createPortrait(user, null, "Portrait "+ i, portrait);
		}




		MapView view2 = new MapView();
		view2.setHeight(1080);
		view2.setWidth(1920);
		view2.setIdentifier("1080p");
		view2.setScreenDiagonalInInches(24);
		view2.setOwner(user);
		viewDAO.save(view2);

		// 194, 277
		Map<String, Integer> colorsToDegrees = new HashMap<>();
		colorsToDegrees.put("0000ff", 0);
		colorsToDegrees.put("ffff00", 90);
		colorsToDegrees.put("00ff00", 180);
		colorsToDegrees.put("ff0000", 270);

		colorsToDegrees.forEach((color, theta) -> {
			ConeMarker marker = new ConeMarker();
			marker.setTheta(theta);
			marker.setColor(color);
			marker.setExtent(15);
			marker.setOffsetX(277);
			marker.setOffsetY(194);
			marker.setView(view2);
			areaMarkerDAO.save(marker);
		});

		colorsToDegrees.forEach((color, theta) -> {
			LineMarker marker = new LineMarker();
			marker.setTheta((theta + 30) % 360);
			marker.setColor(color);
			marker.setExtent(25);
			marker.setOffsetX(385);
			marker.setOffsetY(88);
			marker.setView(view2);
			areaMarkerDAO.save(marker);
		});

		colorsToDegrees.forEach((color, theta) -> {
			CubeMarker marker = new CubeMarker();
			marker.setOffsetX(50 + theta);
			marker.setOffsetY(335);
			marker.setExtent(12);
			marker.setColor(color);
			marker.setView(view2);
			areaMarkerDAO.save(marker);
		});

		colorsToDegrees.forEach((color, theta) -> {
			CircleMarker marker = new CircleMarker();
			marker.setOffsetX(150 + theta);
			marker.setOffsetY(415);
			marker.setExtent(12);
			marker.setColor(color);
			marker.setView(view2);
			areaMarkerDAO.save(marker);
		});

		YouTubePlaylist playlist = new YouTubePlaylist();
		playlist.setOwner(user);
		playlist.setName("Tavern Music");
		playlist.setNumberOfEntries(5);
		playlist.setUrl(
				"https://www.youtube.com/embed/videoseries?list=PLAr9hQZcvLbmtlOTyZ-JElmEd7VRTBpgr&controls=0&showinfo=0?ecver=2");
		playlistDAO.save(playlist);

		playlist = new YouTubePlaylist();
		playlist.setOwner(user);
		playlist.setName("Battle Music");
		playlist.setNumberOfEntries(6);
		playlist.setUrl(
			"https://www.youtube.com/embed/videoseries?list=PLAr9hQZcvLbkfsZzaD51mI8L7jRIAcOK0&controls=0&showinfo=0?ecver=2");
		playlistDAO.save(playlist);

		log.info("Test data created for user {}", user.getUsername());

		TokenDefinition m = null;

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
				TypedResult<TokenDefinition> def = mapService.createToken(user, null,
						names[squares - 1],
						squares,
						imageData);
				if (m == null && def.isOk()) {
					m = def.getObject();
				}
			}


		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		final TokenDefinition monster = m;

		AtomicReference<FogOfWarGroup> e = new AtomicReference<>();

		mapService.createMap(user, null,"temple", 18, image, null).map(map -> {

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

			if (monster != null) {
				for (int x = 40; x <= 120; x += 20) {
					for (int y = 40; y <= 120; y += 20) {
						TokenInstance instance = mapService.createTokenInstance(monster, map, TokenBorderType.Enemy, x, y,
								String.format("Monster (%d,%d)", x, y));
						mapService.setTokenHP(instance, 50, 50);
					}
				}
			}

			group = new FogOfWarGroup();
			group.setMap(map);
			group.setName("A");
			groupDAO.save(group);

			FogOfWarGroup a = group;

			rect = new FogOfWarRect();
			rect.setOffsetX(44);
			rect.setOffsetY(125);
			rect.setWidth(32);
			rect.setHeight(47);
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);

			group = new FogOfWarGroup();
			group.setMap(map);
			group.setName("B");
			groupDAO.save(group);
			FogOfWarGroup b = group;

			rect = new FogOfWarRect();
			rect.setOffsetX(334);
			rect.setOffsetY(371);
			rect.setWidth(62);
			rect.setHeight(33);
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);

			mapService.createLink(a, b);
			mapService.createLink(b, a);

			group = new FogOfWarGroup();
			group.setMap(map);
			group.setName("E");
			groupDAO.save(group);
			e.set(group);

			rect = new FogOfWarRect();
			rect.setOffsetX(470);
			rect.setOffsetY(125);
			rect.setWidth(62);
			rect.setHeight(33);
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);

			return map;
		});

		image = ImageResource.importImage("hugecrypt.jpg");
		mapService.createMap(user, null,"crypt", 9, image, null).map(map -> {
			FogOfWarGroup group = new FogOfWarGroup();
			group.setMap(map);
			group.setName("ALL");
			groupDAO.save(group);

			FogOfWarRect rect = new FogOfWarRect();
			rect.setOffsetX(0);
			rect.setOffsetY(0);
			rect.setWidth(map.getBasicWidth());
			rect.setHeight(map.getBasicHeight());
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);

			group = new FogOfWarGroup();
			group.setMap(map);
			group.setName("N");
			groupDAO.save(group);

			mapService.createLink(group, e.get());
			mapService.createLink(e.get(), group);

			rect = new FogOfWarRect();
			rect.setOffsetX(0);
			rect.setOffsetY(570);
			rect.setWidth(30);
			rect.setHeight(60);
			rect.setMap(map);
			rect.setGroup(group);
			shapeDAO.save(rect);

			return map;
		});



		InitiativeParticipant jim = new InitiativeParticipant();
		jim.setName("Jim");
		jim.setPlayer(true);
		jim.setInitiativeType(InitiativeType.Normal);
		jim.setScore(0);
		jim.setView(view2);
		jim.setSelected(false);
		participantDAO.save(jim);

		InitiativeParticipant bob = new InitiativeParticipant();
		bob.setName("Bob");
		bob.setPlayer(true);
		bob.setInitiativeType(InitiativeType.Normal);
		bob.setScore(0);
		bob.setView(view2);
		bob.setSelected(false);
		participantDAO.save(bob);

		InitiativeParticipant mike = new InitiativeParticipant();
		mike.setName("Mike");
		mike.setPlayer(true);
		mike.setInitiativeType(InitiativeType.Normal);
		mike.setScore(0);
		mike.setView(view2);
		mike.setSelected(false);
		participantDAO.save(mike);


		System.gc();

	}

	private byte[] readImage(InputStream stream) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			int in;
			while ((in = stream.read()) != -1) {
				bos.write(in);
			}
			bos.flush();
			bos.close();

			return bos.toByteArray();
		}
	}

}
