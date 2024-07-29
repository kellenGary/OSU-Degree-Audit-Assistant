<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>OSU Degree Audit</title>
    <link rel="stylesheet" href="style-home.css">
    <script>
        window.addEventListener('load', function() {
            window.scrollTo(0, 0);
        });
    </script>
</head>
<body>
<header class="block-animation">
    <img src="Resources/block-o-header.png" class="block-o-header">
    <h1 class="header-text">OSU Degree Audit</h1>
</header>

<div class="block-animation">
    <div class="container">
        <form class="fileForm" method="post" action="/fileServlet" enctype="multipart/form-data">
            <h3 class="link-info-text">Submit</h3>
            <label for="fileInput" class="custom-file-label">Choose File</label>
            <input type="file" id="fileInput" name="file" class="input-size" required>
            <button type="submit" class="submit-button">Submit File</button>
            <script>
                document.getElementById('fileInput').addEventListener('change', function() {
                    var fileName = this.files[0].name;
                    document.getElementById('fileName').textContent = 'Selected file: ' + fileName;
                });
            </script>
            <span id="fileName" class="file-name"></span>
        </form>
    </div>

    <div class="text-grid">
        <div class="container ">
            <h3>Directions</h3>
            <div class="direction-text">1. Go to <a href="https://degreeaudit.osu.edu/selfservice/" target="_blank">https://degreeaudit.osu.edu/selfservice/</a><br/>
                2. Request a new audit<br/>
                3. View the audit<br/>
                5. Download the .html <br/>
                6. Choose the downloaded file after pressing the "Choose file" button<br/>
                7. Press submit and view your upgraded audit</div>
        </div>
        <div class="container">
            RECENTLY ADDED AUDITS
        </div>
        <div class="container">
            <h3>About Me</h3>
            <div class="about-me">
                <img src="Resources/profile-pic.jpg" class="profile-pic">
                <p class="profile-text span-rest">
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit.
                </p>
            </div>
            <div class="social-links">
                <div class="github link-container">
                    <img src="Resources/github-logo.png">
                    <p>Github Repository <br>
                    <a href="https://github.com/kellenGary/OSU-Degree-Audit-Update" target="_blank">Click here</a></p>
                </div>
                <div class="outlook link-container">
                    <img src="Resources/outlook-icon.png">
                    <p>Outlook <br>
                    <a href="mailto:gary.106@osu.buckeyemail.edu" target="_blank">Email Here</a></p>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>