/*
* Purpose: recieves HTTP promise response from a Quiz servlet, displays questions and choices then
* sends selected choices back to servlet 
*/

const QUIZ_SERVLET = document.currentScript.getAttribute('servletName'); 
const SUBMIT_BUTTON_NAME = document.currentScript.getAttribute('submitButtonName');
const REDIRECT_PAGE = document.currentScript.getAttribute('redirect');

function getQuestionsAndChoices() {
  const responsePromise = fetch(QUIZ_SERVLET);
  responsePromise.then(handleResponse);
}

function handleResponse(response) {
  const responsePromise = response.json();
  responsePromise.then(addToDom);
}

/* Adds questions and choices for the quiz to the page. */
function addToDom(questionsAndChoicesList) {
  const questionsDiv = document.getElementById('questions-and-choices-div');
  questionsDiv.innerHTML = '';
  const questionsForm = document.createElement('form');
  questionsForm.setAttribute('action', QUIZ_SERVLET);
  questionsForm.setAttribute('method', 'POST');
  for (let i = 0; i < questionsAndChoicesList.length; i++) {
    question = questionsAndChoicesList[i].question;
    choices = questionsAndChoicesList[i].choices;
    questionsForm.appendChild(
        createQuestionAndChoices(question, choices));
 }
  let submitButton = createSubmitButton(SUBMIT_BUTTON_NAME);
  questionsForm.appendChild(submitButton);
  questionsDiv.appendChild(questionsForm);
}

function createQuestionAndChoices(question, choices) {
  const formSection = document.createElement('div');

  const questionText = document.createElement('p');
  questionText.innerHTML = question;
  formSection.appendChild(questionText);

  const choiceInput = addChoiceInput(question, choices);
  formSection.appendChild(choiceInput);

  return formSection;
}

function addChoiceInput(question, choices) {
  const choicesDiv = document.createElement('div');
  for (let i = 0; i < choices.length; i++) {
    let choiceLabel = createLabel(choices[i].choiceText);
    const buttonValue = JSON.stringify(choices[i]);
    let choiceRadio = createRadioButton(question, buttonValue);
    choicesDiv.appendChild(choiceLabel);
    choicesDiv.appendChild(choiceRadio);
   }
  return choicesDiv;
}

function createLabel(text) {
  const label = document.createElement('label');
  label.setAttribute('for', text);
  label.innerHTML = text;
  return label;
}

function createRadioButton(name, value) {
  const button = document.createElement('input');
  button.setAttribute('type', 'radio');
  button.setAttribute('name', name);
  button.setAttribute('value', value);
  button.required = true;
  return button;
}

function createSubmitButton(name) {
  const submitButton = document.createElement('input'); 
  submitButton.setAttribute('type','submit');
  submitButton.setAttribute('name', name);
  submitButton.setAttribute('onclick', REDIRECT_PAGE);
  return submitButton;
}
