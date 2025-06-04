package com.bastionserver.analysis.service.strategy;

import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.model.SkyState;

import java.util.List;

public interface ThreatAnalysisStrategy {
    public List<SkyObject> analyze(SkyState skyState);
}
