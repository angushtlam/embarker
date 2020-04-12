package com.raeic.embarker;

import com.raeic.embarker.land.models.StakedChunkManager;
import com.raeic.embarker.land.schedulers.LandScheduler;
import com.raeic.embarker.player.models.EmbarkerPlayerManager;
import com.raeic.embarker.utils.ReflectionUtil;

public class Globals {
    public static Embarker plugin = null;
    public static ReflectionUtil reflectionUtil = null;

    public static EmbarkerPlayerManager embarkerPlayers = null;
    public static StakedChunkManager stakedChunks = null;

    public static LandScheduler landScheduler = null;
}
