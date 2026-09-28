// ============================================================
// CARBONTRACK FRONTEND
// Connects the HTML UI to the Spring Boot REST API
// ============================================================

const API = "/api";

// Your existing household
const HOUSEHOLD_ID = 1;

// Current dashboard month
const YEAR = 2026;
const MONTH = 9;


// ============================================================
// START
// ============================================================

document.addEventListener("DOMContentLoaded", function () {

    // Set today's date in activity form
    document.getElementById("activityDate").value =
        new Date().toISOString().split("T")[0];

    // Load backend data
    loadDashboard();

    // Activity form
    document
        .getElementById("activityForm")
        .addEventListener("submit", submitActivity);
});


// ============================================================
// NAVIGATION
// ============================================================

function showSection(sectionName) {

    // Hide every section
    document
        .querySelectorAll(".section")
        .forEach(section => {
            section.classList.remove("active");
        });

    // Show selected section
    document
        .getElementById(sectionName)
        .classList.add("active");

    // Load relevant data
    if (sectionName === "dashboard") {
        loadDashboard();
    }

    if (sectionName === "activities") {
        loadActivities();
    }

    if (sectionName === "challenges") {
        loadChallenges();
    }

    if (sectionName === "leaderboard") {
        loadLeaderboard();
    }
}


// ============================================================
// DASHBOARD
// ============================================================

async function loadDashboard() {

    try {

        const monthly = await get(
            `/carbon/household/${HOUSEHOLD_ID}/monthly?year=${YEAR}&month=${MONTH}`
        );

        const reduction = await get(
            `/carbon/household/${HOUSEHOLD_ID}/reduction?year=${YEAR}&month=${MONTH}`
        );

        const activities = await get(
            `/activities/household/${HOUSEHOLD_ID}`
        );

        const leaderboard = await get(
            `/leaderboard?year=${YEAR}&month=${MONTH}`
        );


        // --------------------------------------------
        // Monthly carbon
        // --------------------------------------------

        document.getElementById("monthlyCarbon").textContent =
            Number(monthly.monthlyCarbonKg || 0).toFixed(2);


        // --------------------------------------------
        // Reduction
        // --------------------------------------------

        document.getElementById("reduction").textContent =
            Number(reduction.percentageReduction || 0).toFixed(2) + "%";


        // --------------------------------------------
        // Electricity from latest activity
        // --------------------------------------------

        if (activities.length > 0) {

            const latest =
                [...activities]
                    .sort((a, b) =>
                        String(b.date)
                            .localeCompare(String(a.date))
                    )[0];

            document.getElementById("electricity").textContent =
                Number(
                    latest.electricityConsumption || 0
                ).toFixed(1);

        } else {

            document.getElementById("electricity").textContent =
                "--";
        }


        // --------------------------------------------
        // Leaderboard rank
        // --------------------------------------------

        const householdEntry =
            leaderboard.find(
                item =>
                    Number(item.householdId) === HOUSEHOLD_ID
            );

        if (householdEntry) {

            document.getElementById("rank").textContent =
                "#" + householdEntry.rank;

        } else {

            document.getElementById("rank").textContent =
                "--";
        }


        // --------------------------------------------
        // Challenge progress
        // --------------------------------------------

        document.getElementById("challengeProgress").textContent =
            Number(
                reduction.percentageReduction || 0
            ).toFixed(2) + "%";


        // --------------------------------------------
        // Recent activities
        // --------------------------------------------

        renderRecentActivities(activities);

    } catch (error) {

        console.error(error);

        showBackendError();
    }
}


// ============================================================
// ACTIVITIES
// ============================================================

async function loadActivities() {

    try {

        const activities =
            await get(
                `/activities/household/${HOUSEHOLD_ID}`
            );

        renderActivityTable(activities);

    } catch (error) {

        console.error(error);

        document.getElementById("activityTable").innerHTML =
            `<p class="muted">
                Unable to load activities.
            </p>`;
    }
}


// ============================================================
// RENDER ACTIVITY TABLE
// ============================================================

