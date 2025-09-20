package com.isys3001.todo_backend.service.impl;

import com.isys3001.todo_backend.dto.request.CreateTodoRequest;
import com.isys3001.todo_backend.dto.request.UpdateTodoRequest;
import com.isys3001.todo_backend.dto.response.TodoResponse;
import com.isys3001.todo_backend.entity.Todo;
import com.isys3001.todo_backend.entity.User;
import com.isys3001.todo_backend.repositories.TodoRepository;
import com.isys3001.todo_backend.repositories.UserRepository;
import com.isys3001.todo_backend.service.TodoService;
import com.isys3001.todo_backend.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock private TodoRepository todoRepo;
    @Mock private UserRepository userRepo;

    private TodoService service;

    // Test fixtures
    private static final String EMAIL = "alice@example.com";
    private User owner;

    @BeforeEach
    void setUp() {
        // Simulate authenticated principal
        var auth = new UsernamePasswordAuthenticationToken(EMAIL, "ignored");
        SecurityContextHolder.getContext().setAuthentication(auth);

        owner = new User();
        owner.setId(101L);
        owner.setEmail(EMAIL);

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(owner));

        service = new TodoServiceImpl(todoRepo, userRepo);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ---------- create() ----------

    @Test
    void create_ShouldPersistWithOwner_AndReturnMappedResponse() {
        // given
        var req = new CreateTodoRequest("Write report", "CI/CD section first");
        var saved = todo(1L, "Write report", "CI/CD section first", false, owner);

        when(todoRepo.save(any(Todo.class))).thenAnswer(inv -> {
            Todo t = inv.getArgument(0, Todo.class);
            // mimic DB behavior
            t.setId(1L);
            return t;
        });

        // when
        TodoResponse res = service.create(req);

        // then
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.title()).isEqualTo("Write report");
        assertThat(res.description()).isEqualTo("CI/CD section first");
        assertThat(res.completed()).isFalse();

        // verify owner was set and saved
        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepo).save(captor.capture());
        assertThat(captor.getValue().getOwner()).isEqualTo(owner);
    }

    // ---------- listMine() ----------

    @Test
    void listMine_ShouldReturnTodosOfAuthenticatedUser() {
        // given
        var t1 = todo(2L, "A", "a", false, owner);
        var t2 = todo(3L, "B", "b", true, owner);
        when(todoRepo.findByOwnerIdOrderByCreatedAtDesc(owner.getId()))
                .thenReturn(List.of(t2, t1));

        // when
        var list = service.listMine();

        // then
        assertThat(list).hasSize(2);
        assertThat(list.get(0).id()).isEqualTo(3L);
        assertThat(list.get(1).id()).isEqualTo(2L);
        verify(todoRepo).findByOwnerIdOrderByCreatedAtDesc(101L);
    }

    // ---------- get() ----------

    @Test
    void get_ShouldReturnWhenOwnedByCurrentUser() {
        var t = todo(5L, "Read", "docs", false, owner);
        when(todoRepo.findByIdAndOwnerId(5L, owner.getId())).thenReturn(Optional.of(t));

        var res = service.get(5L);

        assertThat(res.id()).isEqualTo(5L);
        assertThat(res.title()).isEqualTo("Read");
        verify(todoRepo).findByIdAndOwnerId(5L, 101L);
    }

    @Test
    void get_ShouldThrowWhenTodoNotFound() {
        when(todoRepo.findByIdAndOwnerId(9L, owner.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Todo not found");
    }

    // ---------- update() ----------

    @Test
    void update_ShouldPatchOnlyProvidedFields() {
        // existing todo
        var existing = todo(7L, "Old", "desc", false, owner);
        when(todoRepo.findByIdAndOwnerId(7L, owner.getId())).thenReturn(Optional.of(existing));
        when(todoRepo.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        // patch: completed only
        var req1 = new UpdateTodoRequest(null, null, true);
        var res1 = service.update(7L, req1);
        assertThat(res1.completed()).isTrue();
        assertThat(res1.title()).isEqualTo("Old");
        assertThat(res1.description()).isEqualTo("desc");

        // patch: title & description
        var req2 = new UpdateTodoRequest("New", "updated", null);
        var res2 = service.update(7L, req2);
        assertThat(res2.title()).isEqualTo("New");
        assertThat(res2.description()).isEqualTo("updated");
        assertThat(res2.completed()).isTrue(); // unchanged from prior update
    }

    @Test
    void update_ShouldThrowWhenTodoMissing() {
        when(todoRepo.findByIdAndOwnerId(99L, owner.getId())).thenReturn(Optional.empty());
        var req = new UpdateTodoRequest("X", null, null);
        assertThatThrownBy(() -> service.update(99L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Todo not found");
    }

    // ---------- delete() ----------

    @Test
    void delete_ShouldRemoveWhenOwnedByUser() {
        var t = todo(11L, "Remove me", null, false, owner);
        when(todoRepo.findByIdAndOwnerId(11L, owner.getId())).thenReturn(Optional.of(t));

        service.delete(11L);

        verify(todoRepo).delete(t);
    }

    @Test
    void delete_ShouldThrowWhenTodoMissing() {
        when(todoRepo.findByIdAndOwnerId(12L, owner.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(12L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Todo not found");
    }

    private static Todo todo(Long id, String title, String desc, boolean completed, User owner) {
        var t = new Todo();
        t.setId(id);
        t.setTitle(title);
        t.setDescription(desc);
        t.setCompleted(completed);
        t.setOwner(owner);
        // timestamps (simulated)
        try {
            var created = Todo.class.getDeclaredField("createdAt");
            created.setAccessible(true);
            created.set(t, Instant.parse("2025-01-01T00:00:00Z"));
            var updated = Todo.class.getDeclaredField("updatedAt");
            updated.setAccessible(true);
            updated.set(t, Instant.parse("2025-01-01T00:00:00Z"));
        } catch (Exception ignored) {}
        return t;
    }
}
