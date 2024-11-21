package wanted.market.testdata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wanted.market.item.domain.entity.Item;
import wanted.market.item.repository.ItemRepository;
import wanted.market.item.service.ItemService;
import wanted.market.member.repository.MemberRepository;


@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemService itemService;
    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {

    }

}