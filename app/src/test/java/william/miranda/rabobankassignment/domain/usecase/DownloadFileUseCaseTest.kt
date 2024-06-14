package william.miranda.rabobankassignment.domain.usecase

import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import william.miranda.downloader.Downloader
import java.io.File

class DownloadFileUseCaseTest {

    private lateinit var underTest: DownloadFileUseCase

    @MockK
    private lateinit var downloader: Downloader

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        underTest = DownloadFileUseCase(
            downloader = downloader
        )
    }

    @Test
    fun `when Use Case is called Then Downloader is called`() {
        val sourceUrl = "An Url"
        val file = mockk<File>()

        runTest {
            underTest.run(
                sourceUrl = sourceUrl,
                targetFile = file
            )
        }

        coVerify(exactly = 1) {
            downloader.download(
                sourceUrl = sourceUrl,
                targetFile = file
            )
        }
    }
}