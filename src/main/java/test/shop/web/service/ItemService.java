package test.shop.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.item.Book;
import test.shop.domain.item.Item;
import test.shop.web.form.item.BookForm;
import test.shop.web.form.item.ItemDto;
import test.shop.web.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

import static test.shop.domain.item.QItem.item;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(BookForm form) {
        Book book = new Book(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());
        itemRepository.save(book);
        return book.getId();
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public BookForm findOne(Long itemId) {
        Book book = (Book)itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item doesn't exist"));
        return new BookForm(book.getId(), book.getName(), book.getPrice(), book.getStockQuantity(), book.getAuthor(), book.getIsbn());
    }

    @Transactional
    public void updateItem(BookForm form) {
        Book book = (Book) itemRepository.findItemById(form.getId());
        book.updateBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());
        itemRepository.save(book);

    }
}
