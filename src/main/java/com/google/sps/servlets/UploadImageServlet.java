// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobKey;
import javax.servlet.annotation.WebServlet;
import java.io.PrintWriter;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Inject;

// Servlet responsible for getting file and sending it to be handled. 
// Writes image url created by Blobstore
@WebServlet("/upload-image")
public class UploadImageServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private static final String IMAGE_HANDLER_SERVLET_PARAMETER = "/image-handler";
    private static final String TEXT_TYPE_PARAMETER = "text/html";

    @Inject public UploadImageServlet(BlobstoreService blobstoreService) {
        this.blobstoreService = blobstoreService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String uploadUrl = blobstoreService.createUploadUrl(IMAGE_HANDLER_SERVLET_PARAMETER);
        response.setContentType(TEXT_TYPE_PARAMETER);
        response.getWriter().println(uploadUrl);
    }
}
