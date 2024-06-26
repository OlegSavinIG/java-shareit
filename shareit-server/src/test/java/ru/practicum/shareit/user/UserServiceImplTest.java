package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.addUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotExistException exception = assertThrows(NotExistException.class, () -> {
            userService.getUserById(user.getId());
        });

        assertEquals("Не существует пользователь с id 1", exception.getMessage());
    }

    @Test
    public void testUpdateUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(userDto, user.getId());

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotExistException exception = assertThrows(NotExistException.class, () -> {
            userService.updateUser(userDto, user.getId());
        });

        assertEquals("Не существует пользователь с id 1", exception.getMessage());
    }

    @Test
    public void testDeleteUserById() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(user.getId());

        userService.deleteUserById(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    public void testDeleteUserByIdNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        NotExistException exception = assertThrows(NotExistException.class, () -> {
            userService.deleteUserById(user.getId());
        });

        assertEquals("Не существует пользователь с этим id", exception.getMessage());
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(user.getName(), result.get(0).getName());
    }
}
