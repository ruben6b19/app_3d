package com.jaco.cc3d.domain.usecases.subjectConfig

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.SubjectConfig
import com.jaco.cc3d.domain.repositories.subjectConfig.SubjectConfigRepository
import javax.inject.Inject

class GetSubjectConfig @Inject constructor(
    private val repository: SubjectConfigRepository
) {
    suspend operator fun invoke(subjectId: String): Result<SubjectConfig> {
        return repository.getSubjectConfig(subjectId)
            .mapCatching { dto -> dto.toDomainModel() }
    }
}