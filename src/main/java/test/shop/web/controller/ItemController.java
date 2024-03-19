package test.shop.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import test.shop.domain.item.Book;
import test.shop.domain.item.Item;
import test.shop.web.form.item.BookForm;
import test.shop.web.service.ItemService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createBookForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/items/createItemForm";
        }

        itemService.saveItem(form);
        return "redirect:/items";
    }

    /**
     * 상품 목록
     */

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }
    /**
     * 상품 수정 폼
     */

    @GetMapping("/items/{itemid}/edit")
    public String updateItemForm(Model model, @PathVariable("itemId") Long itemid) {

        BookForm form = itemService.findOne(itemid);
        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /**
     * 상품 수정
     */
    @PostMapping("/items/{itemid}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, BindingResult bindingResult) {
        itemService.updateItem(form);
        return "redirect:/items";
    }
}
