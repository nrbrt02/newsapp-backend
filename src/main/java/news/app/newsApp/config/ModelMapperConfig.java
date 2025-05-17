package news.app.newsApp.config;

import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configure ModelMapper for strict matching
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        
        // Skip Hibernate PersistentCollections that haven't been initialized
        Condition<?, ?> skipUninitialized = (ctx) -> 
            !(ctx.getSource() instanceof PersistentCollection) || 
            ((PersistentCollection<?>) ctx.getSource()).wasInitialized();
            
        modelMapper.getConfiguration().setPropertyCondition(skipUninitialized);
        
        return modelMapper;
    }
}