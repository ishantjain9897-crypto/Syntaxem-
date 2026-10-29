package com.example.syntaxem.data.local

import com.example.syntaxem.data.local.entity.ChapterEntity
import com.example.syntaxem.data.local.entity.CurriculumEntity
import com.example.syntaxem.data.local.entity.SubjectEntity

/**
 * Bundled syllabus templates for CBSE/NCERT and Vidya Bharati (Provisional).
 */
object BundledCurriculumData {

    val defaultCurriculum = CurriculumEntity(
        id = "curr_cbse_11_science_2026",
        authority = "CBSE / NCERT",
        schoolVariant = "All-India Standard Senior Secondary",
        academicSession = "2026–27",
        classLevel = 11,
        stream = "Science (PCM / PCB)",
        medium = "English & Hindi Medium",
        version = "2026.1.0",
        verificationStatus = "VERIFIED",
        sourceReference = "NCERT Curriculum Guidelines & Rationalised Syllabus 2026–27",
        isSelected = true
    )

    val vidyaBharatiProvisionalCurriculum = CurriculumEntity(
        id = "curr_vidya_bharati_11_2026",
        authority = "Vidya Bharati (Provisional)",
        schoolVariant = "Vidya Bharati Akhil Bharatiya Shiksha Sansthan",
        academicSession = "2026–27",
        classLevel = 11,
        stream = "Science",
        medium = "English & Hindi Medium",
        version = "2026.0.9-prov",
        verificationStatus = "PROVISIONAL",
        sourceReference = "Vidya Bharati Senior Secondary Academic Schema (Provisional - editable by student)",
        isSelected = false
    )

    val cbseClass12Curriculum = CurriculumEntity(
        id = "curr_cbse_12_science_2026",
        authority = "CBSE / NCERT",
        schoolVariant = "All-India Standard Senior Secondary",
        academicSession = "2026–27",
        classLevel = 12,
        stream = "Science (PCM / PCB)",
        medium = "English & Hindi Medium",
        version = "2026.1.0",
        verificationStatus = "VERIFIED",
        sourceReference = "NCERT Board Examination Syllabus 2026–27",
        isSelected = false
    )

    fun getSubjects(curriculumId: String): List<SubjectEntity> {
        return when (curriculumId) {
            "curr_cbse_12_science_2026" -> listOf(
                SubjectEntity("sub_12_math", curriculumId, "Mathematics", "MATH-041", "calculate", "#6C6CF6", 1),
                SubjectEntity("sub_12_phys", curriculumId, "Physics", "PHYS-042", "science", "#2ED8A7", 2),
                SubjectEntity("sub_12_chem", curriculumId, "Chemistry", "CHEM-043", "biotech", "#FDB043", 3)
            )
            else -> listOf(
                SubjectEntity("sub_11_math", curriculumId, "Mathematics", "MATH-041", "calculate", "#6C6CF6", 1),
                SubjectEntity("sub_11_phys", curriculumId, "Physics", "PHYS-042", "science", "#2ED8A7", 2),
                SubjectEntity("sub_11_chem", curriculumId, "Chemistry", "CHEM-043", "biotech", "#FDB043", 3)
            )
        }
    }

