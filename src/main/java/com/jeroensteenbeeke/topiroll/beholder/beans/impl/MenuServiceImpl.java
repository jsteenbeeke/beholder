package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.solstice.api.Any;
import com.jeroensteenbeeke.topiroll.beholder.beans.MenuItemProvider;
import com.jeroensteenbeeke.topiroll.beholder.beans.MenuService;

@Component
@Scope(value = "request")
class MenuServiceImpl implements MenuService {
	@Autowired
	private Any<MenuItemProvider> menuItemProviders;

	@Override
	public List<MenuItemProvider> getProviders() {
		return menuItemProviders.stream()
				.sorted(Comparator.comparing(MenuItemProvider::getPriority))
				.collect(Collectors.toList());
	}
}
