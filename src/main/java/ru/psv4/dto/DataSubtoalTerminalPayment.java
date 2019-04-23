package ru.psv4.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public class DataSubtoalTerminalPayment {
    public String station;
    public String checkNum;
    public LocalDateTime datetime1;
    public LocalDateTime datetime2;
    public String clientCard;

    public int getTime() {
        return (int)Math.round((double)Duration.between(datetime1, datetime2).toMillis()/1000d);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type = подитог -> оплатил безнал");
        sb.append(" ");
        sb.append("station = " + station);
        sb.append(" ");
        sb.append("checkNum = " + checkNum);
        sb.append(" ");
        sb.append("datetime1 = " + datetime1);
        sb.append(" ");
        sb.append("datetime2 = " + datetime2);
        sb.append(" ");
        sb.append("time = " + getTime() + " sec");
        sb.append(" ");
        sb.append("clientCard = " + clientCard);
        return sb.toString();
    }
}
