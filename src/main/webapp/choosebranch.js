/* Purpose: script that fetches recommended career path and highlights the option*/
const RECOMMENDED_PATH_PARAM = 'recommendedPath';
const PATH_INTRODUCTION_TEXT =
    'After analyzing the results of the career quiz, your recommended path is ';
const HIGHLIGHT_CLASS = 'highlight';

function highlightRecommendedCareerPath() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const recommendedPath = urlParams.get(RECOMMENDED_PATH_PARAM);
  const recommendedPathTextElement = document.getElementById('recommendedPathText');
  recommendedPathTextElement.innerHTML = PATH_INTRODUCTION_TEXT + recommendedPath + '.';
  const buttonToHightlight = document.getElementById(recommendedPath);
  buttonToHightlight.classList.add(HIGHLIGHT_CLASS);
}
