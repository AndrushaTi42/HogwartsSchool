package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AvatarController.class)
public class AvatarControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private AvatarService avatarService;


    @Test
    public void uploadAvatarTest() throws Exception {
        final long studentId = 1L;

        MockMultipartFile fakeFile = new MockMultipartFile(
                "avatar",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "some-image-byte-content".getBytes()
        );

        doNothing().when(avatarService).uploadAvatar(anyLong(), any());

        mockMvc.perform(multipart("/avatar/" + studentId)
                        .file(fakeFile))
                .andExpect(status().isOk());
    }

    @Test
    public void uploadAvatarTooBigTest() throws Exception {
        final long studentId = 1L;

        byte[] bigFileContent = new byte[1024 * 301];

        MockMultipartFile bigFile = new MockMultipartFile(
                "avatar",
                "huge_photo.png",
                MediaType.IMAGE_PNG_VALUE,
                bigFileContent
        );

        mockMvc.perform(multipart("/avatar/" + studentId)
                        .file(bigFile))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string("File is too big"));

        verify(avatarService, org.mockito.Mockito.never()).uploadAvatar(anyLong(), any());
    }

    @Test
    public void downloadAvatarDataTest() throws Exception {
        final long studentId = 1L;
        byte[] fakeImageBytes = "some-image-byte-content".getBytes();

        Avatar fakeAvatar = new Avatar();
        fakeAvatar.setMediaType(MediaType.IMAGE_PNG_VALUE);
        fakeAvatar.setData(fakeImageBytes);

        when(avatarService.findAvatar(studentId)).thenReturn(fakeAvatar);

        mockMvc.perform(get("/avatar/" + studentId + "/data"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(fakeImageBytes));
    }

    @Test
    public void downloadAvatarTest() throws Exception {
        final long studentId = 1L;
        String fileContent = "testByteByteByte";
        String actualPath = Files.createTempFile("avatar", ".png").toString();
        Files.writeString(Path.of(actualPath), fileContent);

        Avatar fakeAvatar = new Avatar();
        fakeAvatar.setMediaType(MediaType.IMAGE_PNG_VALUE);
        fakeAvatar.setFileSize(fileContent.getBytes().length);
        fakeAvatar.setFilePath(actualPath);

        when(avatarService.findAvatar(studentId)).thenReturn(fakeAvatar);

        mockMvc.perform(get("/avatar/" + studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(fileContent.getBytes()));
    }


}
