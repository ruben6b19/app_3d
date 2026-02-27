package com.jaco.cc3d.presentation.privado.privateDashboardScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.LocalThemeActions
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.TeacherDisplayViewModel
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.InstitutesScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.InstitutesViewModel
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.SubjectsScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.SubjectsViewModel
import com.jaco.cc3d.presentation.privado.studentDashboard.StudentDashboardScreen
import com.jaco.cc3d.presentation.privado.studentDashboard.StudentDashboardViewModel
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.StudentDisplayViewModel
import com.jaco.cc3d.presentation.privado.teacherDashboard.TeacherDashboardScreen
import com.jaco.cc3d.presentation.privado.teacherDashboard.TeacherDashboardViewModel
import com.jaco.cc3d.presentation.publico.login.LoginViewModel
import kotlinx.coroutines.launch

// Asumiendo que SubjectsScreen se creará en el futuro
// Puedes usar un enum o sealed class para las rutas internas del dashboard
enum class DashboardSection {
    INSTITUTES, SUBJECTS, STUDENT_VIEW, TEACHER_VIEW
}

val availableLanguages = mapOf(
    "es" to "Español",
    "en" to "English",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateDashboardScreen(
    initialTab: DashboardSection,
    institutesViewModel: InstitutesViewModel,
    subjectsViewModel: SubjectsViewModel,
    studentDashboardViewModel: StudentDashboardViewModel,
    teacherDashboardViewModel: TeacherDashboardViewModel,
    studentViewModel: StudentDisplayViewModel,
    displayViewModel: TeacherDisplayViewModel,
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit,
    onRoleSwitched: (Int) -> Unit,
    navigateToUsers: (String) -> Unit,
    navigateToCourses: (String, String) -> Unit,
    navigateToTeacherDisplay: (String, String, String, String) -> Unit,
    navigateToStudentDisplay: (String, String, String, String) -> Unit,
    navigateToQuizTemplates: (String, String) -> Unit
) {
    // 1. Estado para la sección actual
    var selectedSection by rememberSaveable { mutableStateOf(initialTab) }

    // 2. 💡 SOLUCIÓN: Estado para el rol actual (para que el check se mueva)
    var currentRole by remember { mutableStateOf(loginViewModel.getLastRole()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val themeActions = LocalThemeActions.current
    val languageActions = LocalLanguageActions.current
    val currentLanguageCode = languageActions.currentLanguage
    var langExpanded by remember { mutableStateOf(false) }

    // Datos de roles disponibles (la lista no suele cambiar)
    val userData = loginViewModel.getUserData()
    val roles = userData?.role ?: emptyList()

    // Sincronizar sección si cambia el parámetro initialTab
    LaunchedEffect(initialTab) {
        selectedSection = initialTab
        // También refrescamos el rol desde el VM por si acaso
        //currentRole = loginViewModel.getLastRole()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("Menú Principal",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp))
                Divider()

                // --- SECCIÓN NAVEGACIÓN ---
                // Solo mostramos gestión si el usuario tiene el rol de Admin (2)
                if (roles.contains(2)) {
                    NavigationDrawerItem(
                        label = { Text("Institutos") },
                        icon = { Icon(Icons.Filled.School, null) },
                        selected = selectedSection == DashboardSection.INSTITUTES,
                        onClick = {
                            selectedSection = DashboardSection.INSTITUTES
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Materias") },
                        icon = { Icon(Icons.Filled.Book, null) },
                        selected = selectedSection == DashboardSection.SUBJECTS,
                        onClick = {
                            selectedSection = DashboardSection.SUBJECTS
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                if (roles.contains(1)) {
                    NavigationDrawerItem(
                        label = { Text("Vista Profesor (Display)") },
                        icon = { Icon(Icons.Default.Monitor, null) }, // Icono descriptivo
                        selected = selectedSection == DashboardSection.TEACHER_VIEW,
                        onClick = {
                            selectedSection = DashboardSection.TEACHER_VIEW
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                if (roles.contains(0)) {
                    NavigationDrawerItem(
                        label = { Text("Mi Aula (Estudiante)") },
                        icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, null) },
                        selected = selectedSection == DashboardSection.STUDENT_VIEW,
                        onClick = {
                            selectedSection = DashboardSection.STUDENT_VIEW
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Divider(Modifier.padding(vertical = 8.dp))

                // --- CONFIGURACIÓN ---
                Text("Configuración",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.primary)

                ListItem(
                    headlineContent = { Text("Modo Oscuro") },
                    trailingContent = {
                        Switch(checked = themeActions.isDark, onCheckedChange = { themeActions.toggleTheme() })
                    }
                )

                // Selector de Idioma (Omitido por brevedad, mantener igual)
                // ...

                Divider(Modifier.padding(vertical = 8.dp))

                // --- CAMBIO DE ROL ---
                if (roles.size > 1) {
                    roles.forEach { roleId ->
                        // 💡 Comparamos con nuestro estado local reactivo
                        val isSelected = roleId == currentRole
                        val roleLabel = when(roleId) {
                            2 -> "Administrador"
                            1 -> "Profesor"
                            else -> "Alumno"
                        }
                        NavigationDrawerItem(
                            label = { Text("Vista: $roleLabel") },
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    scope.launch {
                                        drawerState.close()
                                        loginViewModel.switchRole(roleId)
                                        currentRole = roleId // Actualizamos el check localmente
                                        onRoleSwitched(roleId) // Disparamos la navegación
                                    }

                                    // 💡 ACTUALIZACIÓN CRÍTICA:

                                }
                            },
                            icon = { if(isSelected) Icon(Icons.Default.Check, null) },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    icon = { Icon(Icons.Filled.ExitToApp, null) },
                    selected = false,
                    onClick = onLogout,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        // Contenido principal
        when (selectedSection) {
            DashboardSection.INSTITUTES -> InstitutesScreen(
                viewModel = institutesViewModel,
                onLogout = onLogout,
                navigateToUsers = navigateToUsers,
                navigateToCourses = navigateToCourses,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
            DashboardSection.SUBJECTS -> SubjectsScreen(
                viewModel = subjectsViewModel,
                onLogout = onLogout,
                onMenuClick = { scope.launch { drawerState.open() } },
                navigateToQuizTemplates = navigateToQuizTemplates
            )
            DashboardSection.STUDENT_VIEW -> {
                StudentDashboardScreen(
                    viewModel = studentDashboardViewModel,
                    onLogout = onLogout,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCourseClick = { courseId ->
                        val enrollment = studentDashboardViewModel.enrollments.find { it.courseId == courseId }

                        // 2. Si lo encontramos, navegamos pasando los 3 parámetros requeridos
                        enrollment?.let {
                            navigateToStudentDisplay(
                                it.courseId,                // courseId
                                it.subjectName, // subjectName
                                it.subjectId,        // subjectId
                                it.contentUrl
                            )
                        }
                    }
                )
            }
            DashboardSection.TEACHER_VIEW -> TeacherDashboardScreen(
                viewModel = teacherDashboardViewModel,
                onLogout = onLogout,
                onCourseClick = { courseId ->
                    // Buscamos el nombre de la materia en la lista del VM
                    val course = teacherDashboardViewModel.courses.find { it.id == courseId }
                    course?.let {
                        navigateToTeacherDisplay(
                            it.id,
                            it.subjectName ?: "Materia",
                            it.subjectId,
                            it.contentUrl ?:""
                        )
                    }
                }
            )
        }
    }
}