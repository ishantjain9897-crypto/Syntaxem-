package com.example.syntaxem.model

import androidx.compose.ui.graphics.Color
import com.example.core.designsystem.*

enum class NodeColorType {
    PERIWINKLE,
    MINT,
    AMBER,
    PINK,
    CYAN;

    val color: Color
        get() = when (this) {
            PERIWINKLE -> Color(0xFF6C6CF6)
            MINT -> Color(0xFF2ED8A7)
            AMBER -> Color(0xFFFDB043)
            PINK -> Color(0xFFFF6B8B)
            CYAN -> Color(0xFF00D2D3)
        }
}

data class MindMapNode(
    val id: String,
    val label: String,
    val colorType: NodeColorType,
    val summary: String,
    val keyFormulas: List<String> = emptyList()
)

data class FlowchartStep(
    val stepNumber: Int,
    val title: String,
    val action: String,
    val tip: String
)

data class ConceptCard(
    val title: String,
    val content: String,
    val formula: String? = null
)

data class ChapterGame(
    val id: String,
    val title: String,
    val mechanic: String, // Quiz Blitz, Formula Match, Step Builder, Graph Spotter, etc.
    val description: String,
    val colorType: NodeColorType
)

data class ChapterData(
    val id: String,
    val number: Int,
    val classLevel: Int, // 11 or 12
    val subject: String = "Mathematics",
    val title: String,
    val centralNodeLabel: String,
    val mindMapNodes: List<MindMapNode>,
    val flowchartSteps: List<FlowchartStep>,
    val conceptCards: List<ConceptCard>,
    val formulaSheet: List<Pair<String, String>>,
    val games: List<ChapterGame>,
    val simulationTitle: String,
    val simulationDescription: String
)

