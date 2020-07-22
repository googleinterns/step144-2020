/* Purpose: script that displays result of promotion quiz to user and allows for return to the game */
const IS_PROMOTED_PARAM = 'isPromoted';
const HTML_MESSAGE_PARAM = 'promotionMessage';
const HTML_BUTTON_PARAM = 'returnButton';
const PROMOTED_MESSAGE = 'Congratulations, you passed the quiz and were promoted!';
const PROMOTED_BUTTON = 'Advance to next stage.'
const NOT_PROMOTED_MESSAGE = "You did not pass the quiz. Study the content and try again later";
const NOT_PROMOTED_BUTTON = 'Return to current stage.'

function showPromotionResults() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const isPromotedString = urlParams.get(IS_PROMOTED_PARAM);
  let isPromoted = (isPromotedString === 'true');
  isPromoted ?
      addToPage(PROMOTED_MESSAGE, PROMOTED_BUTTON) :
      addToPage(NOT_PROMOTED_MESSAGE, NOT_PROMOTED_BUTTON);
}

function addToPage(message, buttonValue) {
  const promotionMessageTextElement = document.getElementById(HTML_MESSAGE_PARAM);
  promotionMessageTextElement.innerHTML = message;
  const promotionButton = document.getElementById(HTML_BUTTON_PARAM);
  promotionButton.innerHTML = buttonValue;
}
