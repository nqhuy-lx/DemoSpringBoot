package com.hnq.service.impl;

import com.hnq.dto.request.AddressDTO;
import com.hnq.dto.request.UserRequestDTO;
import com.hnq.dto.response.PageResponse;
import com.hnq.dto.response.UserDetailResponse;
import com.hnq.exception.ResourceNotFoundException;
import com.hnq.model.Address;
import com.hnq.model.User;
import com.hnq.repository.AddressRepository;
import com.hnq.repository.SearchRepository;
import com.hnq.repository.UserRepository;
import com.hnq.repository.specification.UserSpec;
import com.hnq.service.MailService;
import com.hnq.service.UserService;
import com.hnq.util.Gender;
import com.hnq.util.UserStatus;
import com.hnq.util.UserType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SearchRepository searchRepository;
    // private final MailService mailService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public UserDetailsService getUserDetailsService() {
        return username -> userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(UserStatus.INACTIVE)
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();

        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));

        userRepository.save(user);

        if(user.getId() != null){
            // send email confirm
            //mailService.sendConfirmLink(user.getEmail(), user.getId(), "secretCode");
            kafkaTemplate.send("confirm-account-topic", String.format("email=%s,id=%s,code=%s", user.getEmail(), user.getId(), "code@123"));
        }

        log.info("User has added successfully, userId={}", user.getId());

        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        userRepository.save(user);
        log.info("User has updated successfully, userId={}", userId);
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);

        log.info("User status has changed successfully, userId={}", userId);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User has deleted successfully, userId={}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {

        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    @Override
    public PageResponse<?> getAllUsersWithSortBy(int page, int size, String sortBy) {
        int p = 0;
        if (page > 0) {
            p = page - 1;
        }
        if (sortBy == null) {
            sortBy = "id:desc";
        }
        List<Sort.Order> sorts = new ArrayList<>();
        //firstName:asc|desc
        Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
        Matcher matcher = pattern.matcher(sortBy);
        if (matcher.find()) {
            if (matcher.group(3).equalsIgnoreCase("asc")) {
                sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
            } else
                sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
        }

        Pageable pageable = PageRequest.of(p, size, Sort.by(sorts)); // field trong user model not column
        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailResponse> responses = users.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(users.getTotalPages())
                .items(responses)
                .build();

    }

    @Override
    public PageResponse<?> getAllUsersWithSortByAndSearch(int page, int size, String sortBy, String search) {
        return searchRepository.getAllUsersWithSortByAndSearch(page, size, sortBy, search);
    }

    @Override
    public PageResponse<?> advanceSearchWithCriteria(int page, int size, String sortBy, String... search) {
        return searchRepository.advanceSearchUser(page, size, sortBy, search);
    }

    @Override
    public PageResponse<?> advanceSearchWithSpecification(Pageable pageable, String[] user, String[] address) {
        if(user != null && address != null){
            // join
        } else if(user != null){
            /* search in user, not join in address */
            Specification<User> specFirstName = UserSpec.hasFirstName("Huy");
            Specification<User> specGender = UserSpec.notEqualGender(Gender.FEMALE);
            Specification<User> specFinal = specFirstName.and(specGender);
            // Specification<User> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%Huy%"));
            List<User> list = userRepository.findAll(specFinal);
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPage(10)
                    .items(list)
                    .build();
        }


        Page<User> users = userRepository.findAll(pageable);
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .items(users.stream().toList())
                .build();
    }

    @Override
    public void confirmUser(int userId, String secretCode) {
        User user = getUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.info("User has confirmed successfully, userId={}, secretCode = {}", userId, secretCode);
    }


    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
