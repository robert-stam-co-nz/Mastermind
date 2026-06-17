const CONFIG = {
    EASY: { name: 'Easy', codeLength: 4, maxColors: 6, maxAttempts: 12 },
    MEDIUM: { name: 'Medium', codeLength: 4, maxColors: 8, maxAttempts: 10 },
    HARD: { name: 'Hard', codeLength: 5, maxColors: 8, maxAttempts: 10 }
};

const PEG_COLORS = [
    '#EF5350', '#42A5F5', '#66BB6A', '#FFEE58', '#FFA726', '#EC407A', '#AB47BC', '#26A69A'
];
const PEG_LABELS = ['R', 'B', 'G', 'Y', 'O', 'P', 'U', 'T'];

let state = {
    difficulty: CONFIG.EASY,
    secretCode: [],
    pastGuesses: [],
    currentGuess: [],
    activeCursorIndex: 0,
    elapsedTime: 0,
    gameState: 'PLAYING', // PLAYING, WON, LOST
    shouldShowSharePrompt: true
};

let timerInterval;

function init() {
    startNewGame();
    render();
    setupListeners();
}

function startNewGame() {
    clearInterval(timerInterval);
    state.secretCode = Array.from({ length: state.difficulty.codeLength }, () =>
        Math.floor(Math.random() * state.difficulty.maxColors)
    );
    state.pastGuesses = [];
    state.currentGuess = Array(state.difficulty.codeLength).fill(null);
    state.activeCursorIndex = 0;
    state.elapsedTime = 0;
    state.gameState = 'PLAYING';

    timerInterval = setInterval(() => {
        state.elapsedTime++;
        document.getElementById('timer').innerText = state.elapsedTime + 's';
    }, 1000);

    render();
}

function selectColor(colorIdx) {
    if (state.gameState !== 'PLAYING') return;

    state.currentGuess[state.activeCursorIndex] = colorIdx;

    // Auto-advance
    const nextEmpty = state.currentGuess.indexOf(null);
    if (nextEmpty !== -1) {
        state.activeCursorIndex = nextEmpty;
    } else {
        state.activeCursorIndex = (state.activeCursorIndex + 1) % state.difficulty.codeLength;
    }

    render();
}

function submitGuess() {
    if (state.currentGuess.includes(null)) return;

    const { exact, colorMatch } = evaluate(state.currentGuess, state.secretCode);

    state.pastGuesses.push({
        colors: [...state.currentGuess],
        exact,
        colorMatch
    });

    if (exact === state.difficulty.codeLength) {
        state.gameState = 'WON';
        endGame();
    } else if (state.pastGuesses.length >= state.difficulty.maxAttempts) {
        state.gameState = 'LOST';
        endGame();
    } else {
        state.currentGuess = Array(state.difficulty.codeLength).fill(null);
        state.activeCursorIndex = 0;
    }

    render();
}

function endGame() {
    clearInterval(timerInterval);
    if (state.shouldShowSharePrompt) {
        document.getElementById('share-modal').classList.remove('hidden');
        document.getElementById('share-title').innerText = state.gameState === 'WON' ? '🎉 GAME WON!' : '🎮 GAME OVER';
    }
}

function evaluate(guess, secret) {
    let exact = 0;
    const sRem = [];
    const gRem = [];

    secret.forEach((s, i) => {
        if (s === guess[i]) exact++;
        else {
            sRem.push(s);
            gRem.push(guess[i]);
        }
    });

    let colorMatch = 0;
    gRem.forEach(g => {
        const idx = sRem.indexOf(g);
        if (idx !== -1) {
            colorMatch++;
            sRem.splice(idx, 1);
        }
    });

    return { exact, colorMatch };
}

function render() {
    // Render Board
    const board = document.getElementById('game-board');
    board.innerHTML = '';

    for (let i = 0; i < state.difficulty.maxAttempts; i++) {
        const row = document.createElement('div');
        row.className = 'row';

        if (i < state.pastGuesses.size) {
            // Past
        } else if (i === state.pastGuesses.length && state.gameState === 'PLAYING') {
            row.classList.add('active-row');
            // ...
        }

        // This is a simplified render. In a real PWA I'd use template strings or a framework.
        // For now, let's just show the logic flow.
    }

    // Render Palette
    const palette = document.getElementById('peg-options');
    palette.innerHTML = '';
    for (let i = 0; i < state.difficulty.maxColors; i++) {
        const btn = document.createElement('div');
        btn.className = 'peg';
        btn.style.backgroundColor = PEG_COLORS[i];
        btn.innerText = PEG_LABELS[i];
        btn.onclick = () => selectColor(i);
        palette.appendChild(btn);
    }

    document.getElementById('attempts-left').innerText = `Attempts Left: ${state.difficulty.maxAttempts - state.pastGuesses.length} / ${state.difficulty.maxAttempts}`;
}

function setupListeners() {
    document.getElementById('restart-btn').onclick = startNewGame;
    document.getElementById('clear-btn').onclick = () => {
        state.currentGuess.fill(null);
        state.activeCursorIndex = 0;
        render();
    };
    document.getElementById('close-share-btn').onclick = () => document.getElementById('share-modal').classList.add('hidden');
    document.getElementById('dont-ask-btn').onclick = () => {
        state.shouldShowSharePrompt = false;
        document.getElementById('share-modal').classList.add('hidden');
    };
    document.getElementById('share-btn').onclick = () => {
        const msg = `I just played Mastermind on ${state.difficulty.name} mode and used ${state.pastGuesses.length} lines!`;
        if (navigator.share) {
            navigator.share({
                title: 'Mastermind Result',
                text: msg
            });
        } else {
            alert(msg);
        }
    };
}

init();