package ch.wyler.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import ch.wyler.client.dtos.CreateProjectFromUploadResponse;
import ch.wyler.client.dtos.GetCsrfTokenResponse;
import ch.wyler.client.dtos.GetProjectMetadataResponse;
import ch.wyler.client.dtos.GetVersionResponse;
import ch.wyler.client.dtos.ResultResponse;
import ch.wyler.client.exceptions.OpenRefineException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Slf4j
public class OpenRefineService {

    private final OpenRefineApiService service;

    public OpenRefineService(final String baseUrl) {
        service = OpenRefineApiServiceGenerator.createService(baseUrl);
    }

    public GetCsrfTokenResponse getCsrfToken() throws OpenRefineException {
        try {
            return service.getCsrfToken().execute().body();
        } catch (final IOException e) {
            throw new OpenRefineException(e);
        }
    }

    public GetVersionResponse getVersion() throws OpenRefineException {
        try {
            return service.getVersion().execute().body();
        } catch (final IOException e) {
            throw new OpenRefineException(e);
        }
    }

    public GetProjectMetadataResponse getProjectMetadata(final String projectId) throws OpenRefineException {
        try {
            return service.getProjectMetadata(projectId).execute().body();
        } catch (final IOException e) {
            throw new OpenRefineException(e);
        }
    }

    public CreateProjectFromUploadResponse createProjectFromUpload(final String projectName, final File projectFile)
            throws OpenRefineException {
        final Call<ResponseBody> call = service.createProjectFromUpload(
                RequestBody.create(MediaType.parse("text/plain"), projectName),
                MultipartBody.Part.createFormData(
                        "project-file",
                        projectFile.getName(),
                        RequestBody.create(MediaType.parse("text/*"), projectFile)));
        try {
            final Response<ResponseBody> response = call.execute();
            return CreateProjectFromUploadResponse.builder()
                    .location(new URL(response.raw().priorResponse().headers().get("Location")))
                    .build();
        } catch (final IOException e) {
            throw new OpenRefineException(e);
        }
    }

    public ResultResponse deleteProject(final String projectId) throws OpenRefineException {
        try {
            return service.deleteProject(projectId).execute().body();
        } catch (final IOException e) {
            throw new OpenRefineException(e);
        }
    }

    public ResultResponse applyOperations(final String projectId, final String jsonOperations) throws OpenRefineException {
        try {
            return service.applyOperations(projectId, jsonOperations).execute().body();
        } catch (final IOException e) {
            throw new OpenRefineException(e);
        }
    }

    public ResultResponse exportRows(final String projectId, final String format, final String jsonEngine) {
        final Call<ResponseBody> call = service.exportRows(projectId, format, jsonEngine);
        call.enqueue(new Callback<>() {
            @SneakyThrows
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    final boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                } else {
                    throw new OpenRefineException("failure");
                }
            }

            @SneakyThrows
            @Override
            public void onFailure(final Call<ResponseBody> call, final Throwable throwable) {
                throw new OpenRefineException("failure");
            }
        });

        return null;
    }

    private boolean writeResponseBodyToDisk(final ResponseBody body) {
        try {
            final File file = new File("/Users/patrick/Projects/work/edc-poc-openrefine-pipleline/testdata/export.ttl");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                final byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    final int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

//                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (final IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (final IOException e) {
            return false;
        }
    }
}
