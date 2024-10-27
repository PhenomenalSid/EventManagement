package com.example.eventmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatsDTO {

    @JsonProperty("event_id")
    Long eventId;

    @JsonProperty("user_name")
    String username;

    @JsonProperty("total_rsvps")
    Long totalRSVPs;

    @JsonProperty("accepted_rsvps")
    Long acceptedRSVPs;

    @JsonProperty("pending_rsvps")
    Long pendingRSVPs;

    @JsonProperty("rejected_rsvps")
    Long rejectedRSVPs;
}
