package com.example.finalprojectgroup4.features.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
 * Create an account.
 * Placeholder for now; Track C adds the form, validation and Firebase Auth.
 *
 * Kind: @Composable function. Owner: Track C (Vraj).
 */
@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SpotTopBar(
                title = stringResource(R.string.title_signup),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationContentDescription = stringResource(R.string.action_back),
                onNavigationClick = onBackToLogin
            )
        }
    ) { innerPadding ->
        PlaceholderBody(
            message = stringResource(R.string.placeholder_signup),
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = onSignedUp) {
                Text(stringResource(R.string.action_signup))
            }
            TextButton(onClick = onBackToLogin) {
                Text(stringResource(R.string.action_have_account))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    FinalProjectGroup4Theme { SignUpScreen(onSignedUp = {}, onBackToLogin = {}) }
}