object SyntaxemCurriculumData {
    val chapters: List<ChapterData> = listOf(
        // CHAPTER 3: TRIGONOMETRIC FUNCTIONS (Class 11) - Featured in Image
        ChapterData(
            id = "ch_03_trigonometry",
            number = 3,
            classLevel = 11,
            title = "Trigonometric Functions",
            centralNodeLabel = "Trigono-\nmetry",
            mindMapNodes = listOf(
                MindMapNode(
                    id = "angles",
                    label = "Angles",
                    colorType = NodeColorType.MINT,
                    summary = "Angle measurement in radians and degrees: π radians = 180°. Arc length l = rθ.",
                    keyFormulas = listOf("1 rad = 180°/π ≈ 57°16'", "l = r · θ (θ in rad)")
                ),
                MindMapNode(
                    id = "ratios",
                    label = "Ratios",
                    colorType = NodeColorType.AMBER,
                    summary = "Fundamental trigonometric ratios for all real numbers and ASTC quadrant sign rules.",
                    keyFormulas = listOf("sin(x), cos(x), tan(x)", "Quadrant signs: All-Sin-Tan-Cos")
                ),
                MindMapNode(
                    id = "unit_circle",
                    label = "Unit circle",
                    colorType = NodeColorType.PINK,
                    summary = "Coordinates of terminal point on unit circle are (cos θ, sin θ). Extends ratios to any angle.",
                    keyFormulas = listOf("x = cos θ, y = sin θ", "cos² θ + sin² θ = 1")
                ),
                MindMapNode(
                    id = "identities",
                    label = "Identities",
                    colorType = NodeColorType.MINT,
                    summary = "Compound angle, multiple angle, and transformation formulas.",
                    keyFormulas = listOf("sin(A ± B) = sin A cos B ± cos A sin B", "cos 2A = cos² A - sin² A", "tan 2A = 2 tan A / (1 - tan² A)")
                ),
                MindMapNode(
                    id = "equations",
                    label = "Equations",
                    colorType = NodeColorType.AMBER,
                    summary = "Principal and general solutions of trigonometric equations.",
                    keyFormulas = listOf("sin x = sin α ⇒ x = nπ + (-1)ⁿα", "cos x = cos α ⇒ x = 2nπ ± α")
                ),
                MindMapNode(
                    id = "graphs",
                    label = "Graphs",
                    colorType = NodeColorType.PINK,
                    summary = "Periodic waveforms, amplitude, period, and phase shifts for sin, cos, and tan.",
                    keyFormulas = listOf("Period of sin, cos = 2π", "Period of tan = π")
                )
            ),
            flowchartSteps = listOf(
                FlowchartStep(1, "Identify Equation Form", "Inspect whether equation involves single ratio or mixed ratios.", "Check if sin, cos, or tan is isolated."),
                FlowchartStep(2, "Reduce to One Ratio", "Use compound or double-angle identities to express in a single trigonometric function.", "e.g., substitute cos 2x = 1 - 2 sin² x."),
                FlowchartStep(3, "Find Principal Value", "Determine principal angle α lying in fundamental domain.", "For sin x, α ∈ [-π/2, π/2]; for cos x, α ∈ [0, π]."),
                FlowchartStep(4, "Write General Solution", "Apply general solution formulas with integer parameter n ∈ ℤ.", "x = nπ + (-1)ⁿα for sin, or 2nπ ± α for cos.")
            ),
            conceptCards = listOf(
                ConceptCard("Degree & Radian Measure", "An angle subtended at the center of a circle of radius r by an arc of length l has radian measure θ = l/r. Conversion: 180° = π rad.", "1 rad = (180/π)°"),
                ConceptCard("Unit Circle Definition", "Let P(x, y) be the point where the terminal arm of angle θ intersects x² + y² = 1. Then x = cos θ, y = sin θ, and y/x = tan θ.", "cos² θ + sin² θ = 1"),
                ConceptCard("Quadrant Sign Conventions (ASTC)", "Quadrant I: All positive. Quadrant II: Sine positive. Quadrant III: Tangent positive. Quadrant IV: Cosine positive.", "Add Sugar To Coffee"),
                ConceptCard("Sum and Product Formulas", "Transform sums into products: sin A + sin B = 2 sin((A+B)/2) cos((A-B)/2).", "cos A + cos B = 2 cos((A+B)/2) cos((A-B)/2)")
            ),
            formulaSheet = listOf(
                "Pythagorean Identity" to "sin² θ + cos² θ = 1",
                "Secant Identity" to "1 + tan² θ = sec² θ",
                "Cosecant Identity" to "1 + cot² θ = csc² θ",
                "Sine of Sum" to "sin(A + B) = sin A cos B + cos A sin B",
                "Cosine of Sum" to "cos(A + B) = cos A cos B - sin A sin B",
                "Tangent of Sum" to "tan(A + B) = (tan A + tan B) / (1 - tan A tan B)",
                "Double Angle Sine" to "sin 2θ = 2 sin θ cos θ",
                "Double Angle Cosine" to "cos 2θ = cos² θ - sin² θ = 2 cos² θ - 1 = 1 - 2 sin² θ"
            ),
            games = listOf(
                ChapterGame("unit_circle_race", "Unit Circle Race", "Speed Calc", "Beat the clock: place angles and read sin, cos, tan coordinates.", NodeColorType.MINT),
                ChapterGame("formula_match_trig", "Formula Match", "Formula Match", "Pair trigonometric identities fast before time expires.", NodeColorType.PERIWINKLE),
                ChapterGame("graph_spotter_trig", "Graph Spotter", "Graph Spotter", "Identify the trigonometric curve from amplitude and phase shifts.", NodeColorType.AMBER),
                ChapterGame("equation_puzzle_trig", "Equation Puzzle", "Puzzle Unlock", "Solve trigonometric equations to unlock subsequent stages.", NodeColorType.PINK)
            ),
            simulationTitle = "Unit Circle & Waveform Generator",
            simulationDescription = "A point moving on the unit circle draws live sine and cosine waves with interactive frequency and amplitude sliders."
        ),

        // CHAPTER 1: SETS (Class 11)
        ChapterData(
            id = "ch_01_sets",
            number = 1,
            classLevel = 11,
            title = "Sets",
            centralNodeLabel = "Sets &\nVenn",
            mindMapNodes = listOf(
                MindMapNode("representation", "Roster/Set-builder", NodeColorType.MINT, "Listing elements vs defining property."),
                MindMapNode("types", "Types of Sets", NodeColorType.AMBER, "Empty, finite, infinite, equal, and singleton sets."),
                MindMapNode("subsets", "Subsets & Power", NodeColorType.PINK, "A ⊆ B; Power set P(A) contains 2ⁿ elements."),
                MindMapNode("operations", "Union & Intersect", NodeColorType.CYAN, "A ∪ B, A ∩ B, and relative difference A - B."),
                MindMapNode("laws", "De Morgan's Laws", NodeColorType.PERIWINKLE, "(A ∪ B)′ = A′ ∩ B′ and (A ∩ B)′ = A′ ∪ B′."),
                MindMapNode("venn_app", "Venn Cardinality", NodeColorType.MINT, "n(A ∪ B) = n(A) + n(B) - n(A ∩ B).")
            ),
            flowchartSteps = listOf(
                FlowchartStep(1, "Define Sets", "Identify universal set U and subsets from problem statement.", "Assign variables to disjoint regions."),
                FlowchartStep(2, "Draw Venn Diagram", "Sketch overlapping circles for 2 or 3 sets.", "Fill intersection region first."),
                FlowchartStep(3, "Apply Cardinality", "Use n(A∪B) = n(A) + n(B) - n(A∩B) formula.", "Check for complement regions."),
                FlowchartStep(4, "Solve Unknowns", "Equate known quantities and calculate target subset.", "Verify sum equals universal set.")
            ),
            conceptCards = listOf(
                ConceptCard("Set Definition", "A set is a well-defined collection of distinct objects.", "A = {x : P(x)}"),
                ConceptCard("Power Set", "The collection of all subsets of set A is called the power set P(A).", "n(P(A)) = 2ⁿ"),
                ConceptCard("De Morgan's Laws", "The complement of the union equals the intersection of the complements.", "(A ∪ B)′ = A′ ∩ B′")
            ),
            formulaSheet = listOf(
                "Cardinality of 2 sets" to "n(A ∪ B) = n(A) + n(B) - n(A ∩ B)",
                "Disjoint sets" to "n(A ∪ B) = n(A) + n(B) [if A ∩ B = ∅]",
                "De Morgan 1" to "(A ∪ B)′ = A′ ∩ B′",
                "De Morgan 2" to "(A ∩ B)′ = A′ ∪ B′"
            ),
            games = listOf(
                ChapterGame("set_sorter", "Set Sorter", "Drag & Drop", "Classify elements into correct set partitions.", NodeColorType.MINT),
                ChapterGame("venn_painter", "Venn Painter", "Shade Region", "Shade the queried Boolean set region accurately.", NodeColorType.CYAN),
                ChapterGame("de_morgan_duel", "De Morgan Duel", "Formula Match", "Pair complementary set expressions rapidly.", NodeColorType.PERIWINKLE),
                ChapterGame("power_set_counter", "Power Set Counter", "Speed Calc", "Calculate subset counts for various set sizes.", NodeColorType.AMBER)
            ),
            simulationTitle = "Interactive Venn Diagram Playground",
            simulationDescription = "Drag set circles to see live visual union, intersection, and relative complement updates."
        ),

        // CHAPTER 2: RELATIONS & FUNCTIONS (Class 11)
        ChapterData(
            id = "ch_02_relations_functions",
            number = 2,
            classLevel = 11,
            title = "Relations and Functions",
            centralNodeLabel = "Relations &\nFunctions",
            mindMapNodes = listOf(
                MindMapNode("cartesian", "Cartesian Product", NodeColorType.MINT, "A × B = {(a, b) : a ∈ A, b ∈ B}."),
                MindMapNode("relations", "Relations", NodeColorType.AMBER, "A relation R from A to B is a subset of A × B."),
                MindMapNode("functions", "Functions", NodeColorType.PINK, "Every element in domain has exactly one image."),
                MindMapNode("domain_range", "Domain & Range", NodeColorType.CYAN, "Inputs (domain) and generated outputs (range)."),
                MindMapNode("special_funcs", "Special Functions", NodeColorType.PERIWINKLE, "Modulus, greatest integer, signum, identity."),
                MindMapNode("func_graphs", "Function Graphs", NodeColorType.MINT, "Vertical line test to confirm functional mapping.")
            ),
            flowchartSteps = listOf(
                FlowchartStep(1, "Test Function Validity", "Verify each input in domain maps to unique output.", "Vertical line test: intersects at most once."),
                FlowchartStep(2, "Determine Domain", "Identify inputs where function denominator ≠ 0 and radicand ≥ 0.", "Exclude non-real outputs."),
                FlowchartStep(3, "Calculate Range", "Solve for input x in terms of y and find valid y values.", "Analyze boundary behavior."),
                FlowchartStep(4, "Sketch Curve", "Plot intercepts, symmetry, and asymptotes.", "Verify special function behavior.")
            ),
            conceptCards = listOf(
                ConceptCard("Function Condition", "A relation f: A → B is a function if every element of A has one and only one image in B.", "f(x) = y"),
                ConceptCard("Greatest Integer Function", "[x] denotes the greatest integer less than or equal to x.", "[-1.5] = -2, [2.7] = 2"),
                ConceptCard("Modulus Function", "|x| = x if x ≥ 0; |x| = -x if x < 0.", "|x| ≥ 0")
            ),
            formulaSheet = listOf(
                "Cartesian Cardinality" to "n(A × B) = n(A) · n(B)",
                "Number of Relations" to "Total relations = 2^(n(A)·n(B))",
                "Signum Function" to "sgn(x) = 1 (x>0), 0 (x=0), -1 (x<0)"
            ),
            games = listOf(
                ChapterGame("func_or_not", "Function or Not", "Quiz Blitz", "Test mappings for function rule violations.", NodeColorType.MINT),
                ChapterGame("domain_detective", "Domain Detective", "Puzzle Unlock", "Find allowable domain intervals for complex fractions.", NodeColorType.AMBER),
                ChapterGame("range_hunter", "Range Hunter", "Speed Calc", "Identify output range for quadratic and radical rules.", NodeColorType.PINK),
                ChapterGame("graph_spotter_funcs", "Graph Spotter", "Graph Spotter", "Match modulus, step, and signum curves to formulas.", NodeColorType.PERIWINKLE)
            ),
            simulationTitle = "Arrow Mapping Diagram",
            simulationDescription = "Connect inputs to outputs and observe real-time validation of function constraints."
        ),

        // CHAPTER 4: COMPLEX NUMBERS & QUADRATICS (Class 11)
        ChapterData(
            id = "ch_04_complex_numbers",
            number = 4,
            classLevel = 11,
            title = "Complex Numbers and Quadratic Equations",
            centralNodeLabel = "Complex &\nArgand",
            mindMapNodes = listOf(
                MindMapNode("imaginary_unit", "Powers of i", NodeColorType.MINT, "i = √(-1), i² = -1, i³ = -i, i⁴ = 1."),
                MindMapNode("complex_algebra", "Algebra (a + bi)", NodeColorType.AMBER, "Addition, multiplication, and multiplicative inverse."),
                MindMapNode("modulus_conjugate", "Modulus & Conjugate", NodeColorType.PINK, "|z| = √(a² + b²), z̄ = a - bi. z · z̄ = |z|²."),
                MindMapNode("argand_plane", "Argand Plane", NodeColorType.CYAN, "Geometric representation: real axis and imaginary axis."),
                MindMapNode("polar_form", "Polar Form", NodeColorType.PERIWINKLE, "z = r(cos θ + i sin θ), where r = |z|, θ = arg(z)."),
                MindMapNode("quad_roots", "Complex Roots", NodeColorType.MINT, "Quadratic ax² + bx + c = 0 with D < 0 yields complex conjugate roots.")
            ),
            flowchartSteps = listOf(
                FlowchartStep(1, "Compute Discriminant", "Calculate D = b² - 4ac.", "If D < 0, roots are complex numbers."),
                FlowchartStep(2, "Write Roots", "Express roots as x = (-b ± i√|D|) / (2a).", "Ensure real and imaginary parts are separated."),
                FlowchartStep(3, "Plot on Argand Plane", "Identify coordinate (Re(z), Im(z)) = (a, b).", "Plot vector from origin."),
                FlowchartStep(4, "Find Modulus & Argument", "Calculate r = √(a² + b²) and principal argument θ.", "Verify quadrant of (a, b).")
            ),
            conceptCards = listOf(
                ConceptCard("Imaginary Unit", "Defined as i = √(-1). Powers repeat in cycles of 4: i^(4k) = 1.", "i² = -1"),
                ConceptCard("Modulus and Conjugate", "For z = a + bi, conjugate is z̄ = a - bi and modulus is |z| = √(a² + b²).", "z · z̄ = |z|²"),
                ConceptCard("Polar Form", "Expressing z in terms of distance r and angle θ: z = r(cos θ + i sin θ).", "r = |z|, θ = arg(z)")
            ),
            formulaSheet = listOf(
                "Modulus" to "|z| = √(a² + b²)",
                "Conjugate" to "z̄ = a - bi",
                "Product Property" to "|z₁ · z₂| = |z₁| · |z₂|",
                "Quadratic Roots (D < 0)" to "x = (-b ± i√|b² - 4ac|) / (2a)"
            ),
            games = listOf(
                ChapterGame("power_of_i_spinner", "Power of i Spinner", "Speed Calc", "Simplify high powers of i within seconds.", NodeColorType.MINT),
                ChapterGame("conjugate_match", "Conjugate Match", "Formula Match", "Pair complex numbers with their conjugates and moduli.", NodeColorType.PERIWINKLE),
                ChapterGame("argand_plotter", "Argand Plotter", "Graph Spotter", "Plot complex vectors and identify quadrants.", NodeColorType.AMBER),
                ChapterGame("root_finder_complex", "Complex Root Finder", "Puzzle Unlock", "Solve quadratics with negative discriminants.", NodeColorType.PINK)
            ),
            simulationTitle = "Interactive Argand Plane Explorer",
            simulationDescription = "Drag points on the complex plane to inspect live modulus, argument, and rotational multiplication."
        ),

        // CHAPTER 9: STRAIGHT LINES (Class 11)
        ChapterData(
            id = "ch_09_straight_lines",
            number = 9,
            classLevel = 11,
            title = "Straight Lines",
            centralNodeLabel = "Straight\nLines",
            mindMapNodes = listOf(
                MindMapNode("slope", "Slope (m = tan θ)", NodeColorType.MINT, "m = (y₂ - y₁) / (x₂ - x₁). Parallel m₁ = m₂."),
                MindMapNode("forms", "Equation Forms", NodeColorType.AMBER, "Slope-intercept, point-slope, two-point, intercept form."),
                MindMapNode("distance", "Perpendicular Distance", NodeColorType.PINK, "Distance d = |Ax₁ + By₁ + C| / √(A² + B²)."),
                MindMapNode("angle_between", "Angle Between Lines", NodeColorType.CYAN, "tan θ = |(m₂ - m₁) / (1 + m₁ m₂)|."),
                MindMapNode("conditions", "Parallel & Perpendicular", NodeColorType.PERIWINKLE, "m₁ · m₂ = -1 for orthogonal lines."),
                MindMapNode("general_eq", "General Form", NodeColorType.MINT, "Ax + By + C = 0 with slope -A/B.")
            ),
            flowchartSteps = listOf(
                FlowchartStep(1, "Analyze Given Data", "Identify whether points, slope, or intercepts are specified.", "Choose matching equation form."),
                FlowchartStep(2, "Formulate Equation", "Substitute values into slope-intercept (y = mx+c) or point-slope form.", "Simplify into Ax + By + C = 0."),
                FlowchartStep(3, "Check Perpendicularity", "Verify if slope product m₁ · m₂ = -1.", "Parallel if m₁ = m₂."),
                FlowchartStep(4, "Compute Distances", "Apply perpendicular distance formula from target point.", "Verify sign inside modulus.")
            ),
            conceptCards = listOf(
                ConceptCard("Slope of a Line", "Slope m represents rate of vertical change over horizontal change: m = tan θ.", "m = (y₂ - y₁)/(x₂ - x₁)"),
                ConceptCard("Intercept Form", "A line intercepting x-axis at a and y-axis at b has equation x/a + y/b = 1.", "x/a + y/b = 1"),
                ConceptCard("Distance from Point", "The perpendicular distance from (x₀, y₀) to line Ax + By + C = 0.", "d = |Ax₀ + By₀ + C|/√(A² + B²)")
            ),
            formulaSheet = listOf(
                "Slope-Intercept" to "y = mx + c",
                "Point-Slope" to "y - y₁ = m(x - x₁)",
                "Intercept Form" to "x/a + y/b = 1",
                "Perpendicular Condition" to "m₁ · m₂ = -1",
                "Point-Line Distance" to "d = |Ax₁ + By₁ + C| / √(A² + B²)"
            ),
            games = listOf(
                ChapterGame("slope_surfer", "Slope Surfer", "Speed Calc", "Calculate line slope from coordinate pairs quickly.", NodeColorType.MINT),
                ChapterGame("equation_form_match", "Equation Form Match", "Formula Match", "Convert between general, intercept, and slope forms.", NodeColorType.PERIWINKLE),
                ChapterGame("distance_dash", "Distance Dash", "Puzzle Unlock", "Calculate perpendicular distances to target lines.", NodeColorType.AMBER),
                ChapterGame("parallel_perpendicular", "Parallel or Perpendicular", "Quiz Blitz", "Determine geometric relationships between line pairs.", NodeColorType.PINK)
            ),
            simulationTitle = "Interactive 2D Line & Slope Explorer",
            simulationDescription = "Drag two control points to see live updates to slope, intercepts, and standard Cartesian equation."
        ),

        // CHAPTER 17: MATRICES (Class 12)
        ChapterData(
            id = "ch_17_matrices",
            number = 17,
            classLevel = 12,
            title = "Matrices",
            centralNodeLabel = "Matrix\nAlgebra",
            mindMapNodes = listOf(
                MindMapNode("matrix_types", "Types of Matrices", NodeColorType.MINT, "Row, column, square, diagonal, scalar, identity, zero."),
                MindMapNode("operations", "Matrix Operations", NodeColorType.AMBER, "Addition, scalar multiplication, and matrix product AB."),
                MindMapNode("transpose", "Transpose (Aᵀ)", NodeColorType.PINK, "Interchanging rows and columns: (AB)ᵀ = Bᵀ Aᵀ."),
                MindMapNode("symmetric", "Symmetric & Skew", NodeColorType.CYAN, "Aᵀ = A (symmetric), Aᵀ = -A (skew-symmetric)."),
                MindMapNode("elementary", "Elementary Ops", NodeColorType.PERIWINKLE, "Row and column transformations to compute inverse."),
                MindMapNode("inverse", "Invertible Matrices", NodeColorType.MINT, "AB = BA = I implies B = A⁻¹.")
            ),
            flowchartSteps = listOf(
                FlowchartStep(1, "Check Order Compatibility", "For A(m×n) and B(n×p), verify columns of A equal rows of B.", "Product order is m×p."),
                FlowchartStep(2, "Compute Dot Products", "Multiply each row of A by each column of B.", "c_ij = ∑ a_ik · b_kj."),
                FlowchartStep(3, "Check Transpose & Symmetry", "Compute Aᵀ. Check if Aᵀ = A or Aᵀ = -A.", "Diagonal of skew-symmetric is all zeros."),
                FlowchartStep(4, "Find Inverse via Gauss-Jordan", "Set up [A | I] and apply elementary row operations until [I | A⁻¹].", "Verify A · A⁻¹ = I.")
            ),
            conceptCards = listOf(
                ConceptCard("Matrix Multiplication", "Matrix multiplication is associative but NOT commutative in general: AB ≠ BA.", "(AB)C = A(BC)"),
                ConceptCard("Transpose Properties", "Transpose reverses product order: (AB)ᵀ = Bᵀ Aᵀ.", "(A + B)ᵀ = Aᵀ + Bᵀ"),
                ConceptCard("Symmetric Decomposition", "Any square matrix A can be uniquely written as the sum of a symmetric and a skew-symmetric matrix: A = ½(A + Aᵀ) + ½(A - Aᵀ).", "A = P + Q")
            ),
            formulaSheet = listOf(
                "Order of Product" to "A(m×n) · B(n×p) = C(m×p)",
                "Transpose of Product" to "(AB)ᵀ = Bᵀ · Aᵀ",
                "Symmetric Condition" to "Aᵀ = A",
                "Skew-Symmetric Condition" to "Aᵀ = -A (a_ii = 0)",
                "Inverse Definition" to "A · A⁻¹ = A⁻¹ · A = I"
            ),
            games = listOf(
                ChapterGame("order_checker", "Order Checker", "Quiz Blitz", "Test matrix multiplication compatibility instantly.", NodeColorType.MINT),
                ChapterGame("matrix_multiply_maze", "Matrix Multiply Maze", "Step Builder", "Compute element dot products across dimensions.", NodeColorType.AMBER),
                ChapterGame("symmetric_or_not", "Symmetric or Not", "Quiz Blitz", "Identify symmetry and skew-symmetry from entries.", NodeColorType.PINK),
                ChapterGame("transpose_flip", "Transpose Flip", "Speed Calc", "Flip row and column indices accurately.", NodeColorType.PERIWINKLE)
            ),
            simulationTitle = "2×2 Linear Matrix Transformation Visualizer",
            simulationDescription = "Edit 2×2 matrix values to observe real-time shearing, rotation, scaling, and reflection of shapes."
        )
    )

    fun getChapter(id: String): ChapterData? = chapters.find { it.id == id }
}
