package eu.silktrader.kairos.tag;

import eu.silktrader.kairos.auth.AuthService;
import eu.silktrader.kairos.exception.ItemNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final AuthService authService;

  private final Random colourSeed = new Random(); // the seed will be lost on restarts, could store it in the database

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
    return map(
      tagRepository.findById(id).orElseThrow(ItemNotFoundException::new)
    );
  }

  public TagDto editTag(TagDto tagDto) {
    final Tag tag = tagRepository
      .findByIdAndUserName(tagDto.id(), authService.getCurrentUserName())
      .orElseThrow(ItemNotFoundException::new);

    tag.setTitle(tagDto.title());
    tag.setDescription(tagDto.description());
    tag.setColour(tagDto.colour());
    return map(tagRepository.save(tag));
  }

  public Optional<Tag> getTagByTitle(String title) {
    return tagRepository.findByTitle(title);
  }

  public List<Tag> getTagsByUserNameAndTitleIn(
    String userName,
    List<String> titles
  ) {
    return tagRepository.findByUserNameAndTitleIn(userName, titles);
  }

  public boolean exists(String title) {
    return tagRepository.existsByTitle(title);
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

  public Tag createTag(String title) {
    var tag = new Tag();
    tag.setTitle(title);
    tag.setUser(authService.getCurrentUser());

    // generate random colours with CSS compatible HSL strings, ideally different between each others and pastel looking
    // colours are meant to be edited subsequently by users
    tag.setColour((short) colourSeed.nextInt(361));
    return tagRepository.save(tag);
  }

  public void delete(Long id) {
    try {
      tagRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ItemNotFoundException();
    }
  }

  private TagDto map(Tag tag) {
    return new TagDto(
      tag.getId(),
      tag.getTitle(),
      tag.getDescription(),
      tag.getColour()
    );
  }
}
