package edu.miu;

import javax.swing.text.html.Option;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface UtilPaste {


    BiFunction<List<User>,Long, List<Paste>> getPastesWithHighestFeedback=
            (user,kOfPastes)->user.stream()
                    .flatMap(u->u.getRoles().stream())
                    .filter(r-> r instanceof Member)
                    .map(r->(Member) r )
                    .flatMap(p->p.getPasteList().stream())
                    .flatMap(f->f.getFeedbacks().stream())
                    .collect(Collectors.groupingBy(Feedback::getPaste,Collectors.counting()))
                    .entrySet().stream()
                    .sorted((e1,e2)->(e2.getValue().intValue()-e1.getValue().intValue()))
                    .limit(kOfPastes)
                    .map(pastes->pastes.getKey())
                    .collect(Collectors.toList());
   // Comparator.comparing(Map.Entry::getValue)

/*edu.miu.TriFunction<edu.miu.User,Integer,Long,List<edu.miu.Language>> listTopUsedLanguagesPerYear=
        (user,kOfLanguages,year)->Stream.of(user)
                .filter(role->role.getRole() instanceof edu.miu.Member)
                .map(role->(edu.miu.Member) role.getRole())
                .flatMap(paste->paste.getPasteList().stream())
                .filter(paste->paste.getPasteDateTime().getYear()==year)
                .collect(Collectors.groupingBy(edu.miu.Paste::getLanguage,Collectors.counting()))
                .entrySet().stream()
                .sorted((e1,e2)->(e2.getValue().intValue()-e1.getValue().intValue()))
                .map(lang->lang.getKey())
                .limit(kOfLanguages)
                .collect(Collectors.toList());
//Comparator.comparing(Map.Entry::getValue)
*/

    Predicate<Role> isMember = r -> r instanceof Member;

    Function<Paste, Integer> getNumberOfPastViews = paste ->
            paste.getNumOfViews();

    TriFunction<List<User>, Integer, Integer, List<Optional<Paste>>> getTopKMostViewedPaste = (user, k, year) ->
            user.stream()
                    .flatMap(u -> u.getRoles().stream())
                    .filter(u -> isMember.test(u))
                    .map(u -> (Member) u)
                    .flatMap(u -> u.getPasteList().stream())
                    .filter(p -> p.getPasteDateTime().getYear() == year)
                    .peek(System.out::println)
                    .collect((Collectors.groupingBy(
                            Paste::getTitle,
                            Collectors.maxBy(Comparator.comparingInt(Paste::getNumOfViews)
                            ))))
                    .entrySet().stream()
                    .map(t -> t.getValue())
                    .limit(k)
                    .collect(Collectors.toList());


    //ABDI
    //Start
    Function<User, List<Member>> userToMember =
            (user) -> Stream.of(user)
                    .filter(role->role.getRoles() instanceof Member)
                    .map(role->(Member) role.getRoles()).collect(Collectors.toList());

    TriFunction<User,Integer,Long, List<Paste>> listOfKTopRatedPastesInAGivenYear =
            (user, k, year) -> userToMember.apply(user).stream()
                    .flatMap(paste -> paste.getPasteList().stream())
                    .filter(paste -> paste.getPasteDateTime().getYear() == year )
                    .flatMap(paste -> paste.getFeedbacks().stream())
                    .collect(Collectors.groupingBy(Feedback::getPaste))
                    .entrySet().stream()
                    .map(listEntry -> new Tuple<Paste, Long>(listEntry.getKey(), listEntry.getValue().stream().count()))
                    .sorted((o1, o2) -> o2.getValue().intValue() - o1.getValue().intValue())
                    .limit(k)
                    .map(memberLongTuple -> memberLongTuple.getKey())
                    .collect(Collectors.toList());

    TriFunction<User, Integer, Long, List<Member>> listActiveUserPerYear =
            (user,kOfUser,year)-> userToMember.apply(user).stream()
                    .flatMap(paste -> paste.getPasteList().stream())
                    .filter(paste -> paste.getPasteDateTime().getYear() == year )
                    .collect(Collectors.groupingBy(paste -> paste.getMemberId()))
                    .entrySet().stream()
                    .map(listEntry -> new Tuple<Member, Long>(listEntry.getKey(), listEntry.getValue().stream().count()))
                    .sorted((o1, o2) -> o2.getValue().intValue() - o1.getValue().intValue())
                    .limit(kOfUser)
                    .map(memberLongTuple -> memberLongTuple.getKey())
                    .collect(Collectors.toList());

    BiFunction<User, Integer, Optional<Month>> aMonthWithTheHighestPastInAGivenYear =
            (user, year)-> userToMember.apply(user).stream()
                    .flatMap(paste -> paste.getPasteList().stream())
                    .filter(paste -> paste.getPasteDateTime().getYear() == year )
                    .collect(Collectors.groupingBy(paste -> paste.getPasteDateTime().getMonth()))
                    .entrySet().stream()
                    .map(listEntry -> new Tuple<Month, Long>(listEntry.getKey(), listEntry.getValue().stream().count()))
                    .sorted((o1, o2) -> o2.getValue().intValue() - o1.getValue().intValue())
                    .limit(1)
                    .map(memberLongTuple -> memberLongTuple.getKey())
                    .findFirst();

    //End


}