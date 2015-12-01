package com.yahoo.semsearch.fastlinking.entityranker;

import com.yahoo.semsearch.fastlinking.hash.QuasiSuccinctEntityHash;
import com.yahoo.semsearch.fastlinking.view.CandidatesInfo;
import com.yahoo.semsearch.fastlinking.view.Entity;
import com.yahoo.semsearch.fastlinking.view.EntityContext;

/**
 * Ranks entities using NPMI
 * 
 * @author roi
 *
 */
public class NPMIRanker extends ProbabilityRanker {
    public NPMIRanker( QuasiSuccinctEntityHash hash ) {
	super( hash );
    }

    /**
     * NPMI = ln(p(x,y)/p(x)p(y)) / -ln(p(x,y))
     */
    @Override
    public double rank( Entity e, CandidatesInfo info, EntityContext context, String surfaceForm, int length ) {
	double totalClicks = stats.SQEF;
	double totalAnchor = stats.SLET; //w/out this it doesn't work!
	double p_e_q = ( e.QEF ) / ( totalClicks );// +1 smoothing TODO is this wrong? same in probability ranker	
	double p_e_w = ( e.LET ) / ( totalAnchor );
	double p_a_w = info.LAT / totalAnchor;
	double p_a_q = info.QAF / totalClicks;

	double p_ea_q = e.QAEF / totalClicks;
	double p_ea_w = e.LAET / totalAnchor;
	double score = ( pAnchor * ( Math.log( ( p_ea_w / ( p_e_w * p_a_w ) ) ) / -Math.log( p_ea_w ) ) + pQuery
		* ( Math.log( ( p_ea_q / ( p_e_q * p_a_q ) ) ) / -Math.log( p_ea_q ) ) );

	if ( e.QAEF == 0 ) {
	    if ( e.LAET == 0 ) return -1;
	    score = pAnchor * ( Math.log( ( p_ea_w / ( p_e_w * p_a_w ) ) ) / -Math.log( p_ea_w ) );
	} else if ( e.LAET == 0 ) {
	    score = pQuery * ( Math.log( ( p_ea_q / ( p_e_q * p_a_q ) ) ) / -Math.log( p_ea_q ) );
	}
	return score;
    }
}