function renderActivityTable(activities) {

    if (!activities || activities.length === 0) {

        document.getElementById("activityTable").innerHTML =
            `<p class="muted">
                No activities recorded yet.
            </p>`;

        return;
    }


    const sorted =
        [...activities].sort((a, b) =>
            String(b.date)
                .localeCompare(String(a.date))
        );


    let html = `
        <table>

            <thead>

                <tr>

                    <th>Date</th>

                    <th>Commute</th>

                    <th>Distance</th>

                    <th>Electricity</th>

                    <th>Waste</th>

                    <th>Estimated CO₂</th>

                </tr>

            </thead>

            <tbody>
    `;


    sorted.forEach(activity => {

        const carbon =
            calculateDisplayCarbon(activity);


        html += `

            <tr>

                <td>
                    ${activity.date || "--"}
                </td>

                <td>
                    ${getCommuteIcon(activity.commuteMode)}
                    ${activity.commuteMode || "--"}
                </td>

                <td>
                    ${Number(
                        activity.commuteDistanceKm || 0
                    ).toFixed(1)} km
                </td>

                <td>
                    ${Number(
                        activity.electricityConsumption || 0
                    ).toFixed(1)} kWh
                </td>

                <td>
                    ${Number(
                        activity.wasteGenerated || 0
                    ).toFixed(1)} kg
                </td>

                <td>
                    <strong>
                        ${carbon.toFixed(2)} kg
                    </strong>
                </td>

            </tr>
        `;
    });


    html += `
            </tbody>

        </table>
    `;


    document.getElementById("activityTable").innerHTML =
        html;
}


// ============================================================
// RECENT ACTIVITIES
// ============================================================

function renderRecentActivities(activities) {

    const container =
        document.getElementById("recentActivities");


    if (!activities || activities.length === 0) {

        container.innerHTML =
            `<p class="muted">
                No activities recorded yet.
            </p>`;

        return;
    }


    const recent =
        [...activities]
            .sort((a, b) =>
                String(b.date)
                    .localeCompare(String(a.date))
            )
            .slice(0, 4);


    let html = "";


    recent.forEach(activity => {

        const icon =
            getCommuteIcon(activity.commuteMode);

        const carbon =
            calculateDisplayCarbon(activity);


        html += `

            <div class="activity-row">

                <div class="activity-icon">
                    ${icon}
                </div>

                <div>

                    <strong>
                        ${activity.commuteMode}
                        ·
                        ${Number(
                            activity.commuteDistanceKm || 0
                        ).toFixed(1)}
                        km
                    </strong>

                    <span>
                        ${activity.date}
                        ·
                        ${Number(
                            activity.electricityConsumption || 0
                        ).toFixed(1)}
                        kWh
                        ·
                        ${Number(
                            activity.wasteGenerated || 0
                        ).toFixed(1)}
                        kg waste
                    </span>

                </div>

                <div class="activity-carbon">

                    ${carbon.toFixed(2)} kg

                </div>

            </div>
        `;
    });


    container.innerHTML = html;
}


// ============================================================
// CHALLENGES
// ============================================================

async function loadChallenges() {

    try {

        const challenges =
            await get("/challenges");

        renderChallenges(challenges);

    } catch (error) {

        console.error(error);

        document.getElementById("challengeCards").innerHTML =
            `<p class="muted">
                Unable to load challenges.
            </p>`;
    }
}


// ============================================================
// RENDER CHALLENGES
// ============================================================

function renderChallenges(challenges) {

    const container =
        document.getElementById("challengeCards");


    if (!challenges || challenges.length === 0) {

        container.innerHTML =
            `<div class="card">

                <p class="muted">
                    No community challenges available.
                </p>

            </div>`;

        return;
    }


    let html = "";


    challenges.forEach(challenge => {

        const target =
            Number(
                challenge.targetReduction || 0
            );


        html += `

            <div class="challenge-card">

                <span class="badge">
                    COMMUNITY CHALLENGE
                </span>

                <h3>
                    ${escapeHtml(challenge.name)}
                </h3>

                <p>
                    ${escapeHtml(
                        challenge.description ||
                        "Reduce your household carbon footprint."
                    )}
                </p>

                <h2>
                    ${target}%
                </h2>

                <p>
                    Target reduction
                </p>

                <div class="progress">

                    <span
                        style="width: 0%">
                    </span>

                </div>

                <small>

                    ${challenge.startDate}
                    →
                    ${challenge.endDate}

                </small>

            </div>
        `;
    });


    container.innerHTML = html;
}


// ============================================================
// LEADERBOARD
// ============================================================

async function loadLeaderboard() {

    try {

        const leaderboard =
            await get(
                `/leaderboard?year=${YEAR}&month=${MONTH}`
            );

        renderLeaderboard(leaderboard);

    } catch (error) {

        console.error(error);

        document.getElementById("leaderboardTable").innerHTML =
            `<p class="muted">
                Unable to load leaderboard.
            </p>`;
    }
}


// ============================================================
// RENDER LEADERBOARD
// ============================================================

