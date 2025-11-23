package com.example.solat.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.solat.ui.theme.SolatTheme

data class SelectionDialogItem(
    val id: Int,
    val value: String,
    val label: String
)

@Composable
fun SelectionDialog(
    title: String,
    selectedItem: SelectionDialogItem,
    selectionItems: List<SelectionDialogItem>,
    onDismiss: () -> Unit,
    onSelect: (SelectionDialogItem) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { /* NOTE: Leave this empty... */ },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = title) },
        text = {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                selectionItems.forEach { item ->
                    ListItem(
                        headlineContent = { Text(text = item.label) },
                        modifier = Modifier.clickable(
                            enabled = true,
                            role = Role.DropdownList,
                            onClick = { onSelect(item) }
                        ),
                        trailingContent = {
                            RadioButton(
                                selected = selectedItem.id == item.id,
                                onClick = null
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = AlertDialogDefaults.containerColor)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SelectionDialogPreview() {
    SolatTheme {
        SelectionDialog(
            title = "Selection dialog",
            selectedItem = SelectionDialogItem(
                id = 2,
                value = "two",
                label = "Selection item 2"
            ),
            selectionItems = listOf(
                SelectionDialogItem(
                    id = 1,
                    value = "one",
                    label = "Selection item 1"
                ),
                SelectionDialogItem(
                    id = 2,
                    value = "two",
                    label = "Selection item 2"
                ),
                SelectionDialogItem(
                    id = 3,
                    value = "three",
                    label = "Selection item 3"
                )
            ),
            onDismiss = {},
            onSelect = {}
        )
    }
}
