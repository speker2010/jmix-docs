package com.company.onboarding.view.city;

import com.company.onboarding.entity.City;

import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "cities/:id", layout = MainView.class)
@ViewController("City.detail")
@ViewDescriptor("city-detail-view.xml")
@EditedEntityContainer("cityDc")
public class CityDetailView extends StandardDetailView<City> {
}