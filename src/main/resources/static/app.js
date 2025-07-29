const API_BASE = "/api";

class TriviaGame {
  constructor() {
    this.questions = [];
    this.currentQuestionIndex = 0;
    this.userAnswers = {};
    this.results = null;

    this.screens = {
      start: document.getElementById("start-screen"),
      quiz: document.getElementById("quiz-screen"),
      results: document.getElementById("results-screen"),
      error: document.getElementById("error-screen"),
    };

    this.loading = document.getElementById("loading");

    this.initializeEventListeners();
  }

  initializeEventListeners() {
    document
      .getElementById("start-btn")
      .addEventListener("click", () => this.startGame());
    document
      .getElementById("prev-btn")
      .addEventListener("click", () => this.previousQuestion());
    document
      .getElementById("next-btn")
      .addEventListener("click", () => this.nextQuestion());
    document
      .getElementById("submit-btn")
      .addEventListener("click", () => this.submitAnswers());
    document
      .getElementById("restart-btn")
      .addEventListener("click", () => this.restartGame());
    document
      .getElementById("error-restart-btn")
      .addEventListener("click", () => this.restartGame());
  }

  showScreen(screenName) {
    Object.values(this.screens).forEach((screen) =>
      screen.classList.add("hidden"),
    );
    this.screens[screenName].classList.remove("hidden");
  }

  showLoading() {
    this.loading.classList.remove("hidden");
  }

  hideLoading() {
    this.loading.classList.add("hidden");
  }

  async startGame() {
    const amount = document.getElementById("amount").value;
    const difficulty = document.getElementById("difficulty").value;

    this.showLoading();

    try {
      const params = new URLSearchParams({ amount });
      if (difficulty) params.append("difficulty", difficulty);

      const response = await fetch(`${API_BASE}/questions?${params}`);
      if (!response.ok) throw new Error("Failed to fetch questions");

      this.questions = await response.json();
      this.currentQuestionIndex = 0;
      this.userAnswers = {};

      document.getElementById("total-questions").textContent =
        this.questions.length;
      document.getElementById("total-count").textContent =
        this.questions.length;

      this.showScreen("quiz");
      this.displayQuestion();
    } catch (error) {
      this.showError("Failed to load questions. Please try again.");
    } finally {
      this.hideLoading();
    }
  }

  displayQuestion() {
    const question = this.questions[this.currentQuestionIndex];

    document.getElementById("current-question").textContent =
      this.currentQuestionIndex + 1;
    document.getElementById("question-text").textContent = question.question;

    const optionsContainer = document.getElementById("options-container");
    optionsContainer.innerHTML = "";

    question.options.forEach((option, index) => {
      const optionDiv = document.createElement("div");
      optionDiv.className = "option";
      optionDiv.textContent = option;
      optionDiv.addEventListener("click", () => this.selectOption(option));

      if (this.userAnswers[question.id] === option) {
        optionDiv.classList.add("selected");
      }

      optionsContainer.appendChild(optionDiv);
    });

    this.updateProgressBar();
    this.updateNavigationButtons();
  }

  selectOption(option) {
    const question = this.questions[this.currentQuestionIndex];
    this.userAnswers[question.id] = option;

    document.querySelectorAll(".option").forEach((opt) => {
      opt.classList.remove("selected");
      if (opt.textContent === option) {
        opt.classList.add("selected");
      }
    });
  }

  updateProgressBar() {
    const progress =
      ((this.currentQuestionIndex + 1) / this.questions.length) * 100;
    document.getElementById("progress").style.width = `${progress}%`;
  }

  updateNavigationButtons() {
    const prevBtn = document.getElementById("prev-btn");
    const nextBtn = document.getElementById("next-btn");
    const submitBtn = document.getElementById("submit-btn");

    prevBtn.disabled = this.currentQuestionIndex === 0;

    if (this.currentQuestionIndex === this.questions.length - 1) {
      nextBtn.classList.add("hidden");
      submitBtn.classList.remove("hidden");
    } else {
      nextBtn.classList.remove("hidden");
      submitBtn.classList.add("hidden");
    }
  }

  previousQuestion() {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
      this.displayQuestion();
    }
  }

  nextQuestion() {
    if (this.currentQuestionIndex < this.questions.length - 1) {
      this.currentQuestionIndex++;
      this.displayQuestion();
    }
  }

  async submitAnswers() {
    const unanswered = this.questions.filter((q) => !this.userAnswers[q.id]);
    if (unanswered.length > 0) {
      if (
        !confirm(
          `You have ${unanswered.length} unanswered questions. Submit anyway?`,
        )
      ) {
        return;
      }
    }

    this.showLoading();

    try {
      const answers = this.questions.map((q) => ({
        questionId: q.id,
        answer: this.userAnswers[q.id] || "",
      }));

      const response = await fetch(`${API_BASE}/checkanswers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ answers }),
      });

      if (!response.ok) throw new Error("Failed to submit answers");

      this.results = await response.json();
      this.showResults();
    } catch (error) {
      this.showError("Failed to submit answers. Please try again.");
    } finally {
      this.hideLoading();
    }
  }

  showResults() {
    document.getElementById("score").textContent = Math.round(
      this.results.score,
    );
    document.getElementById("correct-count").textContent =
      this.results.correctAnswers;

    const resultsContainer = document.getElementById("results-container");
    resultsContainer.innerHTML = "";

    this.results.results.forEach((result, index) => {
      const question = this.questions.find((q) => q.id === result.questionId);

      const resultItem = document.createElement("div");
      resultItem.className = `result-item ${result.correct ? "correct" : "incorrect"}`;

      resultItem.innerHTML = `
                <div class="result-question">${index + 1}. ${question.question}</div>
                <div class="result-answer">Your answer: ${this.userAnswers[question.id] || "Not answered"}</div>
                ${!result.correct ? `<div class="result-answer">Correct answer: ${result.correctAnswer}</div>` : ""}
            `;

      resultsContainer.appendChild(resultItem);
    });

    this.showScreen("results");
  }

  showError(message) {
    document.getElementById("error-message").textContent = message;
    this.showScreen("error");
  }

  restartGame() {
    this.questions = [];
    this.currentQuestionIndex = 0;
    this.userAnswers = {};
    this.results = null;
    this.showScreen("start");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  new TriviaGame();
});

