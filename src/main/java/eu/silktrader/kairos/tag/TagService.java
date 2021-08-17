package eu.silktrader.kairos.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import eu.silktrader.kairos.auth.AuthService;
import eu.silktrader.kairos.exception.ItemNotFoundException;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final AuthService authService;

  @Autowired
    public TagService(TagRepository tagRepository, AuthService authService) {
      this.tagRepository = tagRepository;
      this.authService = authService;
    }
  

  public List<TagDto> getTags() {
    var user = this.authService.getCurrentUser();
    return tagRepository.findByUser(user).stream().map(this::map).toList();
  }

  public TagDto getTag(Long id) {
    return map(tagRepository.findById(id).orElseThrow(ItemNotFoundException::new));
  }

  public TagDto add(TagDto tagDto) {
    var tag = new Tag();
    tag.setUser(this.authService.getCurrentUser());
    tag.setTitle(tagDto.title());
    tag.setColour(tagDto.colour());
    tag.setDescription(tagDto.description());
    tag = this.tagRepository.save(tag);
    return map(tag);
  }

  public void delete(Long id) {
    try {
      tagRepository.deleteById(id);
    }
    catch(EmptyResultDataAccessException e) {
      throw new ItemNotFoundException();
    }
  }
  
  private TagDto map(Tag tag) {
    return new TagDto(tag.getId(), tag.getTitle(), tag.getDescription(), tag.getColour());
  }

}
