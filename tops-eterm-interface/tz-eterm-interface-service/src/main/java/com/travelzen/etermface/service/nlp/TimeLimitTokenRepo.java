package com.travelzen.etermface.service.nlp;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.travelzen.framework.core.exception.SException;
import com.travelzen.framework.core.util.SearchUtil;
import com.travelzen.framework.core.util.SearchUtil.SearchResultType;

public class TimeLimitTokenRepo {

	private static Logger logger = LoggerFactory.getLogger(TimeLimitTokenRepo.class);

	public TimeLimitTokenRepo() {
		super();
	}

	List<TimeLimitToken> lsTimeLimitToken = Lists.newArrayList() ;;

	Map<TimeLimitTokenType, List<Integer>> tkType2tkIdxsMap = Maps.newConcurrentMap();

	Map<TimeLimitTokenRole, TreeSet<Integer>> tkRole2tkIdxsMap = Maps.newConcurrentMap();

	public Optional<String> getOneTokenImageWithRole(TimeLimitTokenRole role) {

		Preconditions.checkState(lsTimeLimitToken.size() > 0, "tklist size must>0");

		TreeSet<Integer> tks = tkRole2tkIdxsMap.get(role);
		if (tks != null && tks.size() > 0) {
			int tkIdx = tks.iterator().next();
			TimeLimitToken tk = lsTimeLimitToken.get(tkIdx);
			return Optional.fromNullable(tk.image);

		} else {
			return Optional.absent();
		}

	}

	public Map<TimeLimitTokenRole, TreeSet<Integer>> getRolesTags() {
		return tkRole2tkIdxsMap;
	}

	public void addToken(TimeLimitToken tk) {
		lsTimeLimitToken.add(tk);

		if (!tkType2tkIdxsMap.containsKey(tk.tokenType)) {
			tkType2tkIdxsMap.put(tk.tokenType, Lists.<Integer> newCopyOnWriteArrayList());
		}

		tkType2tkIdxsMap.get(tk.tokenType).add(tk.tkIdx);
	}

	public void updateTokenRole2TermPositionSet(TimeLimitToken tk) {

		TreeSet<Integer> tkRoles = tkRole2tkIdxsMap.get(tk.tokenRole);

		if (tkRoles == null) {
			tkRole2tkIdxsMap.put(tk.tokenRole, Sets.<Integer> newTreeSet());
			tkRoles = tkRole2tkIdxsMap.get(tk.tokenRole);
		}

		tkRoles.add(tk.tkIdx);
	}

	/**
	 * find the 1st appear of a token in a repo
	 * 
	 * @return
	 */
	public boolean findTk1st(TimeLimitTokenType tkTye, Mutable<TimeLimitToken> result) {

		List<Integer> tkList = tkType2tkIdxsMap.get(tkTye);

		if (tkList != null && tkList.size() > 0 && lsTimeLimitToken.size() > 0) {
			result.setValue(lsTimeLimitToken.get(tkList.get(0)));
			return true;
		}
		return false;
	}

