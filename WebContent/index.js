/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

let commentForm = $("#comment-form")

function handleCommentsArray(resultArray) {
    console.log(resultArray);
    let comment_list = $("#comment_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each comment will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    comment_list.html("");
    comment_list.append(res);
}

function refreshComments() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",// Setting request method
        url: "api/comments",
        success: (resultData) => handleCommentsArray(resultData),
    });
}

function insertComment(submitEvent) {
    console.log("submit comment");

    submitEvent.preventDefault()

    $.ajax("api/comments", {
        method: "POST",
        data: commentForm.serialize(),
        success: (insertResponse) => {
            console.log(insertResponse)
            refreshComments()
        },
        error: (err) => console.log(err)
    });
    commentForm[0].reset()
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleStarInfo(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = 755017  // hardcoded Edie Murphy's star ID

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleStarInfo(resultData)
});

// load all the comments
refreshComments();

// bind form submit to a function
commentForm.submit(insertComment);