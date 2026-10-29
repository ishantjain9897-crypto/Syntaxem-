package com.example.calcmaster.ui.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceContainerScreen(
    initialExpression: String = "x^2 - 5*x + 6",
    onSendToCalculator: (String) -> Unit = {}
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: 4-View Equation Workspace, 1: Notebook

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            modifier = Modifier.testTag("workspace_sub_tab_row")
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Equation Workspace (4-View)") },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_equation_workspace")
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Visual Notebook") },
                icon = { Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_visual_notebook")
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTab) {
                0 -> EquationWorkspaceScreen(
                    initialExpression = initialExpression,
                    onSendToCalculator = onSendToCalculator
                )
                1 -> NotebookScreen(
                    onSendToCalculator = onSendToCalculator
                )
            }
        }
    }
}
