package co.kr.poptrend.googleStorage;

import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ErrorCode;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GCSService {

    private final Storage storage;

    public String uploadFileToGCS(MultipartFile file) {
        String fileName = "images/" + UUID.randomUUID();
        try {
            storage.create(
                    BlobInfo.newBuilder("trend_buck", fileName)
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))))
                            .setContentType(file.getContentType())
                            .build(),
                    new ByteArrayInputStream(file.getBytes()));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.ConnectionError, e);
        }
        return "https://cdn.poptrend.co.kr/"+fileName;
    }
}
