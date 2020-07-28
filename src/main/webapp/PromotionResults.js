/* Purpose: script that displays result of promotion quiz to user and allows for return to the game */
const IS_PROMOTED_PARAM = 'isPromoted';
const HTML_MESSAGE_PARAM = 'promotionMessage';
const HTML_BUTTON_PARAM = 'returnButton';
const HTML_MEME = 'promotionMeme';
const PROMOTED_MESSAGE = "Congratulations, you passed the quiz and were promoted!!";
const PROMOTED_BUTTON = 'Advance to next stage.'
const NOT_PROMOTED_MESSAGE = 'You did not pass the promotion quiz and that is okay. Study the content and try again';
const DISCLAMER_MESSAGE = 'please know that going to work on Saturdays is not okay please talk to your manager and enjoy your weekends';
const NOT_PROMOTED_BUTTON = 'Return to current stage.'
const PROMOTED_MEME = '<img src="images/promoted_success1.jpg" alt="successfull promotion meme">';
const NOT_PROMOTED_MEME = '<img src="images/promotionfailed.jpg" alt="unsuccessfull promotion meme">';
const MESSAGE = PROMOTED_MESSAGE + '\n' + DISCLAMER_MESSAGE;

function showPromotionResults() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const isPromotedString = urlParams.get(IS_PROMOTED_PARAM);
  let isPromoted = (isPromotedString === 'true');
  isPromoted ?
      addToPage(MESSAGE, PROMOTED_BUTTON, PROMOTED_MEME) :
      addToPage(NOT_PROMOTED_MESSAGE, NOT_PROMOTED_BUTTON, NOT_PROMOTED_MEME);
}

function addToPage(message, buttonValue, meme) {
  const promotionMessageTextElement = document.getElementById(HTML_MESSAGE_PARAM);
  promotionMessageTextElement.innerHTML = message;
  const promotionButton = document.getElementById(HTML_BUTTON_PARAM);
  promotionButton.innerHTML = buttonValue;
  const promotionMEME = document.getElementById(HTML_MEME);
  promotionMEME.innerHTML = meme;
}
