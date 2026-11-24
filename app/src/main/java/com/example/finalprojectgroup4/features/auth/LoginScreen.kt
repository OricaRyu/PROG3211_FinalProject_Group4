package com.example.finalprojectgroup4.features.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.ui.components.PlaceholderBody
import com.example.finalprojectgroup4.ui.components.SpotTopBar
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme

/**
 * Email and password login.
 * Placeholder for now; Track C adds the form and Firebase Auth.
 *
 * Kind: @Composable function. Owner: Track C (Vraj).
 */
@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { SpotTopBar(title = stringResource(R.string.title_login)) }
    ) { innerPadding ->
        PlaceholderBody(
            message = stringResource(R.string.placeholder_login),
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = onLoggedIn) {
                Text(stringResource(R.string.action_login))
            }
            TextButton(onClick = onCreateAccount) {
                Text(stringResource(R.string.action_create_account))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FinalProjectGroup4Theme { LoginScreen(onLoggedIn = {}, onCreateAccount = {}) }
}
