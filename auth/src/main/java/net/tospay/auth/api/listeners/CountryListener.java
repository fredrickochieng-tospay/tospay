package net.tospay.auth.api.listeners;

import net.tospay.auth.model.Country;

import java.util.List;

public interface CountryListener extends BaseListener{

    void onCountries(List<Country> countries);

}
