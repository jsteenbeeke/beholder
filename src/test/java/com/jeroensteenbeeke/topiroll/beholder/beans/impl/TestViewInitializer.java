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
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

@Component
public class TestViewInitializer implements IAccountInitializer {
	private static final Logger log = LoggerFactory.getLogger(TestViewInitializer.class);
	
	@Autowired
	private MapService mapService;
	
	@Autowired
	private MapViewDAO viewDAO;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void onAccountCreated(BeholderUser user) {
		MapView view = new MapView();
		view.setHeight(1080);
		view.setWidth(1920);
		view.setIdentifier("test");
		view.setScreenDiagonalInInches(24);
		view.setOwner(user);
		viewDAO.save(view);
		
		try (InputStream stream = TestViewInitializer.class.getResourceAsStream("temple.jpg"); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			int in = -1;
			while ((in = stream.read()) != -1) {
				bos.write(in);
			}
			bos.flush();
			bos.close();
			
			byte[] image = bos.toByteArray();
			
			mapService.createMap(user, "temple", 18, image);
			
			log.info("Test data created for user {}", user.getUsername());
			
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		

	}

}