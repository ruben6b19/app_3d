package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.subjectConfig.SlideDto
import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigDto
import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigRequest
import com.jaco.cc3d.domain.models.*

fun SubjectConfigDomainRequest.toDataRequest() = SubjectConfigRequest(
    subjectId = this.subjectId,
    contentUrl = this.contentUrl,
    slides = this.slides.map { it.toDataDto() },
    version = this.version,
    lastCommitMessage = this.lastCommitMessage,
    contentHash = this.contentHash
)

fun SubjectConfigDto.toDomainModel() = SubjectConfig(
    id = this.id,
    subject = this.subject,
    contentUrl = this.contentUrl,
    slides = this.slides.map { it.toDomainModel() },
    version = this.version,
    lastCommitMessage = this.lastCommitMessage
)

fun Slide.toDataDto() = SlideDto(slideId, label, type)
fun SlideDto.toDomainModel() = Slide(slideId, label, type)