package com.vish.pms.service.serviceimpl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vish.pms.entity.User;
import com.vish.pms.exception.UserNotFoundException;
import com.vish.pms.repository.UserRepository;
import com.vish.pms.service.CrudService;
import com.vish.pms.specification.UserSpecification;

@Service
public class UserService implements CrudService<User, UUID> {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User entity) {
        // TODO Auto-generated method stub
        entity.setPassword((passwordEncoder.encode(entity.getPassword())));
        return userRepository.save(entity);
    }

    @Override
    public User update(UUID id, User entity) {
        // TODO Auto-generated method stub
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not foudn with the provided id"));
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setPassword(passwordEncoder.encode(entity.getPassword()));
        user.setRole(entity.getRole());

        return userRepository.save(user);

    }

    @Override
    public User getByID(UUID id) {
        // TODO Auto-generated method stub
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
	public List<User> getAll(int pageNo, int size, String sortBy, String direction) {
		// TODO Auto-generated method stub
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() :Sort.by(sortBy).ascending();
		Pageable pag = PageRequest.of(pageNo, size,sort);
        return userRepository.findAll(pag).getContent();
	}

    @Override
    public void deleteById(UUID id) {
        // TODO Auto-generated method stub
         // TODO Auto-generated method stub
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        userRepository.deleteById(id);
    }

    public List<User> searchUsers(String email, String name, String role, int page, int size, String sortBy, String sortDir){
        Specification<User> spec = Specification
                                    .where(UserSpecification.hasEmail(email))
                                    .and(UserSpecification.hasName(name))
                                    .and(UserSpecification.hasRole(role));
        Sort sort = sortDir.toLowerCase().equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(spec, pageable).getContent();
    }

    public List<User> createAll(List<User> users)
    {
        if(users == null || users.isEmpty())
            throw new IllegalArgumentException("Users list cannot be empty.");
        return userRepository.saveAll(users);
    }

}
