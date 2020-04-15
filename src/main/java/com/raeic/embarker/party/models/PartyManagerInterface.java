package com.raeic.embarker.party.models;

import java.util.ArrayList;

public interface PartyManagerInterface {
    Party create(String leaderUniqueId, ArrayList<String> partyPlayerUniqueIds);
    Party findParty(String playerLookupUniqueId);
}
