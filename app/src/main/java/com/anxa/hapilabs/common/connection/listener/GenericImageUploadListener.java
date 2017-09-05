package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by angelaanxa on 8/10/2016.
 */
public interface GenericImageUploadListener {
    public void ImageUploadSuccess(Boolean forUpload, String hapiMomentId);
    public void ImageUploadFailedWithError(MessageObj response);

}
