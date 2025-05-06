package news.app.newsApp.service;

import news.app.newsApp.dto.TagDto;
import news.app.newsApp.dto.TagRequest;
import news.app.newsApp.exception.ResourceAlreadyExistsException;
import news.app.newsApp.exception.ResourceNotFoundException;
import news.app.newsApp.model.Tag;
import news.app.newsApp.model.User;
import news.app.newsApp.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> modelMapper.map(tag, TagDto.class))
                .collect(Collectors.toList());
    }

    public TagDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        return modelMapper.map(tag, TagDto.class);
    }

    @Transactional
    public TagDto createTag(TagRequest tagRequest) {
        User currentUser = getCurrentUser();
        
        // Only admin or writer can create tags
        if (!currentUser.getRole().equals(User.Role.ADMIN) && !currentUser.getRole().equals(User.Role.WRITER)) {
            throw new AccessDeniedException("Only administrators and writers can create tags");
        }

        if (tagRepository.existsByName(tagRequest.getName())) {
            throw new ResourceAlreadyExistsException("Tag with name " + tagRequest.getName() + " already exists");
        }

        Tag tag = new Tag();
        tag.setName(tagRequest.getName());

        Tag savedTag = tagRepository.save(tag);
        return modelMapper.map(savedTag, TagDto.class);
    }

    @Transactional
    public TagDto updateTag(Long id, TagRequest tagRequest) {
        User currentUser = getCurrentUser();
        
        // Only admin can update tags
        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new AccessDeniedException("Only administrators can update tags");
        }

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));

        // Check if the new name already exists for another tag
        if (!tag.getName().equals(tagRequest.getName()) && 
                tagRepository.existsByName(tagRequest.getName())) {
            throw new ResourceAlreadyExistsException("Tag with name " + tagRequest.getName() + " already exists");
        }

        tag.setName(tagRequest.getName());

        Tag updatedTag = tagRepository.save(tag);
        return modelMapper.map(updatedTag, TagDto.class);
    }

    @Transactional
    public void deleteTag(Long id) {
        User currentUser = getCurrentUser();
        
        // Only admin can delete tags
        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new AccessDeniedException("Only administrators can delete tags");
        }

        tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        
        tagRepository.deleteById(id);
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((User) userService.getUserByUsername(userDetails.getUsername()));
    }
}