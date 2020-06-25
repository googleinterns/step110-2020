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
 * Utility class used to find Blobstore upload Url from form element.
 */
public final class BlobstoreUrlUtility {
  /**
   * Finds the Url that points to the uploaded file in Blobstore.
   * This method is meant to be used for forms that accept input for only a single file.
   *
   * @param request the request sent by the upload form submission
   * @param formUploadElem the ID of the element in the form that selects the file to upload
   * @return the upload Url of the submitted file wrapped in an {@link Optional}, the Optional will
   *     be empty if the form element given has no input that accepts an image file or the Url given
   *     in the request is malformed
   */
  public static Optional<String> getUploadUrl(HttpServletRequest request, String formUploadElem) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    // Blobstore returns map with key: "name field inside HTML form",
    // value: "list of keys for each file that were uploaded in the specified form element"
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);

    // Retrieving the blob keys that were present in the input tag with the with
    // the name found in "formUploadElem"
    List<BlobKey> blobKeyList = blobs.get(formUploadElem);

    if (blobKeyList == null || blobKeyList.isEmpty()) {
      return Optional.empty();
    }

    // The form accepts a single image file so there is only one element in the blob list.
    BlobKey blobKey = blobKeyList.get(0);
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    // If the file has 0 byte size then we discard it.
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
      System.err.println("Failed to create Url from blobKey!");
      return Optional.empty();
    }
  }

  private BlobstoreUrlUtility() {}
}
