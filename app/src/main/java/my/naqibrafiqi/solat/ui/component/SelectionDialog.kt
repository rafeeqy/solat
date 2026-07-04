package my.naqibrafiqi.solat.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.ui.theme.SolatTheme

data class SelectionDialogItem<T>(
    val id: Int,
    val value: T,
    val label: String
)

@Composable
fun <T> SelectionDialog(
    title: String,
    selectedItem: T,
    selectionItems: List<SelectionDialogItem<T>>,
    onDismiss: () -> Unit,
    onSelect: (SelectionDialogItem<T>) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier.fillMaxWidth(.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Column(modifier = Modifier.selectableGroup()) {
                    selectionItems.forEach { item ->
                        ListItem(
                            headlineContent = {
                                Text(text = item.label)
                            },
                            modifier = Modifier.clickable(
                                enabled = true,
                                role = Role.RadioButton,
                                onClick = {
                                    onSelect(item)
                                    onDismiss()
                                }
                            ),
                            leadingContent = {
                                RadioButton(
                                    selected = item.value == selectedItem,
                                    onClick = null
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = CardDefaults.cardColors().containerColor)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(id = R.string.common_button_cancel),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
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
