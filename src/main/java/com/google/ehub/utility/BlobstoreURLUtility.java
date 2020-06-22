package com.google.ehub.utility;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class used to find Blobstore upload URL from form element.
 */
public final class BlobstoreURLUtility {
  /**
   * Finds the URL that points to the uploaded file in Blobstore.
   *
   * @param request the request sent by the upload form submission
   * @param formUploadElem the ID of the element in the form that selects the file to upload
   * @return the upload URL of the submitted file wrapped in an {@link Optional}
   */
  public static Optional<String> getUploadURL(HttpServletRequest request, String formUploadElem) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeyList = blobs.get(formUploadElem);

    if (blobKeyList == null || blobKeyList.isEmpty()) {
      return Optional.empty();
    }

    BlobKey blobKey = blobKeyList.get(0);
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return Optional.empty();
    }

    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return Optional.of(url.getPath());
    } catch (MalformedURLException e) {
      System.err.println("Failed to create URL from blobKey!");
      return Optional.empty();
    }
  }

  private BlobstoreURLUtility() {}
}
