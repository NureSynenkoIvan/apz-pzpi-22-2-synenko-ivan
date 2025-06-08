package com.bastionserver.notification.scenarios;

import java.util.Objects;

public class MessageScenario {
    private String scenarioName;
    private String onAlertStart;
    private String onAlertFinish;

    public MessageScenario() {

    }

    public MessageScenario(String scenarioName,
                           String onAlertStart,
                           String onAlertFinish) {
        this.scenarioName = scenarioName;
        this.onAlertStart = onAlertStart;
        this.onAlertFinish = onAlertFinish;
    }

    public String getScenarioName() {
        return scenarioName;
    }
    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getOnAlertStart() {
        return onAlertStart;
    }

    public void setOnAlertStart(String onAlertStart) {
        this.onAlertStart = onAlertStart;
    }

    public String getOnAlertFinish() {
        return onAlertFinish;
    }

    public void setOnAlertFinish(String onAlertFinish) {
        this.onAlertFinish = onAlertFinish;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MessageScenario that)) return false;
        return Objects.equals(scenarioName, that.scenarioName)
                && Objects.equals(onAlertStart, that.onAlertStart)
                && Objects.equals(onAlertFinish, that.onAlertFinish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenarioName, onAlertStart, onAlertFinish);
    }

    @Override
    public String toString() {
        return "MessageScenario{" +
                "scenarioName='" + scenarioName + '\'' +
                ", onAlertStart='" + onAlertStart + '\'' +
                ", onAlertFinish='" + onAlertFinish + '\'' +
                "}";
    }
}
