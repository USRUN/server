package com.usrun.backend.utility;

import com.usrun.backend.model.User;
import com.usrun.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UniqueIDGenerator {

    @Autowired
    private UserRepository userRepository;

    public void generateID(User toGrantID){
        StringBuffer resultIDBuffer = new StringBuffer();
        int randomLength;
        int codeLength;

        if(toGrantID.getEmail().contains("@student.hcmus.edu.vn")) {
            resultIDBuffer.append("SKHTN");
            randomLength = 5;
            codeLength = 5;
        } else{
            resultIDBuffer.append("N");
            randomLength = 9;
            codeLength= 1;
        }

        if(userRepository.existsByCode("N84afb591-")){
            System.out.println("code existed");
        }

        resultIDBuffer.append(UUID.randomUUID().toString().replace("-", ""), 0, randomLength);

        while (userRepository.existsByEmail(resultIDBuffer.toString())){
            resultIDBuffer.delete(codeLength,codeLength + randomLength);
            resultIDBuffer.append(UUID.randomUUID().toString().replace("-", ""), 0, randomLength);
        }

        toGrantID.setCode(resultIDBuffer.toString());
    }
}