	private TimeLimitToken tkfindInternal(TimelimitTokenQuery query) {

		SearchDirection direction = query.searchDirection;

		List<Integer> tkList = tkType2tkIdxsMap.get(query.getQueryTokenType());

		if (tkList == null) {
			throw new SException("tkfind exception: token unexist:" + query.getQueryTokenType());
		}

		// D2 may be unexist
		Triplet<Integer, Integer, SearchResultType> ret = SearchUtil.typedBinarySearch(tkList, query.targetIdx);

		if (ret.getValue2() == SearchResultType.BIGGER_THAN_HIGH || ret.getValue2() == SearchResultType.SMALLER_THAN_LOW) {

			int tkIdx = -1;
			if (ret.getValue2() == SearchResultType.BIGGER_THAN_HIGH) {
				tkIdx = ret.getValue0();
			}

			if (ret.getValue2() == SearchResultType.SMALLER_THAN_LOW) {
				tkIdx = ret.getValue1();
			}

			int timeLimitTokenIdx = tkList.get(tkIdx);

			if (direction == SearchDirection.FORWORD && timeLimitTokenIdx < query.targetIdx) {
				throw new SException(NLPErrorCode.NO_FORWARD);
			}

			if (direction == SearchDirection.BACKWORD && timeLimitTokenIdx > query.targetIdx) {
				throw new SException(NLPErrorCode.NO_BACKWARD);
			}

			if (query.judgeSpan(timeLimitTokenIdx)) {
				return lsTimeLimitToken.get(timeLimitTokenIdx);
			} else {
				throw new SException(NLPErrorCode.EXCEED_SPAN);
			}
		}

		if (ret.getValue2() == SearchResultType.BIGGER_THAN_HIGH) {
			int timeLimitTokenIdx = ret.getValue0();

			if (query.judgeSpan(timeLimitTokenIdx)) {
				return lsTimeLimitToken.get(timeLimitTokenIdx);
			} else {
				throw new SException(NLPErrorCode.EXCEED_SPAN);
			}
		}
		// end hasn't check span

		if (ret.getValue2() == SearchResultType.NORMAL) {

			int low = ret.getValue0();
			int high = ret.getValue1();

			int timeLimitTokenIdx = 0;

			if (direction == SearchDirection.BACKWORD) {
				if (low >= 0) {
					timeLimitTokenIdx = (Integer) tkList.get(low);
				}
			}

			if (direction == SearchDirection.FORWORD) {
				if (high < tkList.size()) {

					timeLimitTokenIdx = (Integer) tkList.get(high);

					if (query.judgeSpan(timeLimitTokenIdx)) {
						return lsTimeLimitToken.get(timeLimitTokenIdx);
					} else {
						throw new SException(NLPErrorCode.EXCEED_SPAN);
					}
				}
			}

			return lsTimeLimitToken.get(timeLimitTokenIdx);

		}

		if (ret.getValue2() == SearchResultType.FOUND) {
			int tkIdx = ret.getValue0();

			int timeLimitTokenIdx = (Integer) tkList.get(tkIdx);

			if (query.judgeSpan(timeLimitTokenIdx)) {
				return lsTimeLimitToken.get(timeLimitTokenIdx);
			} else {
				throw new SException(NLPErrorCode.EXCEED_SPAN);
			}

		}

		throw new SException("tkfind exception");

	}

	public boolean tkfind(TimelimitTokenQuery query, Mutable<TimeLimitToken> result) {

		try {
			TimeLimitToken tk = tkfindInternal(query);
			if (tk == null) {
				return false;
			} else {
				result.setValue(tk);
				return true;
			}
		} catch (SException e) {
			// logger.error(TZUtil.stringifyException(e));
			return false;
		}
	}

	public boolean tkfindNear(TimeLimitTokenType tkTye, int tkIdx, Mutable<TimeLimitToken> result, SearchDirectionPriority sdp) {

		TimelimitTokenQuery queryForward = TimelimitTokenQuery.createForwardTokenQuery(this, tkTye, tkIdx);
		TimelimitTokenQuery queryBackward = TimelimitTokenQuery.createBackwardTokenQuery(this, tkTye, tkIdx);

		List<TimelimitTokenQuery> qlist = Lists.newArrayList();

		if (sdp == SearchDirectionPriority.FORWORD_FIRST) {
			qlist.add(queryForward);
			qlist.add(queryBackward);
		}

		if (sdp == SearchDirectionPriority.BACKWORD_FIRST) {
			qlist.add(queryBackward);
			qlist.add(queryForward);
		}

		for (TimelimitTokenQuery query : qlist) {
			if (tkfind(query, result)) {
				return true;
			}
		}

		return false;

	}

	@Override
	public String toString() {

		List<String> buf = Lists.newArrayList();
		for (TimeLimitToken tk : lsTimeLimitToken) {
			buf.add(tk.tkIdx + ":" + tk.tokenType + "|" + tk.image);
		}

		return StringUtils.join(buf, "\n");
	}

}
