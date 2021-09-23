package edu.miu;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
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

    //Eyob
    //START
    BiFunction<List<User>,Long, List<Paste>> getPastesWithHighestFeedback=
            (user,kOfPastes)->user.stream()
                    .flatMap(u->u.getRoles().stream())
                    .filter(role->role instanceof Member)
                    .map(role->(Member) role )
                    .flatMap(member->member.getPasteList().stream())
                    .flatMap(fed->fed.getFeedbacks().stream())
                    .collect(Collectors.groupingBy(Feedback::getPaste,Collectors.counting()))
                    .entrySet().stream()
                    .sorted((e1,e2)->(e2.getValue().intValue()-e1.getValue().intValue()))
                    .limit(kOfPastes)
                    .map(pastes->pastes.getKey())
                    .collect(Collectors.toList());
   // Comparator.comparing(Map.Entry::getValue)

TriFunction<List<User>, Integer, Integer, List<Language>> listTopUsedLanguagesPerYear =
        (user, kOfLanguages, year) -> user.stream()
                .flatMap(us->us.getRoles().stream())
                .filter(role -> role instanceof Member)
                .map(role -> (Member) role)
                .flatMap(paste -> paste.getPasteList().stream())
                .filter(paste -> paste.getPasteDateTime().getYear() == year)
                .collect(Collectors.groupingBy(Paste::getLanguage, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> (e2.getValue().intValue() - e1.getValue().intValue()))
                .sorted((e1, e2) -> (e2.getKey().compareTo(e1.getKey())))
                .limit(kOfLanguages)
                .map(lang -> lang.getKey())
                .collect(Collectors.toList());

    TriFunction<User,Integer,Integer,List<Paste>>  getHighestPastesBySizeUsernameOngivenYear=
            (user,kOfPastes,year)-> Stream.of(user)
                    .flatMap(ur->ur.getRoles().stream())
                    .filter(user1->user1 instanceof Member)
                    .map(user2->(Member) user2)
                    .flatMap(meb->meb.getPasteList().stream())
                    .filter(paste->paste.getPasteDateTime().getYear()==year)
                    .sorted((paste1,paste2)->paste2.getContent().length()-paste1.getContent().length())
                    .limit(kOfPastes)
                    .collect(Collectors.toList());
    TriFunction<List<User>,Integer,Integer,List<Member>> getTopKMembersWithMostPastesOnaGivenYear=
            (users,k,year)->users.stream()
                    .flatMap(usrs->usrs.getRoles().stream())
                    .filter(role->role instanceof Member)
                    .map(meb->(Member) meb)
                    .flatMap(member -> member.getPasteList().stream())
                    .filter(pasts->pasts.getPasteDateTime().getYear()==year)
                    .collect(Collectors.groupingBy(Paste::getMember,Collectors.counting()))
                    .entrySet().stream()
                    .sorted((p1,p2)->p2.getValue().intValue()-p1.getValue().intValue())
                    .map(lspaste->lspaste.getKey())
                    .limit(k)
                    .collect(Collectors.toList());
//Comparator.comparing(Map.Entry::getValue)


    //END

    // SAMI
    // START
    Predicate<Role> isMember = r -> r instanceof Member;

    Function<Paste, Integer> getNumberOfPastViews = paste ->
            paste.getNumOfViews();

    Function<Paste, Integer> getRatingForPaste = paste ->
            paste.getRating();

    TriFunction<List<User>, Integer, Integer, List<Paste>> getTopKMostViewedPaste = (user, k, year) ->
            user.stream()
                    .flatMap(u -> u.getRoles().stream())
                    .filter(u -> isMember.test(u))
                    .map(u -> (Member) u)
                    .flatMap(u -> u.getPasteList().stream())
                    .filter(p -> p.getPasteDateTime().getYear() == year)
                    .sorted(Comparator.comparingInt(Paste::getNumOfViews).reversed())
                    .limit(k)
                    .collect(Collectors.toList());

    TriFunction<List<User>, Integer, Integer, List<Paste>> getTopKWorstRatedPaste = (user, k, year) ->
            user.stream()
                    .flatMap(u -> u.getRoles().stream())
                    .filter(u -> isMember.test(u))
                    .map(u -> (Member) u)
                    .flatMap(u -> u.getPasteList().stream())
                    .filter(p -> p.getPasteDateTime().getYear() == year)
                    .sorted((p1, p2) -> p1.getRating() - p2.getRating())
                    .limit(k)
                    .collect(Collectors.toList());

    TriFunction<List<User>, Integer, Integer, List<Paste>> getTopKRewardWithRateAndNumberOfViewedPaste = (user, k, year) ->
            user.stream()
                    .flatMap(u -> u.getRoles().stream())
                    .filter(u -> isMember.test(u))
                    .map(u -> (Member) u)
                    .flatMap(u -> u.getPasteList().stream())
                    .filter(p -> p.getPasteDateTime().getYear() == year)
                    .sorted(Comparator.comparingInt(Paste::getNumOfViews).reversed())
                    .sorted((p1, p2) -> (p2.getNumOfViews() + p2.getRating()) - (p1.getNumOfViews() + p1.getRating()))
                    .peek(System.out::println)
                    .limit(k)
                    .collect(Collectors.toList());

    // END


    //ABDI
    //Start

    Function<User, List<Member>> userToMembers =
            (user) -> Stream.of(user)
                    .filter(role->role.getRoles() instanceof Member)
                    .map(role->(Member) role.getRoles()).collect(Collectors.toList());

    Function<List<User>, List<Member>> usersToMembers =
            (users) -> users.stream()
                    .flatMap(u -> u.getRoles().stream())
                    .filter(u -> isMember.test(u))
                    .map(u -> (Member) u)
                    .collect(Collectors.toList());


    Function<Administrator, List<User>> administratorToUsers =
            (administrator) -> administrator.getUsers().stream()
                    .collect(Collectors.toList());


    TriFunction<Administrator,Integer,Long, List<Paste>> listOfKTopFeedbackPastesInAGivenYear =
            (admin, k, year) -> usersToMembers.apply(administratorToUsers.apply(admin)).stream()
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

//    TriFunction<Administrator,Integer,Long, List<Paste>> listOfKTopRatingPastesInAGivenYear =
//            (admin, k, year) -> usersToMembers.apply(administratorToUsers)
//                    .stream()
//                    .collect(Collectors.groupingBy(p -> p.getPasteList()))
//                    .entrySet()
//    TriFunction<Administrator,Integer,Long, List<Paste>> listOfKTopRatingPastesInAGivenYear =
//            (admin, k, year) -> usersToMembers.apply(administratorToUsers.apply(admin))
//                    .stream()
//                    .flatMap(paste -> paste.getPasteList().stream())
//                    .filter(paste -> paste.getPasteDateTime().getYear() == year )
//                    .flatMap(paste -> paste.getRating().stream())
//                    .collect(Collectors.groupingBy(Feedback::getPaste))
//                    .entrySet().stream()
//                    .map(listEntry -> new Tuple<Paste, Long>(listEntry.getKey(), listEntry.getValue().stream().count()))
//                    .sorted((o1, o2) -> o2.getValue().intValue() - o1.getValue().intValue())
//                    .limit(k)
//                    .map(memberLongTuple -> memberLongTuple.getKey())
//                    .collect(Collectors.toList());


    TriFunction<Administrator, Integer, Integer, List<Member>> listActiveUserPerYear =
            (admin, kOfUser, year) ->
                    Optional.ofNullable(usersToMembers.apply(administratorToUsers.apply(admin)).stream()
                    .flatMap(paste -> paste.getPasteList().stream())
                    .filter(paste -> paste.getPasteDateTime().getYear() == year )
                    .collect(Collectors.groupingBy(paste -> paste.getMemberId()))
                    .entrySet().stream()
                    .map(listEntry -> new Tuple<Member, Long>(listEntry.getKey(), listEntry.getValue().stream().count()))
                    .sorted((o1, o2) -> o2.getValue().intValue() - o1.getValue().intValue())
                    .limit(kOfUser)
                    .map(memberLongTuple -> memberLongTuple.getKey())
                    .collect(Collectors.toList())).orElse(null);

    BiFunction<User, Integer, Optional<Month>> aMonthWithTheHighestPastInAGivenYear =
            (user, year)-> userToMembers.apply(user).stream()
                    .flatMap(paste -> paste.getPasteList().stream())
                    .filter(paste -> paste.getPasteDateTime().getYear() == year )
                    .collect(Collectors.groupingBy(paste -> paste.getPasteDateTime().getMonth()))
                    .entrySet().stream()
                    .map(listEntry -> new Tuple<Month, Long>(listEntry.getKey(), listEntry.getValue().stream().count()))
                    .sorted((o1, o2) -> o2.getValue().intValue() - o1.getValue().intValue())
                    .limit(1)
                    .map(memberLongTuple -> memberLongTuple.getKey())
                    .findFirst();
    TriFunction<List<User>,Integer,Integer,List<Paste>> listKTotalExpiredPastesByGivenYear=
            (users,year,k)->users.stream()
                    .flatMap(ro->ro.getRoles().stream())
                    .filter(rol->rol instanceof Member)
                    .map((role->(Member)role))
                    .flatMap(meb->meb.getPasteList().stream())
                    .filter(pastes->pastes.getExpiryDateTime().getYear()==year)
                    .limit(k)
                    .collect(Collectors.toList());


    BiFunction<User ,Long,Long> TotalFeedbacksInaGiveYear=
            (user,year)-> userToMembers.apply(user).stream()
                    .flatMap(paste -> paste.getPasteList().stream())
                    .filter(paste -> paste.getPasteDateTime().getYear() == year )
                    .flatMap(fed->fed.getFeedbacks().stream())
                    .count();

    //End


}
