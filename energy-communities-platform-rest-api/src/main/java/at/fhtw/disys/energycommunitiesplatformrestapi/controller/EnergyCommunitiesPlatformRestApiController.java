package at.fhtw.disys.energycommunitiesplatformrestapi.controller;

import at.fhtw.disys.energycommunitiesplatformrestapi.dto.BookDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EnergyCommunitiesPlatformRestApiController {
    private final List<BookDto> bookDToList = new ArrayList<>(
            List.of(
                    new BookDto( 1,"test1","fantasy"),
                    new BookDto( 1,"test2","fantasy"),
                    new BookDto( 1,"test3","horror")
            )
    );
    @GetMapping("/books")
    public List<BookDto> getMainTest() {
        return bookDToList;
    }
    @GetMapping("/books/{id}")
    public BookDto getBook(
            @PathVariable int id
    ) {
        Optional<BookDto> optbooks = bookDToList.stream().filter((bookDTo -> bookDTo.getId() == id)).findFirst();
        if(optbooks.isEmpty()){
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        return optbooks.get();
    }

    @PostMapping("/books")
    public void createBook(@RequestBody BookDto bookToCreate) {
        bookDToList.add(
                bookToCreate
        );
    }

    @DeleteMapping ("/books/{id}")
    public void deleteBook(
            @PathVariable int id
    ) {
        Optional<BookDto> optbook = bookDToList.stream().filter((bookDTo -> bookDTo.getId() == id)).findFirst();
        if(optbook.isEmpty()){
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        bookDToList.remove(optbook.get());
    }
}