function renderLeaderboard(leaderboard) {

    const container =
        document.getElementById("leaderboardTable");


    if (!leaderboard || leaderboard.length === 0) {

        container.innerHTML =
            `<p class="muted">
                No leaderboard data available.
            </p>`;

        return;
    }


    let html = `

        <table>

            <thead>

                <tr>

                    <th>Rank</th>

                    <th>Household</th>

                    <th>Previous Month</th>

                    <th>Current Month</th>

                    <th>Reduction</th>

                </tr>

            </thead>

            <tbody>
    `;


    leaderboard.forEach(item => {

        html += `

            <tr>

                <td>

                    <div class="rank">

                        ${item.rank}

                    </div>

                </td>

                <td>

                    <strong>
                        ${escapeHtml(
                            item.householdName
                        )}
                    </strong>

                </td>

                <td>

                    ${Number(
                        item.previousMonthCarbon || 0
                    ).toFixed(2)}
                    kg

                </td>

                <td>

                    ${Number(
                        item.currentMonthCarbon || 0
                    ).toFixed(2)}
                    kg

                </td>

                <td class="reduction">

                    ${Number(
                        item.percentageReduction || 0
                    ).toFixed(2)}
                    %

                </td>

            </tr>
        `;
    });


    html += `

            </tbody>

        </table>
    `;


    container.innerHTML = html;
}


// ============================================================
// ADD ACTIVITY
// ============================================================

async function submitActivity(event) {

    event.preventDefault();


    const activity = {

        household: {

            id: HOUSEHOLD_ID

        },

        date:
            document.getElementById(
                "activityDate"
            ).value,

        commuteMode:
            document.getElementById(
                "commuteMode"
            ).value,

        commuteDistanceKm:
            Number(
                document.getElementById(
                    "commuteDistance"
                ).value
            ),

        electricityConsumption:
            Number(
                document.getElementById(
                    "electricityInput"
                ).value
            ),

        wasteGenerated:
            Number(
                document.getElementById(
                    "wasteInput"
                ).value
            )
    };


    try {

        await fetch(
            `${API}/activities`,
            {

                method: "POST",

                headers: {

                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(activity)
            }
        );


        closeActivityModal();


        document
            .getElementById("activityForm")
            .reset();


        alert(
            "Activity saved successfully 🌱"
        );


        // Refresh dashboard
        loadDashboard();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to save activity."
        );
    }
}


// ============================================================
// MODAL
// ============================================================

function openActivityModal() {

    document
        .getElementById("activityModal")
        .classList.add("show");
}


function closeActivityModal() {

    document
        .getElementById("activityModal")
        .classList.remove("show");
}


// ============================================================
// API GET HELPER
// ============================================================

async function get(endpoint) {

    const response =
        await fetch(API + endpoint);


    if (!response.ok) {

        throw new Error(
            "API request failed: " +
            response.status
        );
    }


    return await response.json();
}


// ============================================================
// CARBON DISPLAY CALCULATION
// ============================================================

function calculateDisplayCarbon(activity) {

    let transportFactor = 0;


    const mode =
        String(
            activity.commuteMode || ""
        ).toUpperCase();


    if (mode === "CAR") {

        transportFactor = 0.115;

    } else if (mode === "BUS") {

        transportFactor = 0.10385;
    }


    const transport =
        Number(
            activity.commuteDistanceKm || 0
        )
        *
        transportFactor;


    const electricity =
        Number(
            activity.electricityConsumption || 0
        )
        *
        0.716;


    const waste =
        Number(
            activity.wasteGenerated || 0
        )
        *
        0.50;


    return transport +
        electricity +
        waste;
}


// ============================================================
// COMMUTE ICON
// ============================================================

function getCommuteIcon(mode) {

    switch (
        String(mode || "").toUpperCase()
    ) {

        case "CAR":
            return "🚗";

        case "BUS":
            return "🚌";

        case "BIKE":
        case "BICYCLE":
            return "🚲";

        case "WALK":
        case "WALKING":
            return "🚶";

        default:
            return "🌱";
    }
}


// ============================================================
// ERROR MESSAGE
// ============================================================

function showBackendError() {

    document.getElementById(
        "monthlyCarbon"
    ).textContent = "--";

    document.getElementById(
        "reduction"
    ).textContent = "--";

    document.getElementById(
        "electricity"
    ).textContent = "--";

    document.getElementById(
        "rank"
    ).textContent = "--";

    document.getElementById(
        "recentActivities"
    ).innerHTML =

        `<p class="muted">
            Backend unavailable.
            Make sure Spring Boot is running.
        </p>`;
}


// ============================================================
// BASIC HTML SAFETY
// ============================================================

function escapeHtml(value) {

    return String(value ?? "")
        .replace(
            /[&<>"']/g,
            character => {

                const entities = {

                    "&": "&amp;",
                    "<": "&lt;",
                    ">": "&gt;",
                    '"': "&quot;",
                    "'": "&#039;"
                };

                return entities[character];
            }
        );
}