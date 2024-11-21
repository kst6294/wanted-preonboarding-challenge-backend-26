package wanted.marketapi;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.stereotype.Component;
import wanted.marketapi.domain.item.Item;
import wanted.marketapi.domain.item.State;
import wanted.marketapi.domain.member.Member;
import wanted.marketapi.repository.ItemRepository;
import wanted.marketapi.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        Item item1 = new Item();
        item1.setItemName("test_item_1");
        item1.setPrice(15000);
        item1.setState(State.판매중);

        Item item2 = new Item();
        item2.setItemName("test_item_2");
        item2.setPrice(20000);
        item2.setState(State.예약중);

        Item item3 = new Item();
        item3.setItemName("test_item_3");
        item3.setPrice(30000);
        item3.setState(State.완료);

//        itemRepository.save(item1);
//        itemRepository.save(item2);
//        itemRepository.save(item3);

        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("test!");
        member.setName("TESTER");

        memberRepository.save(member);
    }

}