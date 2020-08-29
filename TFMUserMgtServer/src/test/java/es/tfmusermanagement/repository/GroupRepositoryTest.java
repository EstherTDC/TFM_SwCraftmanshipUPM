package es.tfmusermanagement.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.tfm.usermanagement.Application;
import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNull;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@DataJpaTest
public class GroupRepositoryTest {
  
    @Autowired
    private GroupRepository groupRepository;

    
    @Test
    public void testGivenTwoGroupsAreDefined_WhenFindByGroupName_ThenReturnsGroupMatchingName() throws Exception {
        //Given
    	UserGroup group1 = new UserGroup("Group1");
    	UserGroup group2 = new UserGroup("Group2");
    	
    	UserGroup savedGroup1 = groupRepository.save(group1);
    	UserGroup savedGroup2 = groupRepository.save(group2);

        //When
        UserGroup userGroup = groupRepository.findByGroupName("Group2");
        
        //Then
        assertThat(userGroup).isEqualTo(savedGroup2);

    }
    
    @Test
    public void testGivenTwoGroupsAreDefinedNoneOfThemMatching_WhenFindByGroupName_ThenReturnsNull() throws Exception {
        //Given
    	UserGroup group1 = new UserGroup("Group1");
    	UserGroup group2 = new UserGroup("Group2");
    	
    	groupRepository.save(group1);
    	groupRepository.save(group2);
        
        //When/Then
        assertNull(groupRepository.findByGroupName("Group3"));
    }
}
