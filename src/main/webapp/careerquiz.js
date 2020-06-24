/*
* Purpose: recieves HTTP promise response from Career Quiz servlet, displays questions and responses 
*/

function getQuestionsAndChoices() {
  const responsePromise = fetch('/careerquiz');
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
  for (let i = 0; i < questionsAndChoicesList.length; i++){
    question = questionsAndChoicesList[i].question;
    choices = questionsAndChoicesList[i].choices
    questionsDiv.appendChild(
        createQuestionAndChoices(question, choices));
 }
}

function createQuestionAndChoices(question, choices) {
  const formContainer = document.createElement('div');

  const questionText = document.createElement('p');
  questionText.innerHTML = question;
  formContainer.appendChild(questionText);

  const choiceForm = createChoiceInputForm(question, choices);
  formContainer.appendChild(choiceForm);

  return formContainer;
}

function createChoiceInputForm(question, choices) {
  const choiceForm = document.createElement('form');
  for (let i = 0; i < choices.length; i++) {
    let choiceLabel = createLabel(choices[i].choiceText);
    let choiceRadio = createRadioButton(question, choices[i].choiceText);
    choiceForm.appendChild(choiceLabel);
    choiceForm.appendChild(choiceRadio);
   }
  let submitButton = createSubmitButton(question);
  choiceForm.appendChild(submitButton);
  return choiceForm;
}

function createLabel(text) {
  const label = document.createElement('label');
  label.setAttribute('for', text)
  label.innerHTML = text;
  return label;
}

function createRadioButton(name, id) {
  const button = document.createElement('input');
  button.setAttribute('type', 'radio');
  button.setAttribute('id', id);
  button.setAttribute('name', name);
  return button;
}

function createSubmitButton(id) {
  const submitButton = document.createElement('input'); 
  submitButton.setAttribute('type','submit');
  submitButton.setAttribute('value','Submit');
  submitButton.setAttribute('id', id);
  // TODO: replace with formaction that handles quiz responses
  submitButton.setAttribute('formaction', '');
  return submitButton;
}
