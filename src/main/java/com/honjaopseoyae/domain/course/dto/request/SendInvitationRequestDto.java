package com.honjaopseoyae.domain.course.dto.request;

import java.util.List;

public record SendInvitationRequestDto(
        List<Long> receiverIds
) {}