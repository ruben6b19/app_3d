package com.jaco.cc3d.domain.usecases.bible

import com.jaco.cc3d.domain.models.BibleVerse
import com.jaco.cc3d.domain.repositories.bible.BibleRepository
import javax.inject.Inject

/**
 * Caso de Uso para obtener un versículo específico de la Biblia.
 *
 * Los Casos de Uso (Use Cases) encapsulan la lógica de negocio y
 * permiten que el ViewModel permanezca simple y solo maneje estados de UI.
 */
class GetVerseUseCase @Inject constructor(
    // Dagger inyecta el repositorio, que a su vez usa el DAO.
    private val bibleRepository: BibleRepository
) {
    /**
     * Hace el caso de uso invocable como una función.
     * Ejemplo de uso en el ViewModel: getVerseUseCase(1, 1)
     */
    suspend operator fun invoke(bibleId: String, citation: String): List<BibleVerse> {
        // 🔑 Lógica de Negocio:
        // Aquí podrías añadir validaciones (ej. book y chapter > 0),
        // formateo, o manejo de errores antes de llamar al repositorio.

        // Por ahora, solo delegamos la llamada.


        return bibleRepository.getVerses(bibleId,citation)
    }
}