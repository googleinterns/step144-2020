/*
* Purpose: recieves HTTP promise response from Career Quiz servlet, displays questions and responses 
*/
const QUIZ_FORM_NAME = 'career-quiz';
const QUIZ_SERVLET = '/careerquiz';
const CHOOSE_BRANCH_URL = 'choosebranch.html';

function getQuestionsAndChoices() {
  const responsePromise = fetch(QUIZ_SERVLET);
  responsePromise.then(handleResponse);
}

function handleResponse(response) {
  const responsePromise = response.json();
  responsePromise.then(addToDom);
}

/* Adds questions and choices for the career quiz to the page. */
function addToDom(questionsAndChoicesList) {
  const questionsDiv = document.getElementById('questions-and-choices-div');
  questionsDiv.innerHTML = '';
  const questionsForm = document.createElement('form');
  questionsForm.setAttribute('action', QUIZ_SERVLET);
  questionsForm.setAttribute('method', 'POST');
  for (let i = 0; i < questionsAndChoicesList.length; i++){
    question = questionsAndChoicesList[i].question;
    choices = questionsAndChoicesList[i].choices
    questionsForm.appendChild(
        createQuestionAndChoices(question, choices));
 }
  let submitButton = createSubmitButton('career-quiz-submit');
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
  label.setAttribute('for', text)
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
  submitButton.setAttribute('onclick', CHOOSE_BRANCH_URL);
  return submitButton;
}
