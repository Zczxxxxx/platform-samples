/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.platform.storage.storageaccessframework

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.google.android.catalog.framework.annotations.Sample

@Sample(
    name = "GetDocument",
    description = "Open a document using the Storage Access Framework",
    documentation = "https://developer.android.com/training/data-storage/shared/documents-files#open-file",
)
@Composable
fun GetDocument() {
    var fileTypes by remember { mutableStateOf(emptySet<FileType>()) }
    var selectMultiple by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedFiles by remember { mutableStateOf(emptyList<Uri>()) }

    val getSingleDocument = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedFiles = uri?.let { listOf(it) } ?: emptyList()
    }

    val getMultipleDocuments = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedFiles = uris
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectMultiple) {
                        getMultipleDocuments.launch("*/*")
                    } else {
                        getSingleDocument.launch("*/*")
                    }
                },
            ) {
                Text(if (selectMultiple) "Select Files" else "Select File")
            }
        },
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {
            item {
                ListItem(
                    headlineContent = { Text("File type filter") },
                    supportingContent = {
                        Text(
                            if (fileTypes.isEmpty()) "Any" else fileTypes.joinToString { it.name },
                        )
                    },
                    trailingContent = {
                        val scrollState = rememberScrollState()
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopStart),
                        ) {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    Icons.Default.FilterAlt,
                                    contentDescription = "Localized description",
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                scrollState = scrollState,
                            ) {
                                FileType.entries.forEach { fileType ->
                                    DropdownMenuItem(
                                        text = { Text(fileType.name) },
                                        onClick = { fileTypes = fileTypes.toggle(fileType) },
                                        leadingIcon = {
                                            if (fileTypes.contains(fileType)) {
                                                Icon(
                                                    Icons.Outlined.Check,
                                                    contentDescription = "Selected",
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                            LaunchedEffect(expanded) {
                                if (expanded) {
                                    // Scroll to show the bottom menu items.
                                    scrollState.scrollTo(scrollState.maxValue)
                                }
                            }
                        }
                    },
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("File type filter") },
                    trailingContent = {
                        Switch(
                            modifier = Modifier.semantics {
                                contentDescription = "Select multiple files"
                            },
                            checked = selectMultiple,
                            onCheckedChange = { selectMultiple = it },
                            thumbContent = {
                                if (selectMultiple) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            },
                        )
                    },
                )
                HorizontalDivider()
            }
        }
    }
}


private fun <T> Set<T>.toggle(item: T): Set<T> {
    return if (contains(item)) {
        this - item
    } else {
        this + item
    }
}