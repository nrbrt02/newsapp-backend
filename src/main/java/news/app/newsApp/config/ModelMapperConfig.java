package news.app.newsApp.config;

import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configure property mappings
        modelMapper.getConfiguration()
            .setSkipNullEnabled(true)
            .setAmbiguityIgnored(true)
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setPropertyCondition(context -> {
                // Skip uninitialized lazy-loaded collections
                if (context.getSource() instanceof PersistentCollection) {
                    return ((PersistentCollection<?>) context.getSource()).wasInitialized();
                }
                return context.getSource() != null;
            });
            
        return modelMapper;
    }
}