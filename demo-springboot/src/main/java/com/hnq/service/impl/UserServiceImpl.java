package com.hnq.service.impl;

import com.hnq.component.Translator;
import com.hnq.dto.request.AddressDTO;
import com.hnq.dto.request.UserRequestDTO;
import com.hnq.dto.response.PageResponse;
import com.hnq.dto.response.UserDetailResponse;
import com.hnq.exception.ResourceNotFoundException;
import com.hnq.model.Address;
import com.hnq.model.User;
import com.hnq.repository.SearchRepository;
import com.hnq.repository.UserRepository;
import com.hnq.service.UserService;
import com.hnq.util.UserStatus;
import com.hnq.util.UserType;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
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