    fun getChapters(subjectId: String): List<ChapterEntity> {
        return when (subjectId) {
            "sub_11_math" -> listOf(
                ChapterEntity("ch_11_m_01", subjectId, 1, "Sets", "Representation of sets, subsets, power sets, Venn diagrams, operations on sets", true, 1),
                ChapterEntity("ch_11_m_02", subjectId, 2, "Relations & Functions", "Cartesian products, domain, co-domain, range, real valued functions", true, 2),
                ChapterEntity("ch_11_m_03", subjectId, 3, "Trigonometric Functions", "Radian measure, trigonometric equations, identities, unit circle", true, 3),
                ChapterEntity("ch_11_m_04", subjectId, 4, "Complex Numbers & Quadratic Equations", "Algebra of complex numbers, modulus, argument, quadratic roots in ℂ", true, 4),
                ChapterEntity("ch_11_m_05", subjectId, 5, "Linear Inequalities", "Algebraic and graphical solutions in one and two variables", true, 5),
                ChapterEntity("ch_11_m_06", subjectId, 6, "Permutations & Combinations", "Fundamental principle of counting, factorial, nPr and nCr theorems", true, 6),
                ChapterEntity("ch_11_m_07", subjectId, 7, "Binomial Theorem", "Expansion for positive integral indices, general and middle terms", true, 7),
                ChapterEntity("ch_11_m_08", subjectId, 8, "Sequences & Series", "Arithmetic progressions, Geometric progressions, infinite GP sums", true, 8),
                ChapterEntity("ch_11_m_09", subjectId, 9, "Straight Lines", "Slope of a line, various forms of equations of line, distance of a point", true, 9),
                ChapterEntity("ch_11_m_10", subjectId, 10, "Conic Sections", "Standard equations of circle, parabola, ellipse, and hyperbola", true, 10),
                ChapterEntity("ch_11_m_11", subjectId, 11, "Introduction to 3D Geometry", "Coordinate axes and planes in 3D, distance formula, section formula", true, 11),
                ChapterEntity("ch_11_m_12", subjectId, 12, "Limits & Derivatives", "Intuitive idea of limits, standard limits, derivative as rate of change", true, 12),
                ChapterEntity("ch_11_m_13", subjectId, 13, "Statistics", "Measures of dispersion, mean deviation, variance and standard deviation", true, 13),
                ChapterEntity("ch_11_m_14", subjectId, 14, "Probability", "Random experiments, outcomes, axiomatic approach to probability", true, 14)
            )
            "sub_11_phys" -> listOf(
                ChapterEntity("ch_11_p_01", subjectId, 1, "Units & Measurements", "SI units, dimensions of physical quantities, dimensional analysis and errors", true, 1),
                ChapterEntity("ch_11_p_02", subjectId, 2, "Motion in a Straight Line", "Frame of reference, velocity, acceleration, kinematic equations for uniform motion", true, 2),
                ChapterEntity("ch_11_p_03", subjectId, 3, "Motion in a Plane", "Scalar and vector quantities, projectile motion, uniform circular motion", true, 3),
                ChapterEntity("ch_11_p_04", subjectId, 4, "Laws of Motion", "Newton's laws of motion, inertia, momentum, impulse, friction and circular dynamics", true, 4),
                ChapterEntity("ch_11_p_05", subjectId, 5, "Work, Energy & Power", "Work-energy theorem, conservative forces, kinetic & potential energy, collisions", true, 5),
                ChapterEntity("ch_11_p_06", subjectId, 6, "System of Particles & Rotational Motion", "Centre of mass, torque, angular momentum, conservation, moment of inertia", true, 6),
                ChapterEntity("ch_11_p_07", subjectId, 7, "Gravitation", "Kepler's laws, universal gravitation, acceleration due to gravity, escape velocity", true, 7),
                ChapterEntity("ch_11_p_08", subjectId, 8, "Mechanical Properties of Solids", "Stress-strain relationship, Hooke's law, Young's modulus, shear modulus", true, 8),
                ChapterEntity("ch_11_p_09", subjectId, 9, "Mechanical Properties of Fluids", "Pascal's law, viscosity, Stokes' law, Bernoulli's theorem, surface tension", true, 9),
                ChapterEntity("ch_11_p_10", subjectId, 10, "Thermal Properties of Matter", "Heat, temperature, thermal expansion, specific heat, Newton's law of cooling", true, 10),
                ChapterEntity("ch_11_p_11", subjectId, 11, "Thermodynamics", "Zeroth, first and second laws, isothermal & adiabatic processes, heat engines", true, 11),
                ChapterEntity("ch_11_p_12", subjectId, 12, "Kinetic Theory of Gases", "Equation of state, assumptions, rms velocity, degrees of freedom, equipartition", true, 12),
                ChapterEntity("ch_11_p_13", subjectId, 13, "Oscillations", "Periodic motion, simple harmonic motion (SHM), kinetic and potential energy", true, 13),
                ChapterEntity("ch_11_p_14", subjectId, 14, "Waves", "Transverse and longitudinal waves, speed, principle of superposition, Doppler effect", true, 14)
            )
            "sub_11_chem" -> listOf(
                ChapterEntity("ch_11_c_01", subjectId, 1, "Some Basic Concepts of Chemistry", "Atomic and molecular masses, mole concept, molar mass, stoichiometry", true, 1),
                ChapterEntity("ch_11_c_02", subjectId, 2, "Structure of Atom", "Bohr model, de Broglie relation, Heisenberg uncertainty, quantum numbers", true, 2),
                ChapterEntity("ch_11_c_03", subjectId, 3, "Classification of Elements & Periodicity", "Modern periodic law, periodic trends in ionization enthalpy, electron gain", true, 3),
                ChapterEntity("ch_11_c_04", subjectId, 4, "Chemical Bonding & Molecular Structure", "Valence electrons, ionic & covalent bond, VSEPR theory, hybridization", true, 4),
                ChapterEntity("ch_11_c_05", subjectId, 5, "Chemical Thermodynamics", "First law, enthalpy, heat capacity, Hess's law, entropy, Gibbs energy", true, 5),
                ChapterEntity("ch_11_c_06", subjectId, 6, "Equilibrium", "Equilibrium in physical & chemical processes, Le Chatelier's principle, pH scale", true, 6),
                ChapterEntity("ch_11_c_07", subjectId, 7, "Redox Reactions", "Oxidation and reduction, oxidation number, balancing redox reactions", true, 7),
                ChapterEntity("ch_11_c_08", subjectId, 8, "Organic Chemistry: Some Basic Principles", "Nomenclature, electronic displacements, inductive effect, reaction mechanisms", true, 8),
                ChapterEntity("ch_11_c_09", subjectId, 9, "Hydrocarbons", "Alkanes, alkenes, alkynes, aromatic hydrocarbons, IUPAC nomenclature", true, 9)
            )
            "sub_12_math" -> listOf(
                ChapterEntity("ch_12_m_01", subjectId, 1, "Relations & Functions", "Reflexive, symmetric, transitive relations, bijective functions", true, 1),
                ChapterEntity("ch_12_m_02", subjectId, 2, "Inverse Trigonometric Functions", "Definition, range, domain, principal value branches", true, 2),
                ChapterEntity("ch_12_m_03", subjectId, 3, "Matrices", "Matrix operations, transpose, symmetric, invertible matrices", true, 3),
                ChapterEntity("ch_12_m_04", subjectId, 4, "Determinants", "Minors, cofactors, applications to areas, matrix inverse", true, 4),
                ChapterEntity("ch_12_m_05", subjectId, 5, "Continuity & Differentiability", "Chain rule, exponential, logarithmic differentiation", true, 5),
                ChapterEntity("ch_12_m_06", subjectId, 6, "Applications of Derivatives", "Rate of change, increasing/decreasing, maxima and minima", true, 6),
                ChapterEntity("ch_12_m_07", subjectId, 7, "Integrals", "Indefinite & definite integrals, fundamental theorem of calculus", true, 7),
                ChapterEntity("ch_12_m_08", subjectId, 8, "Applications of Integrals", "Area under simple curves, area between two curves", true, 8),
                ChapterEntity("ch_12_m_09", subjectId, 9, "Differential Equations", "Order, degree, general and particular solutions, separable", true, 9),
                ChapterEntity("ch_12_m_10", subjectId, 10, "Vector Algebra", "Vectors, scalars, dot product, cross product, projection", true, 10),
                ChapterEntity("ch_12_m_11", subjectId, 11, "Three Dimensional Geometry", "Direction cosines, line equation in space, shortest distance", true, 11),
                ChapterEntity("ch_12_m_12", subjectId, 12, "Linear Programming", "Mathematical formulation, graphical method, feasible region", true, 12),
                ChapterEntity("ch_12_m_13", subjectId, 13, "Probability", "Conditional probability, multiplication theorem, Bayes' theorem", true, 13)
            )
            else -> emptyList()
        }
    }
}
