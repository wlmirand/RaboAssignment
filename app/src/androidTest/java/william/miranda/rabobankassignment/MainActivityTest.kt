package william.miranda.rabobankassignment

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import william.miranda.rabobankassignment.ui.main.MainActivity

/**
 * Simple real UI Tests where we try to fetch some URLs
 * Using the Real Application
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Use the default URL
     */
    @Test
    fun whenURLIsValidThenDisplayTheCSV() {

        composeTestRule
            .onNodeWithText("Fetch CSV")
            .performClick()

        composeTestRule.waitUntil(10 * 1000) {
            composeTestRule
                .onNodeWithTag("LazyColumn")
                .isDisplayed()

            composeTestRule
                .onNodeWithTag("Error")
                .isNotDisplayed()
        }
    }

    /**
     * Use the default URL
     */
    @Test
    fun whenURLIsNotValidThenDisplayError() {

        composeTestRule
            .onNodeWithTag("TextFieldClear")
            .performClick()

        composeTestRule
            .onNodeWithTag("TextField")
            .performTextInput("https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/main/issues.cs")

        composeTestRule
            .onNodeWithText("Fetch CSV")
            .performClick()

        composeTestRule.waitUntil(10 * 1000) {
            composeTestRule
                .onNodeWithTag("LazyColumn")
                .isNotDisplayed()

            composeTestRule
                .onNodeWithTag("Error")
                .isDisplayed()
        }
    }
}