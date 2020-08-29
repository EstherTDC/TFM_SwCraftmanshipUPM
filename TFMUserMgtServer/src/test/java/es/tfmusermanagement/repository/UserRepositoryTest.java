package es.tfmusermanagement.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.tfm.usermanagement.Application;
import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;

import static org.junit.Assert.assertNull;
import static org.assertj.core.api.Assertions.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@DataJpaTest
public class UserRepositoryTest {
  
    @Autowired
    private UserRepository userRepository;

    
    @Test
    public void testGivenTwoDefinedUsers_WhenFindByUserName_ThenReturnsUserMatchingName() throws Exception {
        //Given
    	GameUser user1 = new GameUser("User1");
    	GameUser user2 = new GameUser("User2");
    	
    	GameUser saveduser1 = userRepository.save(user1);
    	GameUser saveduser2 = userRepository.save(user2);

        //When
    	GameUser GameUser = userRepository.findByUserName("User1");
        
        //Then
        assertThat(GameUser).isEqualTo(saveduser1);
    }
    
    @Test
    public void testGivenTwoUsersAreDefinedNoneOfThemMatching_thenFindByUserNameReturnsNull() throws Exception {
        //Given
    	GameUser user1 = new GameUser("User1");
    	GameUser user2 = new GameUser("User2");
    	
    	userRepository.save(user1);
    	userRepository.save(user2);
        
        //When/Then
        assertNull(userRepository.findByUserName("User3"));
    }
}
