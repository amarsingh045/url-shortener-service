package com.schwab.infrastructure.persistence;

import com.schwab.domain.ShortUrl;
import com.schwab.exception.ShortCodeAlreadyExistsException;
import com.schwab.infrastructure.mapper.ShortUrlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaShortUrlRepositoryAdapterTest {

    @Mock
    private ShortUrlEntityRepository repository;

    @Mock
    private ShortUrlMapper mapper;

    private JpaShortUrlRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaShortUrlRepositoryAdapter(repository, mapper);
    }

    @Test
    void shouldSaveAndCacheMappedDomain() {
        ShortUrl domain = new ShortUrl("a1", "https://example.com/a1");
        ShortUrlEntity entity = new ShortUrlEntity("a1", "https://example.com/a1", 0);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        ShortUrl saved = adapter.save(domain);

        assertEquals("a1", saved.getShortCode());

        Optional<ShortUrl> found = adapter.findByShortCode("a1");
        assertTrue(found.isPresent());
        verify(repository, never()).findById("a1");
    }

    @Test
    void shouldTranslateDuplicateInsertToCollisionException() {
        ShortUrl domain = new ShortUrl("dup1", "https://example.com/dup");
        ShortUrlEntity entity = new ShortUrlEntity("dup1", "https://example.com/dup", 0);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(ShortCodeAlreadyExistsException.class, () -> adapter.save(domain));
    }

    @Test
    void shouldLoadFromRepositoryOnCacheMiss() {
        ShortUrl domain = new ShortUrl("b2", "https://example.com/b2");
        ShortUrlEntity entity = new ShortUrlEntity("b2", "https://example.com/b2", 0);

        when(repository.findById("b2")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<ShortUrl> found = adapter.findByShortCode("b2");

        assertTrue(found.isPresent());
        verify(repository, times(1)).findById("b2");
    }

    @Test
    void shouldHandleExistsByShortCodeBranches() {
        ShortUrl domain = new ShortUrl("c3", "https://example.com/c3");
        ShortUrlEntity entity = new ShortUrlEntity("c3", "https://example.com/c3", 0);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        adapter.save(domain);
        assertTrue(adapter.existsByShortCode("c3"));
        verify(repository, never()).existsById("c3");

        when(repository.existsById("d4")).thenReturn(true);
        when(repository.findById("d4")).thenReturn(Optional.of(new ShortUrlEntity("d4", "https://example.com/d4", 0)));
        when(mapper.toDomain(any(ShortUrlEntity.class))).thenReturn(new ShortUrl("d4", "https://example.com/d4"));

        assertTrue(adapter.existsByShortCode("d4"));

        when(repository.existsById("e5")).thenReturn(false);
        assertTrue(!adapter.existsByShortCode("e5"));
    }

    @Test
    void shouldMapCountAndFindAll() {
        ShortUrlEntity e1 = new ShortUrlEntity("x1", "https://example.com/x1", 0);
        ShortUrlEntity e2 = new ShortUrlEntity("x2", "https://example.com/x2", 2);
        ShortUrl d1 = new ShortUrl("x1", "https://example.com/x1");
        ShortUrl d2 = new ShortUrl("x2", "https://example.com/x2");
        d2.setRedirectCount(2);

        when(repository.count()).thenReturn(2L);
        when(repository.findAll()).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        assertEquals(2L, adapter.count());
        int size = 0;
        for (ShortUrl ignored : adapter.findAll()) {
            size++;
        }
        assertEquals(2, size);
    }
}
