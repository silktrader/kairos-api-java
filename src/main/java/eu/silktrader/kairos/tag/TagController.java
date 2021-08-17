package eu.silktrader.kairos.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tags")
public class TagController {

  private final TagService tagService;

  @Autowired
  public TagController(TagService tagService) {
    this.tagService= tagService;
  }

  @GetMapping
  public Iterable<TagDto> getTags() {
    // implicit 200 response
    return this.tagService.getTags();
  }

  @GetMapping("/{id}")
  public TagDto getTag(@PathVariable Long id) {
    return this.tagService.getTag(id);
  }

  @PostMapping
  public ResponseEntity<TagDto> addTag(@RequestBody TagDto tagDto) {
    return new ResponseEntity<>(this.tagService.add(tagDto), HttpStatus.CREATED);
  }

  // returns 200 status by default, which seems acceptable
  @DeleteMapping("/{id}")
  public void deleteTag(@PathVariable("id") Long id) {
    this.tagService.delete(id);
  }
  
}
