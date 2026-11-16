const career = [
    {
        job: "Senior Software Engineer",
        employer: "Fiverr",
        date: "December 2012 - Present",
    },
    {
        job: "Senior Software Engineer",
        employer: "Visa",
        date: "June 2022 - June 2026",
    },
];

const awards = [];

const courses = [];

// DOM Content Loaded Event
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("copyBtn").addEventListener("click", function () {
        const phoneText = document.getElementById("phone").innerText;
        navigator.clipboard.writeText(phoneText).then(() => {
            // switch icon style
            this.classList.remove("fa-regular", "fa-copy");
            this.classList.add("fas", "fa-check");
            this.style.color = "green";

            // revert after 1.5s
            setTimeout(() => {
                this.classList.remove("fas", "fa-check");
                this.classList.add("fa-regular", "fa-copy");
                this.style.color = "#555";
            }, 1500);
        });
    });

    // Populate career section
    const careerContainer = document.getElementById("careerContainer");
    career.forEach((item) => {
        const div = document.createElement("div");
        div.className = "col-lg-6 mb-4";
        div.innerHTML = `
                <div class="experience-card">
                    <h4>${item.job}</h4>
                    <h5>${item.employer}</h5>
                    <p>${item.date}</p>
                </div>
            `;
        careerContainer.appendChild(div);
    });

    // Populate awards section
    const awardsContainer = document.getElementById("awardsContainer");
    awards.forEach((award) => {
        const div = document.createElement("div");
        div.className = "col-lg-6 mb-4";
        div.innerHTML = `
                <div class="experience-card">
                    <h4>${award.date}</h4>
                    <p>${award.details}</p>
                </div>
            `;
        awardsContainer.appendChild(div);
    });

    // Populate courses section
    const coursesContainer = document.getElementById("coursesContainer");
    courses.forEach((course) => {
        const div = document.createElement("div");
        div.className = "col-lg-6 mb-4";
        div.innerHTML = `
                <div class="experience-card">
                    <h4>${course.course}</h4>
                    <h5>${course.institution}</h5>
                    <p>${course.location}</p>
                </div>
            `;
        coursesContainer.appendChild(div);
    });

    // Scroll to top functionality
    const scrollToTop = document.getElementById("scrollToTop");
    window.addEventListener("scroll", function () {
        if (window.pageYOffset > 300) {
            scrollToTop.classList.add("show");
        } else {
            scrollToTop.classList.remove("show");
        }
    });

    // Smooth scrolling for navigation links
    document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
        anchor.addEventListener("click", function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute("href"));
            if (target) {
                target.scrollIntoView({
                    behavior: "smooth",
                    block: "start",
                });
            }
        });
    });
});
