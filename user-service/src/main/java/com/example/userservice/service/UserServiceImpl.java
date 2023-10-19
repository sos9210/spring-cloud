package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private Environment env;
    private RestTemplate restTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,Environment env, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
    }
    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        //userDto와 returnUserDto는 같은 인스턴스를 가르킴
        UserDto returnUserDto = mapper.map(userDto, UserDto.class);
        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(
                () -> new UsernameNotFoundException("찾을 수 없는 사용자입니다."));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

//        List<ResponseOrder> orders = new ArrayList<>();

        /*RestTemplate사용한 방법*/
        //url주소 , http메서드타입, 파라미터, 반환받을 타입
        String orderUrl = String.format(env.getProperty("order_service.url"),userId);
        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(
                orderUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ResponseOrder>>() {
        });

        List<ResponseOrder> orders = orderListResponse.getBody();
        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public List<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("찾을 수 없는 사용자입니다."));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException(username));

        return new User(userEntity.getEmail(),userEntity.getEncryptedPwd(),
                true,true,true,true,new ArrayList<>());
    }
}
