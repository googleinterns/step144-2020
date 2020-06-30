/* Purpose: script that fetches recommended career path */

function getRecommendedCareerPath() {
    const responsePromise = fetch('/careerquiz');
    // when server request complete, pass response into handleResponse
    responsePromise.then(handleResponse);
}

function handleResponse(response) {
    // receives Java Object (ArrayList reponse)
    const recommendedCareerPath = response.json();
    recommendedCareerPath.then(changeDom);
}

function changeDom(recommendedCareerPath) {
  console.log(recommendedCareerPath);
}