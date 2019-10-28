package net.tospay.auth.api.listeners;

import net.tospay.auth.model.Network;

import java.util.List;

public interface NetworkListener extends BaseListener{

    void onNetworks(List<Network> networks);

}
