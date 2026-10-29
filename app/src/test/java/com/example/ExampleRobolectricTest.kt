package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.calcmaster.engine.MathEvaluator
import com.example.calcmaster.inventory.InventoryExtractor
import com.example.calcmaster.model.AngleMode
import com.example.calcmaster.scanner.NCERT_PRESETS
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies app_name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Syntaxem", appName)
    }

    @Test
    fun `evaluator computes arithmetic with proper precedence`() {
        val evaluator = MathEvaluator()
        val result = evaluator.evaluate("2 + 3 * 4")
        assertTrue(result.isSuccess)
        assertEquals("14", result.decimalResult)
        assertTrue(result.steps.isNotEmpty())
    }

    @Test
    fun `evaluator computes exact trigonometry in degree mode`() {
        val evaluator = MathEvaluator()
        val result = evaluator.evaluate("sin(30)", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals("0.5", result.decimalResult)
        assertEquals("1/2", result.exactResult)
    }

    @Test
    fun `evaluator catches division by zero with friendly domain explanation`() {
        val evaluator = MathEvaluator()
        val result = evaluator.evaluate("10 / 0")
        assertFalse(result.isSuccess)
        assertTrue(result.errorMessage?.contains("Division by zero") == true)
    }

    @Test
    fun `evaluator solves quadratic equation with structured derivation steps`() {
        val evaluator = MathEvaluator()
        val result = evaluator.evaluate("x^2 - 5x + 6 = 0")
        assertTrue(result.isSuccess)
        assertTrue(result.steps.size >= 3)
        assertTrue(result.decimalResult.contains("2") && result.decimalResult.contains("3"))
    }

    @Test
    fun `inventory extractor accurately parses apparatus manifest`() {
        val raw = "24x Vernier Calipers (0.01cm precision) | SKU: MEC-VC-01 | Cost: $35.00 | Shelf A1 | Calibrated"
        val items = InventoryExtractor.extractFromText(raw)
        assertEquals(1, items.size)
        val item = items[0]
        assertEquals("MEC-VC-01", item.sku)
        assertEquals(24, item.quantity)
        assertEquals(35.0, item.unitCost, 0.01)
        assertEquals("Mechanics", item.category)
        assertFalse(item.needsCalibration)
    }

    @Test
    fun `ncert preset questions are populated and solve without crashes`() {
        val evaluator = MathEvaluator()
        assertTrue(NCERT_PRESETS.isNotEmpty())
        for (preset in NCERT_PRESETS) {
            val result = evaluator.evaluate(preset.extractedFormula)
            assertTrue(result.isSuccess)
        }
    }

    @Test
    fun `evaluator computes 2x2 matrix determinant and inverse`() {
        val evaluator = MathEvaluator()
        val detRes = evaluator.evaluate("det([[2, 3], [1, 4]])")
        assertTrue(detRes.isSuccess)
        assertEquals("5", detRes.exactResult)
        assertEquals("Mathematics: Determinants & Matrices", detRes.conceptTag)

        val invRes = evaluator.evaluate("inv([[1, 2], [3, 4]])")
        assertTrue(invRes.isSuccess)
        // det = 1*4 - 2*3 = -2
        assertTrue(invRes.decimalResult.contains("Inverse"))
        assertTrue(invRes.steps.size >= 4)
    }

    @Test
    fun `evaluator computes vector dot product and angle correctly`() {
        val evaluator = MathEvaluator()
        // Orthogonal vectors [1, 0, 0] and [0, 1, 0] -> dot = 0, angle = 90 deg
        val result = evaluator.evaluate("dot([1, 0, 0], [0, 1, 0])")
        assertTrue(result.isSuccess)
        assertTrue(result.decimalResult.contains("Dot: 0"))
        assertTrue(result.decimalResult.contains("θ = 90°"))
        assertEquals("Class 11 Physics: Motion in a Plane (Vectors)", result.conceptTag)
    }

    @Test
    fun `evaluator computes vector cross product correctly`() {
        val evaluator = MathEvaluator()
        // i x j = k -> [1, 0, 0] x [0, 1, 0] = [0, 0, 1]
        val result = evaluator.evaluate("cross([1, 0, 0], [0, 1, 0])")
        assertTrue(result.isSuccess)
        assertTrue(result.exactResult.contains("1k̂") || result.exactResult.contains("1.0k̂"))
        assertTrue(result.decimalResult.contains("|A×B| = 1"))
    }

    @Test
    fun `core designsystem exports standardized spacing color and typography tokens`() {
        val spacing = com.example.core.designsystem.Spacing()
        assertEquals(48.0f, spacing.touchTargetMin.value, 0.01f)
        assertEquals(16.0f, spacing.cardPadding.value, 0.01f)
        assertEquals(8.0f, spacing.small.value, 0.01f)

        val darkScheme = com.example.core.designsystem.DarkColorScheme
        assertEquals(com.example.core.designsystem.ForestDarkPrimary, darkScheme.primary)
        assertEquals(com.example.core.designsystem.ForestDarkBackground, darkScheme.background)

        val typography = com.example.core.designsystem.Typography
        assertNotNull(typography.headlineLarge)
        assertNotNull(typography.bodyLarge)

        // Lumen Flow Warm Light Theme tokens
        val lightScheme = com.example.core.designsystem.LightColorScheme
        assertEquals(com.example.core.designsystem.LumenBackground, lightScheme.background)
        assertEquals(com.example.core.designsystem.LumenPrimary, lightScheme.primary)
    }

    @Test
    fun `visual notebook models support multi-block page hierarchy`() {
        val page = com.example.calcmaster.ui.workspace.NotebookPage(
            id = "test_page_1",
            title = "Test Kinematics",
            blocks = listOf(
                com.example.calcmaster.ui.workspace.NotebookBlock.NoteBlock("n1", "Note", "Content"),
                com.example.calcmaster.ui.workspace.NotebookBlock.EquationBlock("e1", "2*x + 3", "7"),
                com.example.calcmaster.ui.workspace.NotebookBlock.GraphBlock("g1", "Curve", "x^2")
            )
        )
        assertEquals(3, page.blocks.size)
        assertEquals("Test Kinematics", page.title)
    }

    @Test
    fun `concept replay accurately detects Class 11 curriculum topics`() {
        val trig = com.example.calcmaster.lumenflow.conceptreplay.detectConceptTopic("sin(30) + cos(45)")
        assertEquals("unit_circle_quest", trig.gameId)
        assertEquals("Trigonometry & Unit Circle", trig.topicName)

        val proj = com.example.calcmaster.lumenflow.conceptreplay.detectConceptTopic("20^2 * sin(2 * 45) / 9.8")
        assertEquals("projectile_lab", proj.gameId)
        assertEquals("Motion in a Plane: Projectiles", proj.topicName)

        val quad = com.example.calcmaster.lumenflow.conceptreplay.detectConceptTopic("x^2 - 5*x + 6")
        assertEquals("algebra_arena", quad.gameId)
        assertEquals("Quadratic Equations & Roots", quad.topicName)

        val friction = com.example.calcmaster.lumenflow.conceptreplay.detectConceptTopic("(25 - 0.3 * 49) / 5")
        assertEquals("force_builder", friction.gameId)
        assertEquals("Newton's Laws & Friction", friction.topicName)
    }

    @Test
    fun `lumen learning repository adds and tracks mistake journal entries`() {
        val repo = com.example.calcmaster.lumenflow.model.LumenLearningRepository
        val initialCount = repo.mistakes.size

        val saved = repo.addMistake(
            question = "Evaluate range scaling when v0 doubles",
            studentAnswer = "Doubles (2x)",
            verifiedAnswer = "Quadruples (4x)",
            errorConcept = "Quadratic Velocity Dependence",
            explanation = "Range depends on v0^2"
        )

        assertEquals(initialCount + 1, repo.mistakes.size)
        assertEquals("Quadratic Velocity Dependence", saved.errorConcept)
        assertEquals(0, saved.reviewCount)

        repo.incrementReview(saved.id)
        val updated = repo.mistakes.first { it.id == saved.id }
        assertEquals(1, updated.reviewCount)

        // Clean up test item
        repo.deleteMistake(saved.id)
        assertEquals(initialCount, repo.mistakes.size)
    }

    @Test
    fun `syntaxem curriculum model exports trigonometric functions with mind map nodes`() {
        val trig = com.example.syntaxem.model.SyntaxemCurriculumData.getChapter("ch_03_trigonometry")
        assertNotNull(trig)
        assertEquals("Trigonometric Functions", trig!!.title)
        assertEquals(11, trig.classLevel)
        assertEquals(6, trig.mindMapNodes.size)
        assertEquals(4, trig.flowchartSteps.size)
        assertEquals(4, trig.games.size)

        val nodeLabels = trig.mindMapNodes.map { it.label }
        assertTrue(nodeLabels.contains("Angles"))
        assertTrue(nodeLabels.contains("Ratios"))
        assertTrue(nodeLabels.contains("Unit circle"))
        assertTrue(nodeLabels.contains("Identities"))
        assertTrue(nodeLabels.contains("Equations"))
        assertTrue(nodeLabels.contains("Graphs"))
    }

    @Test
    fun `offline evaluator produces pedagogical steps with axioms why it applies and common pitfalls`() {
        val evaluator = com.example.core.calculation.evaluator.ExpressionEvaluator(com.example.core.calculation.model.AngleMode.DEG)
        val res = evaluator.evaluate("20^2 * sin(2 * 45) / 9.8")
        assertTrue(res.isSuccess)
        assertTrue(res.steps.isNotEmpty())

        val stepWithPedagogy = res.steps.firstOrNull { it.axiomOrIdentity != null }
        assertNotNull(stepWithPedagogy)
        assertNotNull(stepWithPedagogy!!.whyItApplies)
        assertNotNull(stepWithPedagogy.commonPitfall)
    }

    @Test
    fun `concept replay history repository records calculation and advances phases`() {
        val repo = com.example.syntaxem.model.ConceptReplayHistoryRepository
        val initialCount = repo.sessions.size

        val dummyResult = com.example.core.calculation.model.Result.success(
            expression = "sin(45) * cos(45)",
            exactValue = "1/2",
            decimalValue = 0.5,
            steps = listOf(
                com.example.core.calculation.model.Step(
                    stepIndex = 1,
                    description = "Special Angle Evaluation",
                    intermediateExpression = "(√2/2) * (√2/2) = 1/2",
                    axiomOrIdentity = "Unit Circle Special Values",
                    whyItApplies = "Product of equal sine and cosine at 45°",
                    commonPitfall = "Confusing sin(45) with 1/2"
                )
            )
        )

        val session = repo.recordCalculation(
            expression = "sin(45) * cos(45)",
            result = dummyResult,
            topicName = "Trigonometry & Unit Circle"
        )

        assertEquals(initialCount + 1, repo.sessions.size)
        assertEquals("sin(45) * cos(45)", session.expression)
        assertEquals(com.example.syntaxem.model.ReplayPhase.UNDERSTAND, session.currentPhase)

        repo.advancePhase(session.id, com.example.syntaxem.model.ReplayPhase.EXPERIMENT)
        val updated = repo.sessions.first { it.id == session.id }
        assertEquals(com.example.syntaxem.model.ReplayPhase.EXPERIMENT, updated.currentPhase)

        repo.attachNote(session.id, "sin(2*45)/2 = sin(90)/2 = 1/2 matches double angle identity")
        val withNote = repo.sessions.first { it.id == session.id }
        assertEquals("sin(2*45)/2 = sin(90)/2 = 1/2 matches double angle identity", withNote.studentNotes)

        // Cleanup
        repo.removeSession(session.id)
        assertEquals(initialCount, repo.sessions.size)
    }

    @Test
    fun `concept replay detection pairs expressions with matching games and simulations`() {
        val detect = com.example.calcmaster.lumenflow.conceptreplay.detectConceptTopic("20^2 * sin(2 * 45) / 9.8")
        assertEquals("projectile", detect.simId)
        assertEquals("projectile_lab", detect.gameId)
        assertTrue(detect.keyFormula.contains("R ="))

        val trigDetect = com.example.calcmaster.lumenflow.conceptreplay.detectConceptTopic("cos(60) + sin(30)")
        assertEquals("vectors", trigDetect.simId)
        assertEquals("unit_circle_quest", trigDetect.gameId)
    }

    @Test
    fun `syllabus repository initializes bundled curricula and calculates progress correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.syntaxem.data.repository.SyllabusRepository.getInstance(context)

        kotlinx.coroutines.runBlocking {
            repo.initializeBundledDataIfNeeded()
        }

        // Test progress calculation logic
        val dummyChapters = listOf(
            com.example.syntaxem.data.local.dao.ChapterWithProgress(
                chapter = com.example.syntaxem.data.local.entity.ChapterEntity("c1", "s1", 1, "Ch 1", "Desc", true, 1),
                progress = com.example.syntaxem.data.local.entity.ChapterProgressEntity("c1", "COMPLETED")
            ),
            com.example.syntaxem.data.local.dao.ChapterWithProgress(
                chapter = com.example.syntaxem.data.local.entity.ChapterEntity("c2", "s1", 2, "Ch 2", "Desc", true, 2),
                progress = com.example.syntaxem.data.local.entity.ChapterProgressEntity("c2", "IN_PROGRESS")
            ),
            com.example.syntaxem.data.local.dao.ChapterWithProgress(
                chapter = com.example.syntaxem.data.local.entity.ChapterEntity("c3", "s1", 3, "Ch 3", "Desc", true, 3),
                progress = com.example.syntaxem.data.local.entity.ChapterProgressEntity("c3", "NEEDS_REVISION")
            ),
            com.example.syntaxem.data.local.dao.ChapterWithProgress(
                chapter = com.example.syntaxem.data.local.entity.ChapterEntity("c4", "s1", 4, "Ch 4", "Desc", true, 4),
                progress = null
            ),
            // Explicitly not applicable chapter (should be excluded from denominator)
            com.example.syntaxem.data.local.dao.ChapterWithProgress(
                chapter = com.example.syntaxem.data.local.entity.ChapterEntity("c5", "s1", 5, "Ch 5 (Opt)", "Desc", false, 5),
                progress = null
            )
        )

        val progress = repo.calculateProgress(dummyChapters)
        assertEquals(1, progress.completedCount)
        assertEquals(4, progress.totalCount) // 4 applicable chapters
        assertEquals(1, progress.inProgressCount)
        assertEquals(1, progress.needsRevisionCount)
        assertEquals(1, progress.notStartedCount)
        assertEquals(25.0f, progress.percentage, 0.01f)
        assertEquals("1 / 4 Chapters", progress.displayFraction)
        assertEquals("25.0%", progress.displayPercentage)
    }

    @Test
    fun `syllabus repository updates chapter completion state and undoes recent status change`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.syntaxem.data.repository.SyllabusRepository.getInstance(context)

        kotlinx.coroutines.runBlocking {
            repo.initializeBundledDataIfNeeded()

            // Update status
            repo.updateChapterStatus("ch_11_m_02", "COMPLETED")
            val db = com.example.syntaxem.data.local.AppDatabase.getInstance(context)
            val updated = db.syllabusDao().getProgressForChapter("ch_11_m_02")
            assertNotNull(updated)
            assertEquals("COMPLETED", updated!!.status)

            // Undo status
            val undone = repo.undoLastStatusChange()
            assertTrue(undone)

            val reverted = db.syllabusDao().getProgressForChapter("ch_11_m_02")
            assertNotNull(reverted)
            assertEquals("NOT_STARTED", reverted!!.status)
        }
    }

    @Test
    fun `syllabus repository exports and imports progress in valid json format`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.syntaxem.data.repository.SyllabusRepository.getInstance(context)

        kotlinx.coroutines.runBlocking {
            repo.initializeBundledDataIfNeeded()
            repo.updateChapterStatus("ch_11_m_01", "COMPLETED")

            val jsonString = repo.exportProgressToJson()
            assertTrue(jsonString.contains("chapterProgress"))
            assertTrue(jsonString.contains("Syntaxem"))

            // Test import
            val res = repo.importProgressFromJson(jsonString)
            assertTrue(res.isSuccess)
            assertTrue(res.getOrNull() ?: 0 >= 1)
        }
    }

    @Test
    fun `weather snapshot formats freshness and conditions correctly`() {
        val live = com.example.syntaxem.data.weather.WeatherSnapshot(
            temperature = 28.5,
            feelsLikeTemperature = 30.0,
            condition = "Rain Showers",
            humidity = 70,
            windSpeed = 12.0,
            observedAt = System.currentTimeMillis(),
            fetchedAt = System.currentTimeMillis(),
            locationLabel = "Prayagraj, Uttar Pradesh",
            isStale = false,
            isAvailable = true
        )
        assertTrue(live.freshnessLabel.contains("Live") || live.freshnessLabel.contains("just updated") || live.freshnessLabel.contains("ago"))
        assertEquals("rainy", live.conditionIcon)

        val stale = live.copy(isStale = true, fetchedAt = System.currentTimeMillis() - 1000 * 60 * 35)
        assertTrue(stale.freshnessLabel.contains("Cached") || stale.freshnessLabel.contains("Offline"))

        val offlineUnavailable = live.copy(isAvailable = false)
        assertEquals("Weather Unavailable", offlineUnavailable.freshnessLabel)
    }

    @Test
    fun `city location provides standard default Indian academic hubs`() {
        val cities = com.example.syntaxem.data.weather.CityLocation.DEFAULT_CITIES
        assertTrue(cities.size >= 5)
        val names = cities.map { it.name }
        assertTrue(names.contains("New Delhi"))
        assertTrue(names.contains("Prayagraj"))
        assertTrue(names.contains("Varanasi"))
        assertTrue(names.contains("Jaipur"))
    }

    @Test
    fun `weather repository resolves permission-based location with fallback to selected city`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockApi = com.example.syntaxem.data.weather.MockWeatherApi(simulatedDelayMs = 0)
        val repo = com.example.syntaxem.data.weather.WeatherRepository(context, mockApi)

        kotlinx.coroutines.runBlocking {
            // When location permission is denied
            val fallback = repo.resolveLocation(hasPermissionOverride = false)
            assertFalse(fallback.isFromDeviceGps)
            assertEquals("New Delhi, Delhi NCR", fallback.label)
            assertEquals(28.6139, fallback.latitude, 0.001)

            // When city is changed, fallback reflects selected city
            val prayagraj = com.example.syntaxem.data.weather.CityLocation.DEFAULT_CITIES.first { it.name == "Prayagraj" }
            repo.setSelectedCity(prayagraj)
            val updatedFallback = repo.resolveLocation(hasPermissionOverride = false)
            assertEquals("Prayagraj, Uttar Pradesh", updatedFallback.label)
        }
    }

    @Test
    fun `weather repository fetches from mock api and caches snapshot`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockApi = com.example.syntaxem.data.weather.MockWeatherApi(simulatedDelayMs = 0)
        val repo = com.example.syntaxem.data.weather.WeatherRepository(context, mockApi)

        kotlinx.coroutines.runBlocking {
            repo.clearCache()
            val result = repo.refreshWeather(28.6139, 77.2090, "New Delhi, Delhi NCR")

            assertTrue(result is com.example.syntaxem.data.weather.WeatherResult.Success)
            val success = result as com.example.syntaxem.data.weather.WeatherResult.Success
            assertFalse(success.isFromCache)
            assertFalse(success.isStale)
            assertTrue(success.data.temperature > 10.0 && success.data.temperature < 45.0)
            assertTrue(success.data.isAvailable)
            assertEquals("New Delhi, Delhi NCR", success.data.locationLabel)

            // Verify currentWeather StateFlow updated
            val current = repo.currentWeather.value
            assertNotNull(current)
            assertEquals(success.data.temperature, current!!.temperature, 0.01)
        }
    }

    @Test
    fun `weather repository serves cached data in offline mode when api fails`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockApi = com.example.syntaxem.data.weather.MockWeatherApi(simulatedDelayMs = 0)
        val repo = com.example.syntaxem.data.weather.WeatherRepository(context, mockApi)

        kotlinx.coroutines.runBlocking {
            // 1. Initial successful fetch to populate cache
            mockApi.setSimulateError(false)
            val initial = repo.refreshWeather(25.4358, 81.8463, "Prayagraj, Uttar Pradesh")
            assertTrue(initial is com.example.syntaxem.data.weather.WeatherResult.Success)
            val expectedTemp = (initial as com.example.syntaxem.data.weather.WeatherResult.Success).data.temperature

            // 2. Simulate offline error on mock API
            mockApi.setSimulateError(true, "Offline: Server unreachable")
            val offlineResult = repo.refreshWeather(25.4358, 81.8463, "Prayagraj, Uttar Pradesh")

            // 3. Must return Success with isFromCache = true and isStale = true
            assertTrue(offlineResult is com.example.syntaxem.data.weather.WeatherResult.Success)
            val cachedSuccess = offlineResult as com.example.syntaxem.data.weather.WeatherResult.Success
            assertTrue(cachedSuccess.isFromCache)
            assertTrue(cachedSuccess.isStale)
            assertEquals(expectedTemp, cachedSuccess.data.temperature, 0.01)

            // currentWeather StateFlow is also preserved as stale
            val current = repo.currentWeather.value
            assertNotNull(current)
            assertTrue(current!!.isStale)
            assertTrue(current.errorMessage?.contains("Offline") == true)
        }
    }

    @Test
    fun `weather repository returns offline error state when api fails and no cache exists`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockApi = com.example.syntaxem.data.weather.MockWeatherApi(simulatedDelayMs = 0)
        val repo = com.example.syntaxem.data.weather.WeatherRepository(context, mockApi)

        kotlinx.coroutines.runBlocking {
            // Clear cache completely
            repo.clearCache()

            // Simulate offline network failure
            mockApi.setSimulateError(true, "DNS resolution timeout")
            val result = repo.refreshWeather(26.9124, 75.7873, "Jaipur, Rajasthan")

            // Must return WeatherResult.Error with isOffline = true
            assertTrue(result is com.example.syntaxem.data.weather.WeatherResult.Error)
            val error = result as com.example.syntaxem.data.weather.WeatherResult.Error
            assertTrue(error.isOffline)
            assertTrue(error.message.contains("DNS resolution timeout"))
            assertNull(error.cachedData)

            // currentWeather StateFlow is marked unavailable
            val current = repo.currentWeather.value
            assertNotNull(current)
            assertFalse(current!!.isAvailable)
            assertTrue(current.isStale)
            assertEquals("Offline", current.condition)
        }
    }

    @Test
    fun `visual library pipeline validates structured diagram schema and detects invalid node references`() {
        val validDiagram = com.example.syntaxem.model.DiagramSchema(
            id = "schema_test_trig",
            title = "Trigonometric Ratios & Unit Circle",
            type = com.example.syntaxem.model.DiagramType.TOPIC_MAP,
            curriculumAuthority = "CBSE / NCERT",
            academicSession = "2026–27",
            classLevel = 11,
            subject = "Mathematics",
            chapterId = "ch_03_trigonometry",
            chapterName = "Trigonometric Functions",
            learningObjective = "Understand radian measure and ASTC quadrant rule",
            difficulty = com.example.syntaxem.model.DiagramDifficulty.STANDARD_CLASS_11,
            nodes = listOf(
                com.example.syntaxem.model.DiagramNodeData("n_root", "Trigonometric Functions", "Central concept", subnodes = listOf("n_ratios", "n_circle")),
                com.example.syntaxem.model.DiagramNodeData("n_ratios", "Trig Ratios", "sin, cos, tan", subnodes = emptyList()),
                com.example.syntaxem.model.DiagramNodeData("n_circle", "Unit Circle", "x = cos, y = sin", subnodes = emptyList())
            ),
            verificationStatus = com.example.syntaxem.model.VerificationStatus.VERIFIED_NCERT
        )

        val validRes = com.example.syntaxem.model.VisualLibraryPipeline.validateSchema(validDiagram)
        assertTrue(validRes is com.example.syntaxem.model.ValidationResult.Valid)
        val valid = validRes as com.example.syntaxem.model.ValidationResult.Valid
        assertEquals(3, valid.nodeCount)
        assertTrue(valid.fingerprint.isNotEmpty())

        // Diagram with broken reference
        val brokenDiagram = validDiagram.copy(
            nodes = listOf(
                com.example.syntaxem.model.DiagramNodeData("n_root", "Root", subnodes = listOf("n_nonexistent"))
            )
        )
        val brokenRes = com.example.syntaxem.model.VisualLibraryPipeline.validateSchema(brokenDiagram)
        assertTrue(brokenRes is com.example.syntaxem.model.ValidationResult.Invalid)
        val invalid = brokenRes as com.example.syntaxem.model.ValidationResult.Invalid
        assertTrue(invalid.errors.any { it.contains("non-existent child") })
    }

    @Test
    fun `visual library pipeline reports honest content counts without fabrication`() {
        val report = com.example.syntaxem.model.VisualLibraryPipeline.getContentCoverageReport()
        assertEquals(75, report.totalCreated)
        assertEquals(61, report.verifiedNcertCount)
        assertEquals(8, report.provisionalCount)
        assertEquals(6, report.pendingReviewCount)
        assertEquals(20000, report.targetGoal)
        assertTrue(report.verifiedClass11Subjects.contains("Mathematics (14 Ch)"))
        assertTrue(report.verifiedClass11Subjects.contains("Physics (14 Ch)"))
        assertTrue(report.verifiedClass11Subjects.contains("Chemistry (9 Ch)"))
    }

    @Test
    fun `onboarding preferences initializes sensible Class 11 defaults`() {
        val prefs = com.example.syntaxem.ui.onboarding.StudentProfilePreferences()
        assertEquals("2026–27", prefs.academicSession)
        assertEquals("Class 11", prefs.classLevel)
        assertEquals("Science (PCM)", prefs.stream)
        assertEquals(45, prefs.dailyGoalMinutes)
        assertTrue(prefs.weatherEnabled)
    }

    @Test
    fun `syntaxem database provider initializes database and daos for app module`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = com.example.syntaxem.data.local.SyntaxemDatabaseProvider.getDatabase(context)
        assertNotNull(db)

        val progressDao = com.example.syntaxem.data.local.SyntaxemDatabaseProvider.getChapterProgressDao(context)
        assertNotNull(progressDao)

        val syllabusDao = com.example.syntaxem.data.local.SyntaxemDatabaseProvider.getSyllabusDao(context)
        assertNotNull(syllabusDao)
    }

    @Test
    fun `syntaxem theme manager initializes with context and persists selection`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = com.example.core.designsystem.SyntaxemThemeManager
        manager.init(context)

        // Set to Cosmic Obsidian
        manager.setColorSet(com.example.core.designsystem.SyntaxemColorSet.COSMIC_OBSIDIAN)
        assertEquals(com.example.core.designsystem.SyntaxemColorSet.COSMIC_OBSIDIAN, manager.currentColorSet.value)
        assertEquals("Cosmic Obsidian", manager.currentPalette.value.name)
        assertTrue(manager.currentPalette.value.isDark)

        // Verify saved in SharedPreferences
        val prefs = context.getSharedPreferences("syntaxem_appearance_prefs", Context.MODE_PRIVATE)
        assertEquals(com.example.core.designsystem.SyntaxemColorSet.COSMIC_OBSIDIAN.name, prefs.getString("selected_color_set", null))

        // Switch to Light palette Warm Ivory
        manager.setColorSet(com.example.core.designsystem.SyntaxemColorSet.WARM_IVORY)
        assertEquals(com.example.core.designsystem.SyntaxemColorSet.WARM_IVORY, manager.currentColorSet.value)
        assertEquals("Warm Ivory", manager.currentPalette.value.name)
        assertFalse(manager.currentPalette.value.isDark)
        assertEquals(com.example.core.designsystem.SyntaxemColorSet.WARM_IVORY.name, prefs.getString("selected_color_set", null))

        // Reset to default Midnight Navy
        manager.setColorSet(com.example.core.designsystem.SyntaxemColorSet.MIDNIGHT_NAVY)
        assertEquals(com.example.core.designsystem.SyntaxemColorSet.MIDNIGHT_NAVY, manager.currentColorSet.value)
    }

    @Test
    fun `all syntaxem color sets map to valid palettes with preview tokens and color schemes`() {
        val allSets = com.example.core.designsystem.SyntaxemColorSet.values()
        assertEquals(6, allSets.size)

        for (set in allSets) {
            val palette = com.example.core.designsystem.SyntaxemPalette.forSet(set)
            assertEquals(set, palette.id)
            assertTrue(palette.name.isNotEmpty())
            assertTrue(palette.subtitle.isNotEmpty())
            assertEquals(5, palette.previewColors.size)

            val colorScheme = palette.toColorScheme()
            assertNotNull(colorScheme.background)
            assertNotNull(colorScheme.surface)
            assertNotNull(colorScheme.primary)
            assertNotNull(colorScheme.secondary)
            assertNotNull(colorScheme.tertiary)
        }
    }
}





